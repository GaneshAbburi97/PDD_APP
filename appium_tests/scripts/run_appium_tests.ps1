param(
    [string]$AppiumServer = "http://127.0.0.1:4723",
    [string]$DeviceName = "Android",
    [string]$ApkPath = "",
    [string]$Markers = "",
    [switch]$NoReset
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root

if ($NoReset) {
    $env:APPIUM_NO_RESET = "true"
}

$argsList = @(
    "-m", "pytest",
    "-v",
    "--appium-server", $AppiumServer,
    "--device-name", $DeviceName
)

if ($ApkPath.Trim().Length -gt 0) {
    $argsList += @("--apk", $ApkPath)
}

if ($Markers.Trim().Length -gt 0) {
    $argsList += @("-m", $Markers)
}

python @argsList
