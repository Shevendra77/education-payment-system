package com.cfs.PaymentService.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razorpay_order_id", nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "amount_in_paise", nullable = false)
    private int amountInPaise;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Getters & Setters
    public Long getId() { return id; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String v) { this.razorpayOrderId = v; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String v) { this.razorpayPaymentId = v; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String v) { this.razorpaySignature = v; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String v) { this.courseId = v; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String v) { this.courseTitle = v; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String v) { this.studentName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public int getAmountInPaise() { return amountInPaise; }
    public void setAmountInPaise(int v) { this.amountInPaise = v; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus v) { this.status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime v) { this.paidAt = v; }
}
