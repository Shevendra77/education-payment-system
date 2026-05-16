Write-Host "🛑 Stopping EduPay System..." -ForegroundColor Red

# Java processes band karo
Get-Process | Where-Object {$_.Name -eq "java"} | Stop-Process -Force
Write-Host "✅ Java services stopped!" -ForegroundColor Green

# Docker containers band karo
docker stop cfs-payment-mysql cfs-notification-mysql kafka
Write-Host "✅ Docker containers stopped!" -ForegroundColor Green

Write-Host "👋 EduPay System Stopped!" -ForegroundColor Red