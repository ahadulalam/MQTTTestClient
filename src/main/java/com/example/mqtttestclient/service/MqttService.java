package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.DataType;
import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.entity.ParticularSensor;
import com.example.mqtttestclient.entity.Sensor;
import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.function.PacketFormat;
import com.example.mqtttestclient.image.CreateImage;
import com.example.mqtttestclient.repository.DataTypeRepository;
import com.example.mqtttestclient.repository.DeviceRepository;
import com.example.mqtttestclient.repository.ParticularSensorRepository;
import com.example.mqtttestclient.repository.SensorRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqttService {
    @Autowired
    private Conversion conversion;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ParticularSensorService particularSensorService;
    @Autowired
    private DeviceWiseParticularSensorService deviceWiseParticularSensorService;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private DataTypeRepository dataTypeRepository;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private PacketFormat packetFormat;
    @Autowired
    private CreateImage createImage;

    public byte[] registration(String payloadString, byte[] payloadBytes){
        Device device = deviceRepository.findByName(payloadString).orElse(null);
        Long deviceId = (device == null)?0:device.getId();
        System.out.println("device id = "+deviceId);

        //Check: device add to database if not already saved
        if(deviceId == 0){
            //Get Machine id
            Integer machineId = conversion.fourByteToOneInteger(payloadBytes[4], payloadBytes[5], payloadBytes[6], payloadBytes[7]);
            deviceId = deviceService.addDevice(payloadString, Long.valueOf(machineId));
        }
        byte[] publishPayload = conversion.oneLongToFourByte(deviceId);
        //byte[] publishPayload = conversion.oneLongToFourByte(3500L);
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(16,2, 64250, 0, 02, publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }

    public byte[] configuration(byte[] payloadBytes, Integer sourceId){
        //Sensor Type
        Long sensorId = Long.valueOf(conversion.twoByteToOneInteger(payloadBytes[0], payloadBytes[1]));
        //Data Type
        Long dataTypeId = Long.valueOf(conversion.twoByteToOneInteger(payloadBytes[2], payloadBytes[3]));
        //Unique sensor Id
        Long uniqueSensorId = Long.valueOf(conversion.twoByteToOneInteger(payloadBytes[4], payloadBytes[5]));
        //System.out.println("Sensor Type = "+sensorType+"\n DataType = "+dataType);
        //Device Id
        Long deviceId = Long.valueOf(sourceId);
        //Long newDeviceSensor = deviceSensorService.addDeviceSensor(machineId, sensorId, machineId);

        //Insert into Particular Sensor and Device With Particular Sensor table
        Long particularSensorId = particularSensorService.createParticularSensor(sensorId, dataTypeId);
        Long deviceWiseParticularSensorId = deviceWiseParticularSensorService.createDeviceWiseParticularSensor(deviceId, particularSensorId, uniqueSensorId);

        byte[] publishPayload = conversion.oneLongToFourByte(deviceWiseParticularSensorId);
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(16,4, 64250, sourceId, 04, publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }

    public byte[] mqttRequest(Integer messageId,Integer sourceId, Integer destinationId, String payloadString,  byte[] payloadBytes) throws IOException {
        //MessageId = 1 for Registration
        if (messageId == 1) {
            return registration(payloadString, payloadBytes);
        }
        //Message id: 3 for Configuration Device
        else if (messageId == 3) {
            return  configuration(payloadBytes, sourceId);
        }
        //Message id: 5 for image data begin
        else if (messageId == 5) {
            return  createImage.imageDataBegin(sourceId);
        }
        //Message id: 7 for image data
        else if (messageId == 7) {
            return  createImage.imageDataStore(sourceId, payloadBytes);
        }
        else if (messageId == 9) {
            return  createImage.imageDataEnd(sourceId);
        }
        //Something went wrong
        else{
            return new byte[]{0x0,0x0,0x0,0x0};
        }
    }
}
