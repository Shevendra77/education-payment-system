# Education Payment System

A microservices-based Education Payment System built using Spring Boot, Apache Kafka, Docker, MySQL, and Razorpay integration.

## 🚀 Features

- Course payment processing
- Razorpay payment gateway integration
- Kafka-based event-driven communication
- Email notification service
- Dockerized MySQL databases
- Secure environment variable configuration
- Microservices architecture

---

# 🏗️ Architecture

Frontend
↓
PaymentService
↓
Razorpay Payment Gateway
↓
Apache Kafka
↓
NotificationService
↓
Email Notification

---

# 🛠️ Technologies Used

- Java 17
- Spring Boot
- Spring Kafka
- MySQL
- Docker
- Apache Kafka
- Razorpay API
- Maven
- PowerShell
- Git & GitHub

---

# 📦 Microservices

## 1. PaymentService

Responsibilities:
- Create Razorpay orders
- Handle payment processing
- Publish Kafka events
- Store payment details

Runs on:

http://localhost:8080

---

## 2. NotificationService

Responsibilities:
- Consume Kafka events
- Send email notifications
- Store notification logs

Runs on:

http://localhost:8082

---

# 🗄️ Databases

| Database | Port |
|----------|------|
| payment_db | 3308 |
| notification_db | 3309 |

---

# 📨 Kafka

Kafka runs on:

localhost:9092

Topic used:

course-enrollment-notification

---

# 🔐 Environment Variables

Create a `.env` file in the project root.

Example:

```env
DB_PASSWORD=root

RAZORPAY_KEY_ID=your_key
RAZORPAY_KEY_SECRET=your_secret

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

---

# ▶️ Running the Project Locally

## 1. Start Docker Containers

```powershell
docker start cfs-payment-mysql
docker start cfs-notification-mysql
docker start kafka
```

---

## 2. Run PowerShell Script

```powershell
.\start.ps1
```

---

## 3. Access Services

PaymentService:

http://localhost:8080

NotificationService:

http://localhost:8082

---

# 🧪 Testing

## Verify Payment Database

```powershell
docker exec -it cfs-payment-mysql mysql -uroot -proot
```

```sql
USE payment_db;
SELECT * FROM payment_orders;
```

---

## Verify Notification Database

```powershell
docker exec -it cfs-notification-mysql mysql -uroot -proot
```

```sql
USE notification_db;
SELECT * FROM notification_logs;
```

---

# 📚 What I Learned

- Microservices architecture
- Event-driven systems
- Apache Kafka producer/consumer
- Docker containerization
- Payment gateway integration
- Environment variable security
- Git & GitHub workflow
- Spring Boot backend development

---

# 🔒 Security Notes

- Secrets are managed using environment variables
- `.env` is excluded using `.gitignore`
- API keys are not committed to GitHub

---

# 📌 Future Improvements

- JWT Authentication
- API Gateway
- Kubernetes Deployment
- CI/CD Pipeline
- Swagger Documentation
- Redis Caching

---

# 👨‍💻 Author

Shevendra Chandel

GitHub:
https://github.com/Shevendra77
