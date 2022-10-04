package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.DataType;
import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.entity.DeviceSensorDataType;
import com.example.mqtttestclient.entity.Sensor;
import com.example.mqtttestclient.repository.DataTypeRepository;
import com.example.mqtttestclient.repository.DeviceRepository;
import com.example.mqtttestclient.repository.DeviceSensorDataTypeRepository;
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
public class DeviceSensorDataTypeService {
    @Autowired
    private DeviceSensorDataTypeRepository deviceSensorDataTypeRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private DataTypeRepository dataTypeRepository;

    public Long addDeviceSensorDataType(Long deviceId, Long sensorId, Long dataTypeId){
        Device device = deviceRepository.findById(deviceId).get();
        Sensor sensor = sensorRepository.findById(sensorId).get();
        DataType dataType = dataTypeRepository.findById(dataTypeId).get();

        DeviceSensorDataType deviceSensorDataType = new DeviceSensorDataType();
        deviceSensorDataType.setDevice(device);
        deviceSensorDataType.setSensor(sensor);
        deviceSensorDataType.setDataType(dataType);

        return deviceSensorDataTypeRepository.save(deviceSensorDataType).getId();
    }
}
