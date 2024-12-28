# Teams Notification Bot

A Java-based Microsoft Teams bot that allows you to send notifications to your customers through Microsoft Teams.

## How It Works

1. **Bot Installation**:
   - Your customers install the bot in their Microsoft Teams account
   - They chat with the bot and type "register" to start receiving notifications
   - The bot stores their Teams user ID for future notifications

2. **Sending Notifications**:
   - You can send notifications to registered customers via the API
   - Messages are delivered instantly to their Teams account

## Setup Instructions

1. Register your bot in the Azure Portal:
   - Go to [Azure Portal](https://portal.azure.com)
   - Create a new "Azure Bot" resource
   - Note down the `MicrosoftAppId` and `MicrosoftAppPassword`

2. Configure the application:
   - Open `src/main/resources/application.properties`
   - Replace `your_app_id_here` with your Bot's App ID
   - Replace `your_app_password_here` with your Bot's password

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Creating the Teams App Package

1. Update the manifest:
   - Go to `teams-manifest/manifest.json`
   - Replace `{{BOT_ID}}` with your Bot's App ID
   - Update company information and URLs
   - Add your bot icons (outline.png and color.png)

2. Create the app package:
   - Zip the manifest.json and icon files
   - The zip file is your Teams app package

## Customer Installation

1. Share the Teams app package with your customers
2. Customers install the app in their Teams environment
3. Customers chat with the bot and type "register"
4. The bot confirms registration

## Sending Notifications

Send a notification by making a POST request to the endpoint:

```bash
curl -X POST http://your-server:3978/api/notifications/send \
-H "Content-Type: application/json" \
-d '{
    "hostEmail": "customer@example.com",
    "message": "Hello Joe, your visitor Jane has just arrived. Please proceed to the reception to collect her. Thank you."
}'
```

## Security Notes

- Store sensitive information like App ID and Password in secure configuration management
- Implement proper authentication for the API endpoints
- Use HTTPS in production
- Validate all input data
- Store customer registration data in a proper database (current implementation uses in-memory storage)
