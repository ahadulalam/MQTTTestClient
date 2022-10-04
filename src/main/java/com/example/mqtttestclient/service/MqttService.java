package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.function.PacketFormat;
import com.example.mqtttestclient.repository.DeviceRepository;
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
    private DeviceService deviceService;
    @Autowired
    private PacketFormat packetFormat;

    @Autowired
    private DeviceSensorDataTypeService deviceSensorDataTypeService;

    public byte[] registration(String payloadString, byte[] payloadBytes){
        Device device = deviceRepository.findByName(payloadString).orElse(null);
        Long deviceId = (device == null)?0:device.getId();
        System.out.println("device id = "+deviceId);

        //Check: device ad to database if not already saved
        if(deviceId == 0){
            //Get Machine id
            Integer machineId = conversion.fourByteToOneInteger(payloadBytes[4], payloadBytes[5], payloadBytes[6], payloadBytes[7]);
            deviceId = deviceService.addDevice(payloadString, Long.valueOf(machineId));
        }
        byte[] publishPayload = conversion.oneLongToFourByte(deviceId);

        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(13,2, 64250, 0, 02, publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }

    public byte[] configuration(byte[] payloadBytes, byte[] bytes, Integer sourceId){
        //Sensor Type
        Long sensorId = Long.valueOf(conversion.fourByteToOneInteger(payloadBytes[0], payloadBytes[1], payloadBytes[2], payloadBytes[3]));
        //Data Type
        Long dataTypeId = Long.valueOf(conversion.twoByteToOneInteger(payloadBytes[4], payloadBytes[5]));
        //System.out.println("Sensor Type = "+sensorType+"\n DataType = "+dataType);
        Long machineId = Long.valueOf(sourceId);

        Long newDeviceSensorDataType = deviceSensorDataTypeService.addDeviceSensorDataType(machineId, sensorId, machineId);

        byte[] publishPayload = conversion.oneLongToFourByte(newDeviceSensorDataType);
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(13,2, 64250, 0, 02, publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }

    public byte[] mqttRequest(String payloadString, Integer messageId, byte[] payloadBytes, byte[] bytes, Integer sourceId) throws IOException {
        //MessageId = 1 for Registration
        if (messageId == 1) {
            return registration(payloadString, payloadBytes);
        }
        //Message id: 3 for Configuration Device
        else if (messageId == 3) {
            return  configuration(payloadBytes, bytes, sourceId);
        }
        //Something went wrong
        else{
            return new byte[]{0x0,0x0,0x0,0x0};
        }
    }
}
