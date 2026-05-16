package com.cfs.NotificationService.service;

import com.cfs.NotificationService.entity.NotificationLog;
import com.cfs.NotificationService.entity.NotificationStatus;
import com.cfs.NotificationService.model.EnrollmentNotification;
import com.cfs.NotificationService.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final NotificationLogRepository notificationLogRepository;
    private final boolean mailEnabled;
    private final String mailFrom;

    public EmailService(
            JavaMailSender mailSender,
            NotificationLogRepository notificationLogRepository,
            @Value("${app.mail.enabled:true}") boolean mailEnabled,
            @Value("${app.mail.from}") String mailFrom
    ) {
        this.mailSender = mailSender;
        this.notificationLogRepository = notificationLogRepository;
        this.mailEnabled = mailEnabled;
        this.mailFrom = mailFrom;
    }

    public void sendEnrollmentEmail(EnrollmentNotification notification) {

        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setStudentName(notification.studentName());
        notificationLog.setEmail(notification.email());
        notificationLog.setCourseId(notification.courseId());
        notificationLog.setCourseTitle(notification.courseTitle());
        notificationLog.setRazorpayOrderId(notification.razorpayOrderId());
        notificationLog.setRazorpayPaymentId(notification.razorpayPaymentId());
        notificationLog.setSentAt(LocalDateTime.now());

        if (!mailEnabled) {
            log.info("Mail disabled. Email skipped for {} and course {}", notification.email(), notification.courseTitle());
            notificationLog.setStatus(NotificationStatus.FAILED);
            notificationLog.setErrorMessage("Mail disabled");
            notificationLogRepository.save(notificationLog);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(notification.email());
            message.setSubject("Course enrollment confirmed: " + notification.courseTitle());
            message.setText("""
                    Hello %s,
                    Your payment was successful and your enrollment is confirmed.

                    Course: %s
                    Payment Id: %s
                    Order Id: %s

                    Happy Learning!
                    """.formatted(
                    notification.studentName(),
                    notification.courseTitle(),
                    notification.razorpayPaymentId(),
                    notification.razorpayOrderId()
            ));

            mailSender.send(message);
            notificationLog.setStatus(NotificationStatus.SUCCESS);
            log.info("Enrollment email sent to {}", notification.email());

        } catch (Exception e) {
            notificationLog.setStatus(NotificationStatus.FAILED);
            notificationLog.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}: {}", notification.email(), e.getMessage());
        }

        notificationLogRepository.save(notificationLog);
    }
}