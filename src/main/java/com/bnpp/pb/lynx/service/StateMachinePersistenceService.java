package com.bnpp.pb.lynx.service;

import com.bnpp.pb.lynx.Events;
import com.bnpp.pb.lynx.States;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateMachinePersistenceService implements StateMachinePersist<States, Events, String> {

    private final Map<String, StateMachineContext<States, Events>> storage = new HashMap<>();

    @Override
    public Mono<Void> write(StateMachineContext<States, Events> context, String contextObj) {
        return Mono.fromRunnable(() -> {
            log.info("Persisting state machine context: {}", context);
            storage.put(contextObj, context);
        });
    }

    @Override
    public Mono<StateMachineContext<States, Events>> read(String contextObj) {
        return Mono.fromCallable(() -> {
            StateMachineContext<States, Events> context = storage.get(contextObj);
            if (context == null) {
                log.info("No existing context found for: {}, creating new context", contextObj);
                context = new DefaultStateMachineContext<>(States.START, null, null, null);
            }
            return context;
        });
    }
} 