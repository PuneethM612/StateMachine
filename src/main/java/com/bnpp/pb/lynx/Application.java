package com.bnpp.pb.lynx;

import com.bnpp.pb.lynx.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("!production")
    public CommandLineRunner demoRunner(WorkflowService workflowService) {
        return args -> {
            log.info("=== Running State Machine Demo ===");
            
            // Create a unique ID for this workflow instance
            String machineId = UUID.randomUUID().toString();
            log.info("Created workflow with ID: {}", machineId);
            
            // Start the workflow
            log.info("Starting workflow...");
            States currentState = workflowService.startProcess(machineId);
            log.info("Current state after starting: {}", currentState);
            
            // Wait for the workflow to complete or reach error state
            while (!isTerminalState(currentState)) {
                Thread.sleep(1000);
                currentState = workflowService.getCurrentState(machineId);
                log.info("Current state: {}", currentState);
            }
            
            log.info("Workflow completed with final state: {}", currentState);
            log.info("=== Demo Complete ===");
        };
    }
    
    private boolean isTerminalState(States state) {
        return state == States.END || state == States.ERROR;
    }
} 