package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.entity.DeviceWiseParticularSensor;
import com.example.mqtttestclient.entity.ParticularSensor;
import com.example.mqtttestclient.repository.DeviceRepository;
import com.example.mqtttestclient.repository.DeviceWiseParticularSensorRepository;
import com.example.mqtttestclient.repository.ParticularSensorRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceWiseParticularSensorService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ParticularSensorRepository particularSensorRepository;
    @Autowired
    private DeviceWiseParticularSensorRepository deviceWiseParticularSensorRepository;

    public Long createDeviceWiseParticularSensor(Long deviceId, Long particularSensorId, Long uniqueSensorId) {
        DeviceWiseParticularSensor deviceWiseParticularSensor = deviceWiseParticularSensorRepository.findByDeviceIdAndParticularSensorIdAndUniqueSensorId(uniqueSensorId, deviceId, particularSensorId).orElse(null);
        Long deviceWiseParticularSensorId;
        if(deviceWiseParticularSensor == null){
            Device device = deviceRepository.findById(deviceId).orElse(null); //TODO add validation
            ParticularSensor particularSensor = particularSensorRepository.findById(particularSensorId).orElse(null);

            DeviceWiseParticularSensor newDeviceWiseParticularSensor = new DeviceWiseParticularSensor();
            newDeviceWiseParticularSensor.setParticularSensor(particularSensor);
            newDeviceWiseParticularSensor.setUniqueSensorId(uniqueSensorId);
            newDeviceWiseParticularSensor.setDevice(device);
            deviceWiseParticularSensorId = deviceWiseParticularSensorRepository.save(newDeviceWiseParticularSensor).getId();
        }else{
            deviceWiseParticularSensorId = deviceWiseParticularSensor.getId();
        }
        return  deviceWiseParticularSensorId;

    }
}
