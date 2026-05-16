package com.cfs.NotificationService.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    @Column(name = "razorpay_order_id", nullable = false)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", nullable = false)
    private String razorpayPaymentId;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    public Long getId() { return id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String v) { this.studentName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String v) { this.courseId = v; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String v) { this.courseTitle = v; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String v) { this.razorpayOrderId = v; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String v) { this.razorpayPaymentId = v; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime v) { this.sentAt = v; }
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus v) { this.status = v; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String v) { this.errorMessage = v; }
}