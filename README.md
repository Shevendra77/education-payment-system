# 🎓 Education Payment System

A production-style microservices-based Education Payment System built using Spring Boot, Apache Kafka, Docker, MySQL, and Razorpay integration.

This project demonstrates event-driven architecture, asynchronous communication between services, secure environment-based configuration, and payment workflow integration commonly used in modern backend systems.

---

# 📌 Project Overview

The system allows students to purchase courses through an online payment workflow.

After successful payment:
1. PaymentService processes the payment
2. A Kafka event is published
3. NotificationService consumes the event
4. Email notification is sent to the student
5. Notification logs are stored in the database

---

# 🏗️ System Architecture

```text
┌─────────────┐
│  Frontend   │
└──────┬──────┘
       │ REST API
       ▼
┌──────────────────┐
│  PaymentService  │
└──────┬───────────┘
       │
       │ Razorpay Integration
       ▼
┌──────────────────┐
│ Payment Gateway  │
└──────┬───────────┘
       │
       │ Kafka Producer
       ▼
┌──────────────────┐
│   Apache Kafka   │
└──────┬───────────┘
       │ Kafka Consumer
       ▼
┌──────────────────────┐
│ NotificationService  │
└─────────┬────────────┘
          │
          ▼
   Email Notification
```

---

# 🚀 Features

- Microservices architecture
- Event-driven communication
- Apache Kafka producer/consumer implementation
- Razorpay payment gateway integration
- Email notification system
- Dockerized MySQL databases
- Environment variable based configuration
- Automated startup scripts
- Database persistence
- Secure secret management
- GitHub-ready project structure

---

# 🛠️ Tech Stack

## Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Kafka

## Messaging
- Apache Kafka

## Database
- MySQL 8

## DevOps / Infrastructure
- Docker
- Docker Compose
- PowerShell Automation

## Payment Gateway
- Razorpay API

## Build Tool
- Maven

## Version Control
- Git
- GitHub

---

# 📦 Microservices

## 1️⃣ PaymentService

### Responsibilities
- Create Razorpay payment orders
- Handle payment processing
- Publish Kafka events
- Persist payment details

### Runs On

```text
http://localhost:8080
```

---

## 2️⃣ NotificationService

### Responsibilities
- Consume Kafka events
- Send email notifications
- Persist notification logs

### Runs On

```text
http://localhost:8082
```

---

# 🗄️ Database Configuration

| Database | Container | Port |
|----------|-----------|------|
| payment_db | cfs-payment-mysql | 3308 |
| notification_db | cfs-notification-mysql | 3309 |

---

# 📨 Kafka Configuration

| Property | Value |
|----------|------|
| Broker | localhost:9092 |
| Topic | course-enrollment-notification |

---

# 🔐 Environment Variables

Create a `.env` file in the project root directory.

Example:

```env
DB_PASSWORD=root

RAZORPAY_KEY_ID=your_razorpay_key
RAZORPAY_KEY_SECRET=your_razorpay_secret

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

---

# 📁 Project Structure

```text
Education Payment System/
│
├── PaymentService/
│
├── NotificationService/
│
├── docker-compose.yml
├── start.ps1
├── stop.ps1
├── .env
├── .gitignore
└── README.md
```

---

# ▶️ Running the Project Locally

## Prerequisites

Ensure the following are installed:

- Java 17+
- Maven
- Docker Desktop
- Git

---

# Step 1 — Clone Repository

```bash
git clone https://github.com/Shevendra77/education-payment-system.git
```

```bash
cd education-payment-system
```

---

# Step 2 — Configure Environment Variables

Create:

```text
.env
```

Add:

```env
DB_PASSWORD=root

RAZORPAY_KEY_ID=your_key
RAZORPAY_KEY_SECRET=your_secret

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

---

# Step 3 — Start Docker Containers

```powershell
docker start cfs-payment-mysql
docker start cfs-notification-mysql
docker start kafka
```

---

# Step 4 — Start Application

```powershell
.\start.ps1
```

---

# Step 5 — Access Services

| Service | URL |
|---------|-----|
| PaymentService | http://localhost:8080 |
| NotificationService | http://localhost:8082 |

---

# 🧪 Database Verification

## Payment Database

```powershell
docker exec -it cfs-payment-mysql mysql -uroot -proot
```

```sql
USE payment_db;
SHOW TABLES;
SELECT * FROM payment_orders;
```

---

## Notification Database

```powershell
docker exec -it cfs-notification-mysql mysql -uroot -proot
```

```sql
USE notification_db;
SHOW TABLES;
SELECT * FROM notification_logs;
```

---

# 🔄 Event Flow

```text
1. User initiates payment
2. PaymentService creates Razorpay order
3. Payment succeeds
4. Kafka event is published
5. NotificationService consumes event
6. Email notification sent
7. Notification logs stored
```

---

# 🔒 Security

- Secrets managed using environment variables
- `.env` excluded using `.gitignore`
- API keys are never committed to GitHub
- Sensitive credentials externalized from source code

---

# 📚 Key Learning Outcomes

This project demonstrates practical implementation of:

- Microservices Architecture
- Event-Driven Systems
- Apache Kafka Messaging
- Asynchronous Communication
- Docker Containerization
- Payment Gateway Integration
- Environment-Based Configuration
- Service Decoupling
- Git & GitHub Workflow

---

# 🚧 Future Enhancements

- JWT Authentication & Authorization
- API Gateway
- Service Discovery
- Kubernetes Deployment
- CI/CD Pipeline
- Swagger/OpenAPI Documentation
- Redis Caching
- Centralized Logging
- Monitoring & Observability
- Retry & Dead Letter Queue handling

---

# 👨‍💻 Author

## Shevendra Chandel

GitHub:
https://github.com/Shevendra77

---

# ⭐ If You Like This Project

Consider giving this repository a star on GitHub.
