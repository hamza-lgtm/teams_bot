# Create Teams App Package Script

# Parameters
param(
    [Parameter(Mandatory=$true)]
    [string]$BotId,
    
    [Parameter(Mandatory=$true)]
    [string]$CompanyName,
    
    [Parameter(Mandatory=$true)]
    [string]$WebsiteUrl
)

# Ensure we're in the right directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

# Create teams-manifest directory if it doesn't exist
$manifestDir = "teams-manifest"
if (-not (Test-Path $manifestDir)) {
    New-Item -ItemType Directory -Path $manifestDir
}

# Update manifest.json with provided parameters
$manifestContent = Get-Content "teams-manifest/manifest.json" | ConvertFrom-Json
$manifestContent.id = $BotId
$manifestContent.developer.name = $CompanyName
$manifestContent.developer.websiteUrl = $WebsiteUrl
$manifestContent.developer.privacyUrl = "$WebsiteUrl/privacy"
$manifestContent.developer.termsOfUseUrl = "$WebsiteUrl/terms"

# Save updated manifest
$manifestContent | ConvertTo-Json -Depth 10 | Set-Content "teams-manifest/manifest.json"

# Check if icons exist
if (-not (Test-Path "teams-manifest/color.png") -or -not (Test-Path "teams-manifest/outline.png")) {
    Write-Warning "Please ensure color.png (192x192) and outline.png (32x32) exist in the teams-manifest directory"
    exit
}

# Create zip package
$packageName = "teams-bot-package.zip"
if (Test-Path $packageName) {
    Remove-Item $packageName
}

Compress-Archive -Path "$manifestDir/*" -DestinationPath $packageName

Write-Host "Teams app package created: $packageName"
Write-Host "You can now distribute this package to your users for installation in Teams"
