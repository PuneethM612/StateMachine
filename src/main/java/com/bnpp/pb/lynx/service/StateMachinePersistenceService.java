package com.bnpp.pb.lynx.service;

import com.bnpp.pb.lynx.States;
import com.bnpp.pb.lynx.entity.StateMachineEntity;
import com.bnpp.pb.lynx.repository.StateMachineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.bnpp.pb.lynx.Events;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateMachinePersistenceService implements StateMachinePersist<States, Events, String> {

    private final StateMachineRepository repository;

    @Override
    public void write(StateMachineContext<States, Events> context, String contextObj) {
        log.info("Persisting state machine: {} with state: {}", contextObj, context.getState());
        
        Optional<StateMachineEntity> existingEntity = repository.findByMachineId(contextObj);
        StateMachineEntity entity;
        
        if (existingEntity.isPresent()) {
            entity = existingEntity.get();
            entity.setCurrentState(context.getState());
            
            // Handle error state if applicable
            if (States.ERROR.equals(context.getState())) {
                String errorMsg = context.getExtendedState().get("ERROR_MESSAGE", String.class);
                entity.setErrorMessage(errorMsg != null ? errorMsg : "Unknown error occurred");
            }
        } else {
            entity = new StateMachineEntity();
            entity.setMachineId(contextObj);
            entity.setCurrentState(context.getState());
        }
        
        repository.save(entity);
        log.info("State machine persisted successfully");
    }

    @Override
    public StateMachineContext<States, Events> read(String contextObj) {
        log.info("Reading state machine with ID: {}", contextObj);
        
        Optional<StateMachineEntity> entityOpt = repository.findByMachineId(contextObj);
        if (entityOpt.isPresent()) {
            StateMachineEntity entity = entityOpt.get();
            log.info("Found state machine with state: {}", entity.getCurrentState());
            return new DefaultStateMachineContext<>(entity.getCurrentState(), null, null, null);
        }
        
        log.info("No state machine found, returning default state: START");
        return new DefaultStateMachineContext<>(States.START, null, null, null);
    }
} 