package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository extends JpaRepository<Plant, Long> {
}
