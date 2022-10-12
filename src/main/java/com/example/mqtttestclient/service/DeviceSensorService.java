/*
package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.DataType;
import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.entity.DeviceSensor;
import com.example.mqtttestclient.entity.Sensor;
import com.example.mqtttestclient.repository.DataTypeRepository;
import com.example.mqtttestclient.repository.DeviceRepository;
import com.example.mqtttestclient.repository.DeviceSensorRepository;
import com.example.mqtttestclient.repository.SensorRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceSensorService {
    @Autowired
    private DeviceSensorRepository deviceSensorDataTypeRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private DataTypeRepository dataTypeRepository;

    public Long addDeviceSensor(Long deviceId, Long sensorId, Long dataTypeId){
        Device device = deviceRepository.findById(deviceId).get();
        DataType dataType = dataTypeRepository.findById(dataTypeId).get();
        //Sensor sensor = sensorRepository.findById(sensorId).get();
        String fetchDataTypeName = dataType.getName();
        fetchDataTypeName = fetchDataTypeName.substring(0, 1).toUpperCase() + fetchDataTypeName.substring(1);
        System.out.println("Name: "+fetchDataTypeName);
        Sensor sensor = sensorRepository.findByIdAndDataTypeName("Pressure Sensor", fetchDataTypeName).orElse(null);
        System.out.println("Sensor: "+sensor);
        Sensor newSensor = new Sensor();
        if(sensor != null){
            newSensor = sensor;
        }else{
            Sensor fetchSensor = sensorRepository.findById(sensorId).get();
            String dataTypeName = dataType.getName();
            dataTypeName = dataTypeName.substring(0, 1).toUpperCase() + dataTypeName.substring(1);

            newSensor.setName(fetchSensor.getName());
            newSensor.setDataType(com.example.mqtttestclient.customizeEnum.DataType.valueOf(dataTypeName));

            newSensor = sensorRepository.save(newSensor);
        }

        DeviceSensor deviceSensorDataType = new DeviceSensor();
        deviceSensorDataType.setDevice(device);
        deviceSensorDataType.setSensor(newSensor);
        //deviceSensorDataType.setDataType(dataType);

        return deviceSensorDataTypeRepository.save(deviceSensorDataType).getId();
    }
}
*/
