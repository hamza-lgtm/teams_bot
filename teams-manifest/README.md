# Teams Bot Manifest

This directory contains the Teams application manifest and required assets for the notification bot.

## Required Files
- `manifest.json`: The Teams application manifest file
- `outline.jpeg`: A 32x32 transparent outline icon for your app
- `color.jpeg`: A 192x192 full color version of your app icon

## How to Use
1. Replace the placeholder images (`outline.jpeg` and `color.jpeg`) with your actual bot icons
2. Zip all three files (manifest.json and both images)
3. Upload the zip file to Teams when registering your bot

## Important Notes
- The botId in the manifest matches the MicrosoftAppId in your application.properties
- The bot is configured for personal scope only
- Make sure your actual icon files match the required dimensions (32x32 for outline, 192x192 for color)
