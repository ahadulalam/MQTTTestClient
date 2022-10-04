package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.DeviceSensorDataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceSensorDataTypeRepository extends JpaRepository<DeviceSensorDataType, Long> {
}
