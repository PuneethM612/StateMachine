package com.bnpp.pb.lynx.repository;

import com.bnpp.pb.lynx.entity.StateMachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateMachineRepository extends JpaRepository<StateMachineEntity, Long> {
    Optional<StateMachineEntity> findByMachineId(String machineId);
} 