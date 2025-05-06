# Spring State Machine Demo

A simple demonstration of Spring State Machine showing a basic workflow with three states:

- START
- MID
- END

## Requirements

- Java 8
- Maven

## How to Run

```bash
mvn spring-boot:run
```

## Project Structure

- `States.java` - Enum defining all states in the workflow
- `Events.java` - Enum defining events that trigger state transitions
- `StateMachineConfig.java` - Configuration class with @EnableStateMachine annotation
- `Application.java` - Main Spring Boot application class with a runner that triggers the workflow

## Workflow

The state machine follows this flow:

1. Initial state: START
2. TO_MID event transitions from START to MID
3. TO_END event transitions from MID to END
