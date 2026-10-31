
# ==========================================
# Education Payment System - Stop Script
# ==========================================

$projectRoot = "S:\Projects\Education Payment System"
$pidFile = Join-Path $projectRoot "service-pids.txt"

Write-Host "`n======================================" -ForegroundColor Red
Write-Host "      EduPay System Stopping         " -ForegroundColor Red
Write-Host "======================================" -ForegroundColor Red

# 1. Stop tracked Spring Boot service windows
if (Test-Path $pidFile) {

    $servicePids = Get-Content $pidFile

    foreach ($servicePid in $servicePids) {

        if ($servicePid -match '^\d+$') {

            $process = Get-Process -Id ([int]$servicePid) -ErrorAction SilentlyContinue

            if ($process) {

                Write-Host "Stopping service process tree: $servicePid" -ForegroundColor Yellow

                taskkill /PID ([int]$servicePid) /T /F | Out-Null

                if ($LASTEXITCODE -eq 0) {
                    Write-Host "Process $servicePid stopped." -ForegroundColor Green
                }
                else {
                    Write-Host "Could not stop process $servicePid." -ForegroundColor Red
                }
            }
            else {
                Write-Host "Process $servicePid is already stopped." -ForegroundColor DarkGray
            }
        }
    }

    Remove-Item $pidFile -Force
}
else {
    Write-Host "No tracked service processes found." -ForegroundColor Yellow
}

# 2. Stop Docker Compose containers (MySQL databases)
Write-Host "`nStopping Docker Compose containers..." -ForegroundColor Cyan

if (Test-Path "$projectRoot\docker-compose.yml") {

    Set-Location $projectRoot

    docker compose stop

    if ($LASTEXITCODE -eq 0) {
        Write-Host "Docker Compose containers stopped." -ForegroundColor Green
    }
    else {
        Write-Host "Docker Compose stop failed!" -ForegroundColor Red
    }
}
else {
    Write-Host "docker-compose.yml not found!" -ForegroundColor Red
}

# 3. Stop Kafka separately
# If Kafka is part of Docker Compose, it is already stopped above.
Write-Host "`nStopping Kafka..." -ForegroundColor Cyan

$kafka = docker inspect kafka --format='{{.State.Status}}' 2>$null

if ($LASTEXITCODE -eq 0 -and $kafka -eq "running") {

    docker stop kafka

    if ($LASTEXITCODE -eq 0) {
        Write-Host "Kafka stopped successfully." -ForegroundColor Green
    }
    else {
        Write-Host "Kafka failed to stop!" -ForegroundColor Red
    }
}
elseif ($LASTEXITCODE -eq 0) {
    Write-Host "Kafka is already stopped." -ForegroundColor Yellow
}
else {
    Write-Host "Kafka container not found." -ForegroundColor Yellow
}

# 4. Completion
Write-Host "`n======================================" -ForegroundColor Green
Write-Host "       EduPay System Stopped          " -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

Write-Host "`nAll shutdown commands executed." -ForegroundColor Green
