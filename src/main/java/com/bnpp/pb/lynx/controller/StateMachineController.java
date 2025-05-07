package com.bnpp.pb.lynx.controller;

import com.bnpp.pb.lynx.Events;
import com.bnpp.pb.lynx.States;
import com.bnpp.pb.lynx.service.StateMachinePersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/statemachine")
@RequiredArgsConstructor
@Slf4j
public class StateMachineController {

    private final StateMachine<States, Events> stateMachine;
    private final StateMachinePersister<States, Events, String> persister;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startProcess() {
        try {
            String machineId = UUID.randomUUID().toString();
            stateMachine.startReactively().subscribe();
            persister.persist(stateMachine, machineId).subscribe();
            
            Map<String, Object> response = new HashMap<>();
            response.put("machineId", machineId);
            response.put("currentState", stateMachine.getState().getId());
            response.put("message", "Process started successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting process", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to start process: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{machineId}/event/{event}")
    public ResponseEntity<Map<String, Object>> sendEvent(
            @PathVariable String machineId,
            @PathVariable Events event) {
        try {
            persister.restore(stateMachine, machineId).subscribe();
            
            stateMachine.sendEvent(Mono.just(event)).subscribe();
            persister.persist(stateMachine, machineId).subscribe();
            
            Map<String, Object> response = new HashMap<>();
            response.put("machineId", machineId);
            response.put("currentState", stateMachine.getState().getId());
            response.put("event", event);
            response.put("message", "Event processed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing event", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to process event: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{machineId}/state")
    public ResponseEntity<Map<String, Object>> getCurrentState(@PathVariable String machineId) {
        try {
            persister.restore(stateMachine, machineId).subscribe();
            
            Map<String, Object> response = new HashMap<>();
            response.put("machineId", machineId);
            response.put("currentState", stateMachine.getState().getId());
            response.put("extendedState", stateMachine.getExtendedState().getVariables());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting current state", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to get current state: " + e.getMessage()
            ));
        }
    }
} 