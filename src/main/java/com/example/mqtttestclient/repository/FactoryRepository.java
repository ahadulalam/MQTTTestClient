package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.Factory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactoryRepository extends JpaRepository<Factory, Long> {
}
