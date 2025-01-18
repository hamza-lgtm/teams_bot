# Teams Notification Bot - Testing Guide

This guide covers different types of testing for the Teams Notification Bot.

## 1. Unit Testing

Create unit tests in `src/test/java` directory using JUnit 5 and Mockito.

### Setting Up Test Dependencies

Add these dependencies to your `pom.xml` if not already present:

```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.8.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.5.1</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Example Test Cases

1. **NotificationController Tests**:

```java
@SpringBootTest
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private NotificationBot notificationBot;
    
    @Test
    void testSendNotification() throws Exception {
        // Prepare test data
        NotificationRequest request = new NotificationRequest();
        request.setHostEmail("test@example.com");
        request.setMessage("Test message");
        
        // Mock bot response
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        when(notificationBot.sendNotification(anyString(), anyString()))
            .thenReturn(future);
        
        // Perform test
        mockMvc.perform(post("/api/notifications/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("Notification sent successfully"));
    }
}
```

2. **NotificationBot Tests**:

```java
class NotificationBotTest {
    @Mock
    private TeamsUserRepository teamsUserRepository;
    
    @Mock
    private BotFrameworkHttpAdapter adapter;
    
    @InjectMocks
    private NotificationBot notificationBot;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testSendNotification() {
        // Prepare test data
        TeamsUser user = new TeamsUser();
        user.setEmail("test@example.com");
        user.setConversationReference("{}");
        
        // Mock repository
        when(teamsUserRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(user));
            
        // Test notification sending
        CompletableFuture<Void> result = notificationBot
            .sendNotification("test@example.com", "Test message");
            
        assertNotNull(result);
        verify(teamsUserRepository).findByEmail("test@example.com");
    }
}
```

## 2. Integration Testing

### API Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testNotificationEndpoint() {
        // Prepare test data
        NotificationRequest request = new NotificationRequest();
        request.setHostEmail("test@example.com");
        request.setMessage("Integration test message");
        
        // Send request
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/notifications/send",
            request,
            String.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

## 3. Manual Testing

### Local Testing

1. Start the application locally:
```bash
mvn spring-boot:run
```

2. Use curl to test the notification endpoint:
```bash
curl -X POST http://localhost:3978/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "hostEmail": "user@example.com",
    "message": "Test notification"
  }'
```

3. Check Teams client for the notification

### Bot Framework Emulator Testing

1. Download and install [Bot Framework Emulator](https://github.com/Microsoft/BotFramework-Emulator/releases)
2. Connect to your bot:
   - Bot URL: http://localhost:3978/api/messages
   - Microsoft App ID: Your App ID
   - Microsoft App Password: Your App Password

## 4. End-to-End Testing

1. **Prerequisites**:
   - Bot deployed to Azure
   - Bot installed in Teams
   - Test user accounts in Teams

2. **Test Scenarios**:

   a. Bot Installation:
   - Install bot in Teams
   - Verify welcome message
   - Check user registration in database

   b. Notification Flow:
   - Send notification via API
   - Verify delivery in Teams
   - Check error handling

   c. User Management:
   - Add new team member
   - Remove team member
   - Update user information

## 5. Performance Testing

Use Apache JMeter to test API performance:

1. Create test plan for notification endpoint
2. Configure test parameters:
   - Number of users: 100
   - Ramp-up period: 30 seconds
   - Loop count: 10

Example JMeter test configuration:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Teams Bot Test Plan">
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
    </TestPlan>
    <!-- Add HTTP Request Defaults -->
    <!-- Add Thread Group -->
    <!-- Add HTTP Request Sampler -->
  </hashTree>
</jmeterTestPlan>
```

## 6. Security Testing

1. **Authentication Testing**:
   - Verify Bot Framework authentication
   - Test invalid credentials
   - Check token expiration handling

2. **API Security**:
   - Test CORS configuration
   - Verify rate limiting
   - Check input validation
   - Test SQL injection protection

3. **Data Security**:
   - Verify secure storage of credentials
   - Test data encryption
   - Check secure communication

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=NotificationControllerTest

# Run with coverage report
mvn verify
```

## Test Reports

Test reports can be found in:
- Unit test results: `target/surefire-reports/`
- Coverage reports: `target/site/jacoco/`
- Integration test results: `target/failsafe-reports/`
