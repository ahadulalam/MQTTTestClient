package com.example.mqtttestclient.service;

import com.example.mqtttestclient.model.Device;
import com.example.mqtttestclient.repository.DeviceRepository;
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

    public Long addDevice(String name){
        Device device = new Device();
        device.setName(name);
        return deviceRepository.save(device).getId();
    }
}
