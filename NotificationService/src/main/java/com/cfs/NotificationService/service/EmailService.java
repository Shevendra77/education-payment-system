package com.cfs.NotificationService.service;

import com.cfs.NotificationService.entity.NotificationLog;
import com.cfs.NotificationService.entity.NotificationStatus;
import com.cfs.NotificationService.model.EnrollmentNotification;
import com.cfs.NotificationService.repository.NotificationLogRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

            log.info(
                    "Mail disabled. Email skipped for {} and course {}",
                    notification.email(),
                    notification.courseTitle()
            );

            notificationLog.setStatus(NotificationStatus.FAILED);
            notificationLog.setErrorMessage("Mail disabled");

            notificationLogRepository.save(notificationLog);

            return;
        }

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(notification.email());

            helper.setSubject(
                    "🎉 Enrollment Confirmed - " + notification.courseTitle()
            );

            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">

                        <style>

                            body {
                                font-family: Arial, sans-serif;
                                background-color: #f4f4f4;
                                margin: 0;
                                padding: 0;
                            }

                            .container {
                                max-width: 600px;
                                margin: 40px auto;
                                background-color: #ffffff;
                                border-radius: 10px;
                                overflow: hidden;
                                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                            }

                            .header {
                                background-color: #2563eb;
                                color: white;
                                padding: 25px;
                                text-align: center;
                            }

                            .content {
                                padding: 30px;
                                color: #333333;
                                line-height: 1.7;
                            }

                            .details {
                                background-color: #f8fafc;
                                padding: 20px;
                                border-radius: 8px;
                                margin-top: 20px;
                            }

                            .details p {
                                margin: 8px 0;
                            }

                            .button {
                                display: inline-block;
                                padding: 12px 20px;
                                background-color: #2563eb;
                                color: white !important;
                                text-decoration: none;
                                border-radius: 6px;
                                margin-top: 20px;
                            }

                            .footer {
                                background-color: #f1f5f9;
                                padding: 20px;
                                text-align: center;
                                font-size: 13px;
                                color: #666666;
                            }

                        </style>
                    </head>

                    <body>

                        <div class="container">

                            <div class="header">
                                <h1>Enrollment Confirmed 🎓</h1>
                            </div>

                            <div class="content">

                                <h2>Hello %s,</h2>

                                <p>
                                    Congratulations!
                                    Your payment has been successfully processed
                                    and your enrollment is now confirmed.
                                </p>

                                <div class="details">

                                    <p>
                                        <strong>📘 Course:</strong> %s
                                    </p>

                                    <p>
                                        <strong>💳 Payment ID:</strong> %s
                                    </p>

                                    <p>
                                        <strong>🧾 Order ID:</strong> %s
                                    </p>

                                </div>

                                <p>
                                    You can now start your learning journey
                                    and access your course materials.
                                </p>

                                <a href="http://localhost:8080"
                                   class="button">
                                    Access Your Course
                                </a>

                            </div>

                            <div class="footer">

                                © 2026 CFS Learning Platform
                                <br>
                                Thank you for learning with us.

                            </div>

                        </div>

                    </body>
                    </html>
                    """.formatted(
                    notification.studentName(),
                    notification.courseTitle(),
                    notification.razorpayPaymentId(),
                    notification.razorpayOrderId()
            );

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            notificationLog.setStatus(NotificationStatus.SUCCESS);

            log.info(
                    "Enrollment email sent to {}",
                    notification.email()
            );

        } catch (Exception e) {

            notificationLog.setStatus(NotificationStatus.FAILED);

            notificationLog.setErrorMessage(
                    e.getMessage()
            );

            log.error(
                    "Failed to send email to {}: {}",
                    notification.email(),
                    e.getMessage()
            );
        }

        notificationLogRepository.save(notificationLog);
    }
}
