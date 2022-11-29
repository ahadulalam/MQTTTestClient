package com.example.mqtttestclient.service;


import com.example.mqtttestclient.entity.DataType;
import com.example.mqtttestclient.entity.ParticularSensor;
import com.example.mqtttestclient.entity.Sensor;
import com.example.mqtttestclient.repository.DataTypeRepository;
import com.example.mqtttestclient.repository.ParticularSensorRepository;
import com.example.mqtttestclient.repository.SensorRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticularSensorService {
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private DataTypeRepository dataTypeRepository;
    @Autowired
    private ParticularSensorRepository particularSensorRepository;

    public Long createParticularSensor(Long sensorId, Long dataTypeId) {
        ParticularSensor particularSensor = particularSensorRepository.findBySensorIdAndDataTypeId(sensorId, dataTypeId).orElse(null);
        Long particularSensorId;
        if(particularSensor == null){
            Sensor sensor = sensorRepository.findById(sensorId).orElse(null); //Todo add validation
            DataType dataType = dataTypeRepository.findById(dataTypeId).orElse(null);

            ParticularSensor newParticularSensor = new ParticularSensor();
            newParticularSensor.setDataType(dataType);
            newParticularSensor.setSensor(sensor);

            particularSensorId = particularSensorRepository.save(newParticularSensor).getId();
        }else{
            particularSensorId = particularSensor.getId();
        }

        return particularSensorId;

    }

}
