package com.bnpp.pb.lynx.config;

import com.bnpp.pb.lynx.Events;
import com.bnpp.pb.lynx.States;
import com.bnpp.pb.lynx.service.StateMachinePersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.util.EnumSet;
import java.util.Random;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    private final StateMachinePersistenceService persistenceService;
    private final Random random = new Random();

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config
            .withConfiguration()
            .autoStartup(true)
            .listener(stateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states
            .withStates()
            .initial(States.START)
            .states(EnumSet.allOf(States.class))
            .end(States.END)
            .end(States.ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
            .withExternal()
                .source(States.START)
                .target(States.PROCESSING)
                .event(Events.START_PROCESS)
                .action(processingAction(), errorAction())
                .and()
            .withExternal()
                .source(States.PROCESSING)
                .target(States.VALIDATING)
                .event(Events.CONTINUE_PROCESS)
                .guard(validationGuard())
                .action(validatingAction(), errorAction())
                .and()
            .withExternal()
                .source(States.VALIDATING)
                .target(States.END)
                .event(Events.FINISH_PROCESS)
                .action(completeAction())
                .and()
            .withExternal()
                .source(States.PROCESSING)
                .target(States.ERROR)
                .event(Events.ERROR_OCCURRED)
                .and()
            .withExternal()
                .source(States.VALIDATING)
                .target(States.ERROR)
                .event(Events.ERROR_OCCURRED);
    }

    @Bean
    public StateMachineListener<States, Events> stateMachineListener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                if (from != null) {
                    log.info("State changed from: {} to: {}", from.getId(), to.getId());
                } else {
                    log.info("State initialized to: {}", to.getId());
                }
            }

            @Override
            public void transition(Transition<States, Events> transition) {
                if (transition.getTarget().getId() == States.ERROR) {
                    log.error("Transition to ERROR state occurred");
                }
            }
        };
    }

    @Bean
    public StateMachinePersister<States, Events, String> persister() {
        return new DefaultStateMachinePersister<>(persistenceService);
    }
    
    private Action<States, Events> processingAction() {
        return context -> {
            log.info("Processing started...");
            try {
                // Simulate processing work
                Thread.sleep(1000);
                log.info("Processing complete, triggering next step");
                context.getStateMachine().sendEvent(Events.CONTINUE_PROCESS);
            } catch (Exception e) {
                log.error("Error in processing", e);
                context.getStateMachine().getExtendedState().getVariables()
                    .put("ERROR_MESSAGE", "Processing failed: " + e.getMessage());
                context.getStateMachine().sendEvent(Events.ERROR_OCCURRED);
            }
        };
    }
    
    private Action<States, Events> validatingAction() {
        return context -> {
            log.info("Validation started...");
            try {
                // Simulate validation
                Thread.sleep(1000);
                log.info("Validation complete, triggering completion");
                context.getStateMachine().sendEvent(Events.FINISH_PROCESS);
            } catch (Exception e) {
                log.error("Error in validation", e);
                context.getStateMachine().getExtendedState().getVariables()
                    .put("ERROR_MESSAGE", "Validation failed: " + e.getMessage());
                context.getStateMachine().sendEvent(Events.ERROR_OCCURRED);
            }
        };
    }
    
    private Guard<States, Events> validationGuard() {
        return context -> {
            // Simulate a validation check that might fail
            // 10% chance of failure for demo purposes
            boolean valid = random.nextInt(10) != 0;
            if (!valid) {
                log.warn("Validation guard check failed");
                context.getStateMachine().getExtendedState().getVariables()
                    .put("ERROR_MESSAGE", "Validation guard check failed - random condition");
                context.getStateMachine().sendEvent(Events.ERROR_OCCURRED);
            }
            return valid;
        };
    }
    
    private Action<States, Events> completeAction() {
        return context -> {
            log.info("Process completed successfully");
        };
    }
    
    private Action<States, Events> errorAction() {
        return context -> {
            Exception exception = context.getException();
            log.error("Error occurred during state transition", exception);
            context.getStateMachine().getExtendedState().getVariables()
                .put("ERROR_MESSAGE", exception != null ? exception.getMessage() : "Unknown error");
            context.getStateMachine().sendEvent(Events.ERROR_OCCURRED);
        };
    }
} 