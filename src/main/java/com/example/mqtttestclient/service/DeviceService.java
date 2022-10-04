package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.entity.Machine;
import com.example.mqtttestclient.repository.DeviceRepository;
import com.example.mqtttestclient.repository.MachineRepository;
import com.example.mqtttestclient.repository.SensorRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private MachineRepository machineRepository;

    public Long addDevice(String name, Long machineId){
        Machine machine = machineRepository.findById(machineId).get();
        Device device = new Device();
        device.setName(name);
        device.setMachine(machine);

        //device.setSensors(deviceInfo.getSensorIds().stream().map(sensorId -> sensorRepository.findById(sensorId).orElse(null)).collect(Collectors.toList()));
        return deviceRepository.save(device).getId();
    }
}
