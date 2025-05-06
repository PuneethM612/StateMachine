package com.bnpp.pb.lynx;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config
            .withConfiguration()
            .autoStartup(true)
            .listener(new StateMachineListenerAdapter<States, Events>() {
                @Override
                public void stateChanged(State<States, Events> from, State<States, Events> to) {
                    System.out.println("State changed from " + (from != null ? from.getId() : "null") + " to " + to.getId());
                }
            });
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states
            .withStates()
            .initial(States.START)
            .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
            .withExternal()
                .source(States.START)
                .target(States.MID)
                .event(Events.TO_MID)
                .and()
            .withExternal()
                .source(States.MID)
                .target(States.END)
                .event(Events.TO_END);
    }
} 