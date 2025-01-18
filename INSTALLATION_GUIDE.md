# Teams Notification Bot - Installation Guide

This guide provides detailed steps to set up and configure the Teams Notification Bot in your environment.

## System Requirements

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- Microsoft Azure Account
- Microsoft Teams Admin Access
- ngrok (for local development)

## Step-by-Step Installation

### 1. Clone and Build the Project

```bash
# Clone the repository
git clone [your-repository-url]

# Navigate to project directory
cd teams-notification-bot-java

# Build the project
mvn clean install
```

### 2. Azure Bot Registration

1. Go to [Azure Portal](https://portal.azure.com)
2. Click on "Create a resource"
3. Search for "Azure Bot" and select it
4. Fill in the required information:
   - Bot handle: Choose a unique name
   - Subscription: Select your subscription
   - Resource group: Create new or use existing
   - Pricing tier: Choose appropriate tier (F0 is free)
   - Microsoft App ID: Create new
5. Click "Review + Create" and then "Create"
6. Once created, go to the bot resource
7. Under "Settings", find and note down:
   - Microsoft App ID
   - Generate and note down the Microsoft App Password

### 3. Configure Application Properties

1. Create `application.properties` file in `src/main/resources/` if it doesn't exist
2. Add the following configurations:

```properties
server.port=3978
MicrosoftAppId=your-app-id
MicrosoftAppPassword=your-app-password

# Database Configuration (if using local database)
spring.datasource.url=jdbc:h2:file:./data/teamsbot
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

### 4. Set Up Local Development Environment

1. Download and install ngrok from [https://ngrok.com/download](https://ngrok.com/download)
2. Start ngrok to create a tunnel to your local server:

```bash
ngrok http 3978
```

3. Copy the HTTPS URL provided by ngrok (e.g., https://xxxx.ngrok.io)

### 5. Update Bot Endpoint in Azure

1. Go back to your Bot resource in Azure Portal
2. Under "Configuration", update the "Messaging endpoint":
   ```
   https://xxxx.ngrok.io/api/messages
   ```
3. Save the changes

### 6. Prepare Teams App Package

1. Navigate to `teams-manifest` directory
2. Update `manifest.json`:
   - Replace the `botId` with your Microsoft App ID
   - Update `developer` information
   - Modify `validDomains` if needed
3. Ensure you have the required icons:
   - `outline.jpeg`
   - `color.jpeg`
4. Create a ZIP file containing:
   - manifest.json
   - outline.jpeg
   - color.jpeg

### 7. Install Bot in Teams

1. Open Microsoft Teams Admin Center
2. Navigate to "Teams apps" → "Manage apps"
3. Click "Upload" and select your ZIP package
4. Follow the prompts to complete installation

### 8. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using Java
java -jar target/teams-notification-bot-1.0-SNAPSHOT.jar
```

## Verify Installation

1. Open Microsoft Teams
2. Find the bot in your installed apps
3. Send a test message to the bot
4. Test the notification API:

```bash
curl -X POST http://localhost:3978/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "hostEmail": "user@example.com",
    "message": "Test notification"
  }'
```

## Troubleshooting

### Common Issues

1. **Bot not responding**
   - Verify ngrok is running and the endpoint is updated in Azure
   - Check application logs for errors
   - Verify Microsoft App ID and Password are correct

2. **API calls failing**
   - Ensure the application is running
   - Check if the port is correct and not blocked
   - Verify the request format

3. **Teams installation issues**
   - Verify manifest.json format
   - Check if all required permissions are included
   - Ensure bot ID matches the Microsoft App ID

### Getting Help

If you encounter any issues not covered in this guide:
1. Check the application logs
2. Review Azure Bot Service documentation
3. Contact your system administrator or raise an issue in the project repository

## Security Considerations

- Keep your Microsoft App ID and Password secure
- Don't commit sensitive credentials to source control
- Use environment variables or secure configuration management
- Regularly update dependencies for security patches
- Monitor bot usage and implement rate limiting if needed
