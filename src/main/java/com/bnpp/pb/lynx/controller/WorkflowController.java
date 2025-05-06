package com.bnpp.pb.lynx.controller;

import com.bnpp.pb.lynx.States;
import com.bnpp.pb.lynx.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Slf4j
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startProcess() {
        String machineId = UUID.randomUUID().toString();
        log.info("Creating new workflow process with ID: {}", machineId);
        
        States state = workflowService.startProcess(machineId);
        
        return ResponseEntity.ok(Map.of(
            "machineId", machineId,
            "state", state,
            "message", "Process started successfully"
        ));
    }
    
    @GetMapping("/{machineId}")
    public ResponseEntity<Map<String, Object>> getProcessState(@PathVariable String machineId) {
        log.info("Retrieving state for process ID: {}", machineId);
        States state = workflowService.getCurrentState(machineId);
        
        return ResponseEntity.ok(Map.of(
            "machineId", machineId,
            "state", state
        ));
    }
    
    @PostMapping("/{machineId}/reset")
    public ResponseEntity<Map<String, Object>> resetProcess(@PathVariable String machineId) {
        log.info("Resetting process with ID: {}", machineId);
        States state = workflowService.resetToStart(machineId);
        
        return ResponseEntity.ok(Map.of(
            "machineId", machineId,
            "state", state,
            "message", "Process reset to START state"
        ));
    }
    
    @PostMapping("/{machineId}/error")
    public ResponseEntity<Map<String, Object>> reportError(
            @PathVariable String machineId,
            @RequestBody Map<String, String> payload) {
        
        String errorMessage = payload.getOrDefault("errorMessage", "Unknown error reported");
        log.error("Manual error reported for process ID: {} - {}", machineId, errorMessage);
        
        States state = workflowService.reportError(machineId, errorMessage);
        
        return ResponseEntity.ok(Map.of(
            "machineId", machineId,
            "state", state,
            "message", "Error reported successfully"
        ));
    }
} 