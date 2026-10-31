
# ==========================================
# EduPay System - Startup Script
# ==========================================

$ErrorActionPreference = "Stop"

$projectRoot = "S:\Projects\Education Payment System"
$pidFile = Join-Path $projectRoot "service-pids.txt"

Write-Host "======================================" -ForegroundColor Green
Write-Host "       EduPay System Starting         " -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

# 1. Validate project folder
if (!(Test-Path $projectRoot)) {
    Write-Host "Project folder not found!" -ForegroundColor Red
    exit 1
}

# 2. Validate service folders
$services = @(
    @{ Name = "NotificationService"; Folder = "NotificationService"; Port = 8082 },
    @{ Name = "PaymentService"; Folder = "PaymentService"; Port = 8080 },
    @{ Name = "AuthService"; Folder = "AuthService"; Port = 8081 }
)

foreach ($service in $services) {
    $servicePath = Join-Path $projectRoot $service.Folder

    if (!(Test-Path $servicePath)) {
        Write-Host "$($service.Folder) folder not found!" -ForegroundColor Red
        exit 1
    }
}

# 3. Check Maven
if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "Maven is not installed or not in PATH!" -ForegroundColor Red
    exit 1
}

# 4. Load environment variables
$envFile = Join-Path $projectRoot ".env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()

        if ($line -and !$line.StartsWith("#")) {
            $parts = $line -split '=', 2

            if ($parts.Count -eq 2) {
                $name = $parts[0].Trim()
                $value = $parts[1].Trim()

                if ($value.Length -ge 2 -and
                    (($value.StartsWith('"') -and $value.EndsWith('"')) -or
                     ($value.StartsWith("'") -and $value.EndsWith("'")))) {
                    $value = $value.Substring(1, $value.Length - 2)
                }

                if ($name -match '^[a-zA-Z_][a-zA-Z0-9_]*$') {
                    Set-Item -Path "env:$name" -Value $value
                }
            }
        }
    }

    Write-Host ".env variables loaded." -ForegroundColor Green
}

# 5. Prevent duplicate startup
if (Test-Path $pidFile) {
    $oldPids = Get-Content $pidFile
    $runningPids = @()

    foreach ($oldPid in $oldPids) {
        if ($oldPid -match '^\d+$') {
            if (Get-Process -Id ([int]$oldPid) -ErrorAction SilentlyContinue) {
                $runningPids += $oldPid
            }
        }
    }

    if ($runningPids.Count -gt 0) {
        Write-Host "Services may already be running. Run stop.ps1 first." -ForegroundColor Yellow
        exit 1
    }

    Remove-Item $pidFile -Force
}

# 6. Start Docker infrastructure
Write-Host "`nStarting Docker infrastructure..." -ForegroundColor Yellow

Set-Location $projectRoot

docker compose -f "$projectRoot\docker-compose.yml" up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker Compose failed!" -ForegroundColor Red
    exit 1
}

# 7. Wait for MySQL databases
Write-Host "`nWaiting for MySQL databases..." -ForegroundColor Yellow

$databases = @(
    "cfs-payment-mysql",
    "cfs-notification-mysql",
    "cfs-auth-mysql"
)

foreach ($db in $databases) {

    $attempt = 0
    $health = ""

    do {
        Start-Sleep -Seconds 3

        $health = docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' $db 2>$null

        if ($LASTEXITCODE -ne 0) {
            $health = "not-found"
        }

        $attempt++

        Write-Host "$db : $health"

    } until ($health -eq "healthy" -or $attempt -ge 20)

    if ($health -ne "healthy") {
        Write-Host "$db did not become healthy. Check Docker logs." -ForegroundColor Red
        exit 1
    }
}

Write-Host "All MySQL databases are healthy." -ForegroundColor Green

# 8. Start Kafka
Write-Host "`nChecking Kafka..." -ForegroundColor Yellow

$kafka = docker inspect --format='{{.State.Status}}' kafka 2>$null

if ($LASTEXITCODE -ne 0) {
    Write-Host "Kafka container not found. Check your existing Kafka setup." -ForegroundColor Red
    exit 1
}
elseif ($kafka -eq "running") {
    Write-Host "Kafka is already running." -ForegroundColor Green
}
else {
    docker start kafka

    if ($LASTEXITCODE -ne 0) {
        Write-Host "Kafka failed to start!" -ForegroundColor Red
        exit 1
    }
}

# 9. Start service windows and save PIDs
Write-Host "`nStarting Spring Boot services..." -ForegroundColor Yellow

$servicePids = @()

foreach ($service in $services) {

    $servicePath = Join-Path $projectRoot $service.Folder

    Write-Host "Starting $($service.Name)..." -ForegroundColor Cyan

    $command = "Set-Location -LiteralPath '$servicePath'; mvn spring-boot:run"

    $process = Start-Process `
        -FilePath "powershell.exe" `
        -ArgumentList @(
            "-NoExit",
            "-ExecutionPolicy", "Bypass",
            "-Command", $command
        ) `
        -PassThru

    if (!$process) {
        Write-Host "Failed to launch $($service.Name)!" -ForegroundColor Red
        exit 1
    }

    $servicePids += $process.Id

    # Save after each launch, so partial startup can also be stopped
    $servicePids | Set-Content -Path $pidFile

    Write-Host "$($service.Name) launched. PID: $($process.Id)" -ForegroundColor Green

    Start-Sleep -Seconds 3
}

# 10. Completion
Write-Host "`n======================================" -ForegroundColor Green
Write-Host "    EduPay Startup Commands Launched  " -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

Write-Host "`nService URLs:" -ForegroundColor Cyan
Write-Host "AuthService        : http://localhost:8081"
Write-Host "PaymentService     : http://localhost:8080"
Write-Host "NotificationService: http://localhost:8082"

Write-Host "`nDatabase URLs:" -ForegroundColor Cyan
Write-Host "Payment DB         : localhost:3308"
Write-Host "Notification DB    : localhost:3309"
Write-Host "Auth DB            : localhost:3310"
Write-Host "Kafka              : localhost:9092"

Write-Host "`nProcess IDs saved in service-pids.txt" -ForegroundColor Green
Write-Host "Use .\stop.ps1 to stop the system." -ForegroundColor Yellow
