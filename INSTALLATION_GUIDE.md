# Teams Notification Bot - Installation Guide

## For Bot Administrators (You)

### 1. Register the Bot in Azure
1. Go to [Azure Portal](https://portal.azure.com)
2. Create a new "Azure Bot" resource:
   - Click "Create a resource"
   - Search for "Azure Bot"
   - Click "Create"
   - Fill in the required information:
     - Bot handle (name)
     - Subscription
     - Resource group
     - Location
3. After creation, note down:
   - Microsoft App ID
   - Microsoft App Password (client secret)

### 2. Configure the Bot
1. Update `application.properties` with your credentials:
   ```properties
   MicrosoftAppId=your_app_id_here
   MicrosoftAppPassword=your_app_password_here
   ```

### 3. Deploy the Bot
1. Build the application:
   ```bash
   mvn clean package
   ```
2. Deploy to a server with HTTPS support (required by Microsoft Teams)
3. Make sure your bot endpoint is accessible at: `https://your-domain.com/api/messages`

### 4. Create Teams App Package
1. Update the manifest.json in teams-manifest folder:
   ```json
   {
     "id": "your_bot_id",
     "packageName": "com.notification.teamsbot",
     "developer": {
       "name": "Your Company",
       "websiteUrl": "https://your-company.com",
       "privacyUrl": "https://your-company.com/privacy",
       "termsOfUseUrl": "https://your-company.com/terms"
     }
   }
   ```
2. Create icons:
   - color.png (192x192)
   - outline.png (32x32)
3. Zip these files together:
   - manifest.json
   - color.png
   - outline.png

## For Users (Your Customers)

### 1. Install the Bot
1. Receive the Teams app package (ZIP file) from the administrator
2. In Microsoft Teams:
   - Click the "Apps" icon in the left sidebar
   - Click "Upload a custom app" at the bottom left
   - Select "Upload for me or my teams"
   - Choose the ZIP file you received
   - Click "Add"

### 2. Register with the Bot
1. After installation:
   - Find the bot in your Teams app list
   - Start a chat with the bot
   - Type "register" in the chat
   - The bot will confirm your registration

### 3. Receive Notifications
- Once registered, you'll automatically receive notifications when they're sent
- Notifications will appear as chat messages from the bot
- You can view all past notifications in your chat history with the bot

## Testing the Installation

1. After installation, users can verify the setup:
   - Start a chat with the bot
   - Type "register"
   - Should receive a confirmation message

2. Administrators can test sending notifications:
   ```bash
   curl -X POST https://your-domain.com/api/notifications/send \
   -H "Content-Type: application/json" \
   -d '{
       "hostEmail": "user@company.com",
       "message": "Test notification message"
   }'
   ```

## Troubleshooting

### Common Issues

1. Bot Not Responding
   - Verify the bot service is running
   - Check application.properties configuration
   - Ensure the endpoint is accessible

2. Installation Failed
   - Verify the manifest.json is correctly formatted
   - Ensure all required URLs are HTTPS
   - Check if icons meet size requirements

3. Registration Failed
   - Ensure the bot service is running
   - Check network connectivity
   - Verify user has permissions to install apps

### Support

For technical support:
1. Check the application logs
2. Contact the bot administrator
3. Verify Teams app installation permissions with your IT department

## Security Notes

1. The bot only sends messages to registered users
2. All communication is encrypted via HTTPS
3. User data is stored securely
4. Bot access is limited to approved organizations
