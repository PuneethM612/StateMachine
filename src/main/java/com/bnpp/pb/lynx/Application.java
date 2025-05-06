package com.bnpp.pb.lynx;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner(StateMachine<States, Events> stateMachine) {
        return args -> {
            System.out.println("Starting state machine...");
            stateMachine.start();
            
            System.out.println("Sending MID event...");
            stateMachine.sendEvent(Events.TO_MID);
            
            System.out.println("Sending END event...");
            stateMachine.sendEvent(Events.TO_END);
        };
    }
} 