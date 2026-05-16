package com.cfs.PaymentService.service;

import com.cfs.PaymentService.dto.CreateOrderRequest;
import com.cfs.PaymentService.dto.CreateOrderResponse;
import com.cfs.PaymentService.dto.PaymentVerificationRequest;
import com.cfs.PaymentService.entity.PaymentOrder;
import com.cfs.PaymentService.entity.PaymentStatus;
import com.cfs.PaymentService.model.Course;
import com.cfs.PaymentService.model.EnrollmentNotification;
import com.cfs.PaymentService.repository.PaymentOrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final CourseCatalogService courseCatalogService;
    private final KafkaTemplate<String, EnrollmentNotification> kafkaTemplate;
    private final PaymentOrderRepository paymentOrderRepository;
    private final String key;
    private final String secret;
    private final String kafkaTopic;

    public PaymentService(
            RazorpayClient razorpayClient,
            CourseCatalogService courseCatalogService,
            KafkaTemplate<String, EnrollmentNotification> kafkaTemplate,
            PaymentOrderRepository paymentOrderRepository,
            @Value("${app.razorpay.api.key-id}") String key,
            @Value("${app.razorpay.api.key-secret}") String secret,
            @Value("${app.kafka.topic}") String kafkaTopic
    ) {
        this.razorpayClient = razorpayClient;
        this.courseCatalogService = courseCatalogService;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentOrderRepository = paymentOrderRepository;
        this.key = key;
        this.secret = secret;
        this.kafkaTopic = kafkaTopic;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request) throws RazorpayException {
        Course course = courseCatalogService.findById(request.courseId());

        JSONObject notes = new JSONObject();
        notes.put("courseId", course.id());
        notes.put("email", request.email());

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", course.amountInPaise());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "course_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);
        orderRequest.put("notes", notes);

        Order order = razorpayClient.orders.create(orderRequest);

        // DB mein save karo — CREATED status
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setRazorpayOrderId(order.get("id"));
        paymentOrder.setCourseId(course.id());
        paymentOrder.setCourseTitle(course.title());
        paymentOrder.setStudentName(request.studentName());
        paymentOrder.setEmail(request.email());
        paymentOrder.setAmountInPaise(course.amountInPaise());
        paymentOrder.setStatus(PaymentStatus.CREATED);
        paymentOrder.setCreatedAt(LocalDateTime.now());
        paymentOrderRepository.save(paymentOrder);

        return new CreateOrderResponse(
                key,
                order.get("id"),
                course.amountInPaise(),
                "INR",
                course.id(),
                course.title(),
                request.studentName(),
                request.email()
        );
    }

    public void verifyAndNotify(PaymentVerificationRequest request) {
        if (!isValidSignature(request)) {
            throw new IllegalArgumentException("payment signature verification failed");
        }

        Course course = courseCatalogService.findById(request.courseId());

        // DB mein status update karo — SUCCESS
        paymentOrderRepository.findByRazorpayOrderId(request.razorpayOrderId())
                .ifPresent(order -> {
                    order.setRazorpayPaymentId(request.razorpayPaymentId());
                    order.setRazorpaySignature(request.razorpaySignature());
                    order.setStatus(PaymentStatus.SUCCESS);
                    order.setPaidAt(LocalDateTime.now());
                    paymentOrderRepository.save(order);
                });

        // Kafka pe message bhejo
        EnrollmentNotification notification = new EnrollmentNotification(
                request.studentName(),
                request.email(),
                course.id(),
                course.title(),
                course.amountInPaise(),
                request.razorpayOrderId(),
                request.razorpayPaymentId(),
                Instant.now().toString()
        );

        kafkaTemplate.send(kafkaTopic, request.email(), notification);
    }

    public boolean isValidSignature(PaymentVerificationRequest request) {
        String payload = request.razorpayOrderId() + "|" + request.razorpayPaymentId();
        String expectedSignature = hmacSha256(payload, secret);
        return expectedSignature.equals(request.razorpaySignature());
    }

    private String hmacSha256(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Unable to verify Razorpay signature", ex);
        }
    }
}