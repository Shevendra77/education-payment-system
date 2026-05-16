Write-Host "EduPay System Starting..." -ForegroundColor Green

Get-Content .env | ForEach-Object {
    if ($_ -match "^\s*#") { return }
    if ($_ -match "^\s*$") { return }

    $name, $value = $_ -split '=', 2
    Set-Item -Path env:$name -Value $value
}


# ============================================
# Step 1 - Start Docker Containers
# ============================================

Write-Host "`nStarting Docker containers..." -ForegroundColor Yellow

docker start cfs-payment-mysql
docker start cfs-notification-mysql
docker start kafka

Write-Host "Waiting 15 seconds for containers..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

$running = docker ps --format "{{.Names}}"

Write-Host "`nRunning containers:" -ForegroundColor Green
Write-Host $running


# ============================================
# Step 2 - Environment Variables
# ============================================

#Write-Host "`nSetting environment variables..." -ForegroundColor Yellow




Write-Host "Environment variables set!" -ForegroundColor Green


# ============================================
# Step 3 - Start NotificationService
# ============================================

Write-Host "`nStarting NotificationService..." -ForegroundColor Yellow

Start-Process powershell -ArgumentList @(
"-NoExit",
"-Command",
"cd 'S:\Projects\Education Payment System\NotificationService'; mvn spring-boot:run"
)

Start-Sleep -Seconds 5


# ============================================
# Step 4 - Start PaymentService
# ============================================

Write-Host "`nStarting PaymentService..." -ForegroundColor Yellow

Start-Process powershell -ArgumentList @(
"-NoExit",
"-Command",
"cd 'S:\Projects\Education Payment System\PaymentService'; mvn spring-boot:run"
)

# ============================================
# Step 5 - Create Tables
# ============================================

Write-Host "`nCreating tables..." -ForegroundColor Yellow

docker exec cfs-payment-mysql mysql -uroot -proot -e 'USE payment_db; CREATE TABLE IF NOT EXISTS payment_orders (id BIGINT NOT NULL AUTO_INCREMENT, amount_in_paise INT NOT NULL, course_id VARCHAR(255) NOT NULL, course_title VARCHAR(255) NOT NULL, created_at DATETIME(6) NOT NULL, email VARCHAR(255) NOT NULL, paid_at DATETIME(6), razorpay_order_id VARCHAR(255) NOT NULL UNIQUE, razorpay_payment_id VARCHAR(255), razorpay_signature VARCHAR(255), status VARCHAR(20) NOT NULL, student_name VARCHAR(255) NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB;'

docker exec cfs-notification-mysql mysql -uroot -proot -e 'USE notification_db; CREATE TABLE IF NOT EXISTS notification_logs (id BIGINT NOT NULL AUTO_INCREMENT, course_id VARCHAR(255) NOT NULL, course_title VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, error_message VARCHAR(255), razorpay_order_id VARCHAR(255) NOT NULL, razorpay_payment_id VARCHAR(255) NOT NULL, sent_at DATETIME(6) NOT NULL, status VARCHAR(20) NOT NULL, student_name VARCHAR(255) NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB;'


# ============================================
# Step 6 - Success Message
# ============================================

Write-Host "`nEduPay System Started Successfully!" -ForegroundColor Green

Write-Host "NotificationService -> http://localhost:8082" -ForegroundColor Cyan
Write-Host "PaymentService      -> http://localhost:8080" -ForegroundColor Cyan
Write-Host "payment_db          -> localhost:3308" -ForegroundColor Cyan
Write-Host "notification_db     -> localhost:3309" -ForegroundColor Cyan
Write-Host "Kafka               -> localhost:9092" -ForegroundColor Cyan