# Ngrok Setup Guide for Teams Notification Bot

This guide provides detailed instructions for setting up and using ngrok with your Teams Notification Bot during development.

## What is Ngrok?

Ngrok is a tunneling tool that creates a secure tunnel between your local development machine and the public internet. It's essential for Teams bot development because Microsoft Teams requires public HTTPS endpoints to communicate with your bot.

## Installation Steps

### 1. Download Ngrok

1. Visit [https://ngrok.com/download](https://ngrok.com/download)
2. Download the version for Windows
3. Extract the zip file to a location on your computer (e.g., `C:\Program Files\ngrok`)
4. Add ngrok to your system PATH:
   - Open System Properties → Advanced → Environment Variables
   - Under System Variables, find PATH
   - Add the ngrok directory path

### 2. Create Ngrok Account

1. Go to [https://dashboard.ngrok.com/signup](https://dashboard.ngrok.com/signup)
2. Create a free account
3. Copy your authtoken from the dashboard

### 3. Configure Ngrok

1. Open Command Prompt
2. Run the following command:
```bash
ngrok config add-authtoken YOUR_AUTH_TOKEN
```

## Using Ngrok with Teams Bot

### 1. Start Your Bot Locally

1. Navigate to your bot project directory
```bash
cd path/to/teams-notification-bot-java
```

2. Start your Spring Boot application
```bash
mvn spring-boot:run
```
Your bot should now be running on `http://localhost:3978`

### 2. Start Ngrok Tunnel

1. Open a new Command Prompt
2. Start ngrok pointing to your bot's port
```bash
ngrok http 3978
```

3. You'll see output similar to:
```
Session Status                online
Account                       your-email@example.com
Version                       3.x.x
Region                       United States (us)
Web Interface                 http://127.0.0.1:4040
Forwarding                    https://abc123.ngrok.io -> http://localhost:3978
```

4. Copy the HTTPS URL (e.g., `https://abc123.ngrok.io`)

### 3. Update Bot Configuration

#### A. Update Azure Bot Service

1. Go to [Azure Portal](https://portal.azure.com)
2. Navigate to your bot resource
3. Go to Configuration in the left sidebar
4. Update the following settings:
   - Messaging endpoint: `https://your-ngrok-url/api/messages`
   - Microsoft App ID: Keep your existing App ID
   - Microsoft App Password: Keep your existing password
5. Click "Apply" to save changes
6. Test the connection using the "Test in Web Chat" feature

#### B. Update Teams Manifest

1. Open `teams-manifest/manifest.json`
2. Update the following fields:
```json
{
    "validDomains": [
        "*.ngrok.io",
        "token.botframework.com"
    ],
    "bots": [
        {
            "botId": "YOUR-MICROSOFT-APP-ID",
            "scopes": [
                "personal",
                "team"
            ],
            "supportsFiles": false,
            "isNotificationOnly": true
        }
    ]
}
```

#### C. Update Application Properties

1. Open `src/main/resources/application.properties`
2. Update or verify the following properties:
```properties
# Server Configuration
server.port=3978

# Bot Configuration
MicrosoftAppId=your-app-id
MicrosoftAppPassword=your-app-password

# Logging for Development
logging.level.com.teamsbot=DEBUG
logging.level.com.microsoft.bot=DEBUG
```

#### D. Verify Configuration

1. **Check Bot Registration**:
   - Visit Azure Bot Service
   - Go to "Test in Web Chat"
   - Send a test message
   - Verify you receive a response

2. **Check Teams Integration**:
   - Open Microsoft Teams
   - Go to Apps
   - Find your bot
   - Send a test message
   - Check ngrok inspection UI for request/response

3. **Verify Endpoints**:
   - Open ngrok web interface (http://localhost:4040)
   - Look for successful HTTP 200 responses
   - Check for any error responses
   - Verify the correct endpoints are being called

#### E. Troubleshooting Configuration

1. **Common Configuration Issues**:
   - Incorrect ngrok URL in Azure
   - Missing '/api/messages' in endpoint
   - Invalid App ID or Password
   - Incorrect validDomains in manifest

2. **Configuration Checklist**:
```plaintext
□ Ngrok HTTPS URL is correct in Azure
□ Endpoint ends with /api/messages
□ App ID matches in all locations
□ Valid domains include ngrok domain
□ Bot Framework token domain is included
□ Application properties are updated
□ Teams manifest is properly formatted
```

3. **Validation Steps**:
   - Use ngrok interface to monitor requests
   - Check application logs for errors
   - Verify Azure Bot Service status
   - Test bot in Teams web client

#### F. Security Considerations

1. **Environment Variables**:
   - Store sensitive values in environment variables
   - Use a `.env` file for local development
   - Never commit secrets to source control

2. **Example Environment Setup**:
```bash
# .env file
MICROSOFT_APP_ID=your-app-id
MICROSOFT_APP_PASSWORD=your-app-password
```

3. **Production Security**:
   - Use Azure Key Vault for secrets
   - Implement proper authentication
   - Enable audit logging

## Testing the Connection

1. **Verify Ngrok Tunnel**
   - Open `http://localhost:4040` in your browser
   - This shows the ngrok inspection interface
   - You can see all requests going through the tunnel

2. **Test Bot Connection**
   - In Teams, send a message to your bot
   - Check the ngrok interface for the incoming request
   - Verify your bot receives and responds to the message

## Common Issues and Solutions

### 1. Bot Not Responding

**Problem**: Teams can't reach your bot
**Solutions**:
- Verify ngrok is running
- Check if the Azure endpoint matches your ngrok URL
- Ensure the URL ends with '/api/messages'
- Restart ngrok and update endpoints

### 2. URL Changes

**Problem**: Ngrok URL changes after restart
**Solutions**:
- Update Azure bot endpoint
- Update Teams manifest if needed
- Consider ngrok paid plan for persistent URLs

### 3. SSL Certificate Warnings

**Problem**: SSL certificate issues
**Solution**:
- Ngrok provides valid SSL certificates automatically
- No additional configuration needed
- If persisting, clear Teams cache

## Best Practices

1. **Development Workflow**
   - Keep ngrok running throughout development
   - Use the web interface to debug requests
   - Update endpoints immediately when URL changes

2. **Security**
   - Don't expose sensitive data during development
   - Use environment variables for secrets
   - Regularly check ngrok logs for unauthorized access

3. **Efficiency**
   - Create a batch script to start both bot and ngrok
   - Keep endpoint URLs easily accessible
   - Document your ngrok URL in development notes

## Batch Script Example

Create `start-dev.bat`:
```batch
@echo off
start cmd /k "cd /d path/to/your/bot && mvn spring-boot:run"
start cmd /k "ngrok http 3978"
start http://localhost:4040
```

## Production Considerations

Remember that ngrok is only for development. In production:
1. Deploy your bot to a proper hosting service
2. Use a stable HTTPS endpoint
3. Remove ngrok-related configurations
4. Update the bot endpoint to your production URL

## Additional Resources

- [Ngrok Documentation](https://ngrok.com/docs)
- [Microsoft Teams Bot Documentation](https://docs.microsoft.com/en-us/microsoftteams/platform/bots/how-to/debug/locally-with-an-ide)
- [Bot Framework Documentation](https://docs.microsoft.com/en-us/azure/bot-service/)

## Support

If you encounter issues:
1. Check ngrok logs at `http://localhost:4040`
2. Verify your bot is running locally
3. Ensure all endpoints are correctly updated
4. Review the Azure Bot Service logs
