# Teams Notification Bot

A Microsoft Teams bot that enables sending notifications to users through a REST API.

## Prerequisites

- Java 11 or higher
- Maven
- Microsoft Azure account
- Microsoft Teams account with admin privileges
- ngrok (for local development)

## Setup Instructions

### 1. Azure Bot Registration

1. Go to [Azure Portal](https://portal.azure.com)
2. Create a new Bot Registration
3. Note down the `MicrosoftAppId` and `MicrosoftAppPassword`
4. Update these values in `src/main/resources/application.properties`

### 2. Build and Run the Bot

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/teams-notification-bot-1.0-SNAPSHOT.jar
```

### 3. Expose Local Server (Development)

```bash
# Download and install ngrok
# Run ngrok to expose your local server
ngrok http 3978
```

Note down the HTTPS URL provided by ngrok (e.g., https://xxxx.ngrok.io)

### 4. Update Bot Endpoint

1. Go to your Bot Registration in Azure Portal
2. Update the messaging endpoint to your ngrok URL:
   `https://xxxx.ngrok.io/api/messages`

### 5. Install Bot in Teams

1. Package the bot manifest:
   - Update `manifest.json` with your bot details
   - Create a ZIP file containing:
     - manifest.json
     - outline.png
     - color.png
2. Install the bot in Teams:
   - Go to Teams Admin Center
   - Upload the custom app (ZIP file)
   - Grant necessary permissions

## Using the Bot

### Send Notification

Send a POST request to `/api/notifications/send`:

```bash
curl -X POST http://localhost:3978/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "hostEmail": "user@example.com",
    "message": "Hello, your visitor has arrived!"
  }'
```

## Features

- Proactive messaging to Teams users
- User registration on bot installation
- REST API for sending notifications
- Persistent storage of user information

## Support

For support or questions, please contact your system administrator.
