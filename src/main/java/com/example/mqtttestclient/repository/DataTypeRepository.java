package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.DataType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataTypeRepository extends JpaRepository<DataType, Long> {
}
