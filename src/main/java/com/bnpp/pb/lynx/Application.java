package com.bnpp.pb.lynx;

import com.bnpp.pb.lynx.Events;
import com.bnpp.pb.lynx.States;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class Application {

    private final StateMachine<States, Events> stateMachine;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return args -> {
            log.info("Starting state machine demo...");
            
            // Start the state machine
            stateMachine.startReactively().subscribe();
            
            // Send events to trigger state transitions
            stateMachine.sendEvent(Mono.just(Events.START_PROCESS)).subscribe();
            
            // Note: The state machine will automatically transition through the states
            // based on the actions defined in StateMachineConfig
        };
    }
} 