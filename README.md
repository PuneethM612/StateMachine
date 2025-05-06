# Spring State Machine Workflow

A comprehensive workflow implementation using Spring State Machine. This project demonstrates a workflow with multiple states, automatic transitions, error handling, and database persistence.

## Workflow States

- **START**: Initial state when a workflow is created
- **PROCESSING**: First intermediate state where data processing occurs
- **VALIDATING**: Second intermediate state where validation occurs
- **END**: Final state indicating successful workflow completion
- **ERROR**: Final state indicating workflow failure

## Features

- Automatic transitions through all states
- Database persistence for state machine context and transitions
- Error handling with descriptive error messages
- RESTful API endpoints to trigger and monitor workflows
- Console logging for all transitions and events

## Requirements

- Java 8
- Maven
- PostgreSQL (or H2 for in-memory testing)

## Configuration

Database connection properties can be configured in `application.properties`. By default, the application is set up to use PostgreSQL, but it can be configured to use H2 for testing purposes.

## Running the Application

```bash
# With Maven
mvn spring-boot:run

# As a JAR file
mvn clean package
java -jar target/state-machine-demo-1.0-SNAPSHOT.jar
```

## API Endpoints

- `POST /api/workflow/start` - Start a new workflow process
- `GET /api/workflow/{machineId}` - Get the current state of a workflow
- `POST /api/workflow/{machineId}/reset` - Reset a workflow to the START state
- `POST /api/workflow/{machineId}/error` - Manually report an error for a workflow

## Example Usage

1. Start a new workflow:

   ```bash
   curl -X POST http://localhost:8080/api/workflow/start
   ```

   Response:

   ```json
   {
     "machineId": "123e4567-e89b-12d3-a456-426614174000",
     "state": "START",
     "message": "Process started successfully"
   }
   ```

2. Check workflow status:
   ```bash
   curl -X GET http://localhost:8080/api/workflow/123e4567-e89b-12d3-a456-426614174000
   ```
   Response:
   ```json
   {
     "machineId": "123e4567-e89b-12d3-a456-426614174000",
     "state": "PROCESSING"
   }
   ```

## Project Structure

- `States.java` - Enum defining all states in the workflow
- `Events.java` - Enum defining events that trigger state transitions
- `StateMachineConfig.java` - Configuration class with state machine setup
- `StateMachinePersistenceService.java` - Service for persisting state machine state
- `WorkflowService.java` - Service for managing workflow processes
- `WorkflowController.java` - REST controller exposing workflow endpoints
- `StateMachineEntity.java` - JPA entity for persisting state machine data
- `Application.java` - Main application class with demo runner
