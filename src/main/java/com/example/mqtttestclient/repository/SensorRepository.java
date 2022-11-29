package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update sensor set data_type = :dataType where id = :sensorId")
    int updateSensor(@Param("sensorId") Long sensorId,
                       @Param("dataType") String dataType);

    @Query(nativeQuery = true, value = "select * from sensor where data_type = :fetchDataTypeName and name = :sensorName")
    Optional<Sensor> findByIdAndDataTypeName(@Param("sensorName") String sensorName,
                                             @Param("fetchDataTypeName") String fetchDataTypeName);
}
