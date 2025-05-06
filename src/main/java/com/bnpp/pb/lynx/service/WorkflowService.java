package com.bnpp.pb.lynx.service;

import com.bnpp.pb.lynx.Events;
import com.bnpp.pb.lynx.States;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final StateMachineFactory<States, Events> stateMachineFactory;
    private final StateMachinePersister<States, Events, String> persister;

    public States startProcess(String machineId) {
        log.info("Starting process for machine ID: {}", machineId);
        StateMachine<States, Events> stateMachine = getStateMachine(machineId);
        
        if (stateMachine.getState().getId() == States.START) {
            stateMachine.sendEvent(Events.START_PROCESS);
            return persistAndGetState(stateMachine, machineId);
        } else {
            log.warn("Cannot start process - machine is not in START state. Current state: {}", 
                    stateMachine.getState().getId());
            return stateMachine.getState().getId();
        }
    }
    
    public States getCurrentState(String machineId) {
        StateMachine<States, Events> stateMachine = getStateMachine(machineId);
        return stateMachine.getState().getId();
    }
    
    public States resetToStart(String machineId) {
        log.info("Resetting machine to START state: {}", machineId);
        StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().clear();
        stateMachine.stopReactively().block();
        stateMachine.getStateMachineAccessor().doWithAllRegions(accessor -> 
            accessor.resetStateMachine(new org.springframework.statemachine.support.DefaultStateMachineContext<>(
                States.START, null, null, null
            ))
        );
        
        return persistAndGetState(stateMachine, machineId);
    }
    
    public States reportError(String machineId, String errorMessage) {
        log.error("Error in process for machine ID: {} - {}", machineId, errorMessage);
        StateMachine<States, Events> stateMachine = getStateMachine(machineId);
        stateMachine.getExtendedState().getVariables().put("ERROR_MESSAGE", errorMessage);
        stateMachine.sendEvent(Events.ERROR_OCCURRED);
        
        return persistAndGetState(stateMachine, machineId);
    }
    
    private StateMachine<States, Events> getStateMachine(String machineId) {
        StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine();
        try {
            return persister.restore(stateMachine, machineId);
        } catch (Exception e) {
            log.error("Failed to restore state machine: {}", machineId, e);
            return stateMachine;
        }
    }
    
    private States persistAndGetState(StateMachine<States, Events> stateMachine, String machineId) {
        try {
            persister.persist(stateMachine, machineId);
            return stateMachine.getState().getId();
        } catch (Exception e) {
            log.error("Failed to persist state machine: {}", machineId, e);
            return stateMachine.getState().getId();
        }
    }
} 