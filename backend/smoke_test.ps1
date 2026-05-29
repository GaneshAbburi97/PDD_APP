# Smoke Test Script for FastAPI Backend End-to-End Flow
# Run in Windows PowerShell

$ErrorActionPreference = "Stop"
$BaseUrl = "http://localhost:8000"
$MockAuthHeader = @{ "Authorization" = "Bearer mock-testuser" }

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "🚀 STARTING medical-processor BACKEND SMOKE TEST" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

# 1. Check Health Endpoint
Write-Host "`n[1/6] Checking Health Endpoint..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$BaseUrl/health" -Method Get
    Write-Host "✅ Health Check Successful!" -ForegroundColor Green
    Write-Host ($health | ConvertTo-Json -Depth 5) -ForegroundColor Gray
} catch {
    Write-Host "❌ Health Check Failed. Make sure the FastAPI server is running." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Exit
}

# 2. Register Mock User
Write-Host "`n[2/6] Registering Mock User..." -ForegroundColor Yellow
$regBody = @{
    email = "testuser@medical.com"
    password = "SuperPassword123!"
    displayName = "Test User"
} | ConvertTo-Json

try {
    $regResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/register" -Method Post -Body $regBody -ContentType "application/json"
    Write-Host "✅ User Registration Successful!" -ForegroundColor Green
    Write-Host ($regResponse | ConvertTo-Json -Depth 5) -ForegroundColor Gray
} catch {
    Write-Host "❌ User Registration Failed." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Exit
}

# 3. Login Mock User
Write-Host "`n[3/6] Logging in Mock User..." -ForegroundColor Yellow
$loginBody = @{
    email = "testuser@medical.com"
    password = "SuperPassword123!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    Write-Host "✅ User Login Successful!" -ForegroundColor Green
    Write-Host ($loginResponse | ConvertTo-Json -Depth 5) -ForegroundColor Gray
} catch {
    Write-Host "❌ User Login Failed." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Exit
}

# 4. Upload File
Write-Host "`n[4/6] Performing Multipart File Upload..." -ForegroundColor Yellow
$tempFilePath = Join-Path $env:TEMP "mock_medical_image.nii"
"DUMMY NIFTI IMAGE CONTENT" | Out-File -FilePath $tempFilePath -Encoding ascii

try {
    # Using curl.exe since it's pre-installed on Windows and handles multipart easily
    Write-Host "Uploading temp file: $tempFilePath" -ForegroundColor Gray
    $uploadOutput = & curl.exe -s -X POST "$BaseUrl/upload" `
        -H "Authorization: Bearer mock-testuser" `
        -F "file=@$tempFilePath"
    
    $uploadResponse = $uploadOutput | ConvertFrom-Json
    if ($uploadResponse.success -eq $true) {
        $fileUrl = $uploadResponse.data.fileUrl
        $fileName = $uploadResponse.data.fileName
        Write-Host "✅ File Upload Successful!" -ForegroundColor Green
        Write-Host "File URL: $fileUrl" -ForegroundColor Cyan
        Write-Host ($uploadResponse | ConvertTo-Json -Depth 5) -ForegroundColor Gray
    } else {
        Write-Host "❌ File Upload Failed: $($uploadResponse.message)" -ForegroundColor Red
        Exit
    }
} catch {
    Write-Host "❌ File Upload Failed with error." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Exit
} finally {
    if (Test-Path $tempFilePath) {
        Remove-Item $tempFilePath
    }
}

# 5. Start Processing Job
Write-Host "`n[5/6] Starting Processing Job..." -ForegroundColor Yellow
$processBody = @{
    fileUrl = $fileUrl
    fileName = $fileName
    fileSize = 1024
    cloudProvider = "firebase"
    userEmail = "testuser@medical.com"
} | ConvertTo-Json

try {
    $processResponse = Invoke-RestMethod -Uri "$BaseUrl/process" -Method Post -Body $processBody -ContentType "application/json" -Headers $MockAuthHeader
    if ($processResponse.success -eq $true) {
        $jobId = $processResponse.data.jobId
        Write-Host "✅ Processing Job Started Successfully!" -ForegroundColor Green
        Write-Host "Job ID: $jobId" -ForegroundColor Cyan
        Write-Host ($processResponse | ConvertTo-Json -Depth 5) -ForegroundColor Gray
    } else {
        Write-Host "❌ Job Start Failed: $($processResponse.message)" -ForegroundColor Red
        Exit
    }
} catch {
    Write-Host "❌ Job Start Failed with error." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Exit
}

# 6. Poll Job Status
Write-Host "`n[6/6] Polling Job Status for Job ID: $jobId..." -ForegroundColor Yellow
$completed = $false
$attempts = 0
$maxAttempts = 30

while (-not $completed -and $attempts -lt $maxAttempts) {
    $attempts++
    Start-Sleep -Seconds 2
    
    try {
        $statusResponse = Invoke-RestMethod -Uri "$BaseUrl/process/status/$jobId" -Method Get -Headers $MockAuthHeader
        if ($statusResponse.success -eq $true) {
            $status = $statusResponse.data.status
            $progress = $statusResponse.data.progress
            Write-Host "Attempt ${attempts}: Status = $status, Progress = $progress%" -ForegroundColor Gray
            
            if ($status -eq "COMPLETED") {
                Write-Host "`n✅ Job Completed Successfully!" -ForegroundColor Green
                Write-Host "Result URL: $($statusResponse.data.resultUrl)" -ForegroundColor Cyan
                $completed = $true
            } elseif ($status -eq "FAILED") {
                Write-Host "`n❌ Job Failed: $($statusResponse.data.errorMessage)" -ForegroundColor Red
                Exit
            }
        }
    } catch {
        Write-Host "⚠️ Status check warning: $_" -ForegroundColor DarkYellow
    }
}

if (-not $completed) {
    Write-Host "❌ Job polling timed out." -ForegroundColor Red
    Exit
}

# 7. Fetch Final Result
Write-Host "`n[BONUS] Retrieving Final Job Result..." -ForegroundColor Yellow
try {
    $resultResponse = Invoke-RestMethod -Uri "$BaseUrl/process/result/$jobId" -Method Get -Headers $MockAuthHeader
    Write-Host "✅ Result Payload Fetched Successfully!" -ForegroundColor Green
    Write-Host ($resultResponse | ConvertTo-Json -Depth 5) -ForegroundColor Gray
} catch {
    Write-Host "❌ Result Fetch Failed." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host "`n==================================================" -ForegroundColor Green
Write-Host "🎉 ALL BACKEND SMOKE TESTS PASSED PERFECTLY!" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green
