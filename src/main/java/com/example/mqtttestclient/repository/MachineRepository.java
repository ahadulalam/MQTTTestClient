package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> {
}
