package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.function.BinFile;
import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.function.PacketFormat;
import com.example.mqtttestclient.image.CreateImage;
import com.example.mqtttestclient.repository.DataTypeRepository;
import com.example.mqtttestclient.repository.DeviceRepository;
import com.example.mqtttestclient.repository.SensorRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    @Autowired
    private BinFile binFile;

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
            publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),2, 64250, 0, (publishPayload.length / 2), publishPayload);

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

        //Get Particular Sensor and Device With Particular Sensor id
        Long particularSensorId = particularSensorService.createParticularSensor(sensorId, dataTypeId);
        Long deviceWiseParticularSensorId = deviceWiseParticularSensorService.createDeviceWiseParticularSensor(deviceId, particularSensorId, uniqueSensorId);

        byte[] publishPayload = conversion.oneLongToFourByte(deviceWiseParticularSensorId);
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),4, 64250, sourceId, (publishPayload.length / 2), publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }

    public byte[] mqttRequest(byte[] bytes) throws IOException {

        /*********** Retrieve Packet Data Start  *********************/

        //Packet Start
        Integer startOfFrameByte = conversion.twoByteToOneInteger(bytes[0], bytes[1]);
        System.out.println("Packet start");

        //Packet Length
        Integer packetLength = conversion.fourByteToOneInteger(bytes[2],bytes[3],bytes[4],bytes[5]);
        System.out.println("Packet Length: "+packetLength);

        //End of Frame
        Integer endOfFrameByte = conversion.twoByteToOneInteger(bytes[(packetLength*2) - 2], bytes[(packetLength*2) - 1]);
        System.out.println("End of Packet: "+endOfFrameByte);

        //Message ID
        Integer messageId = conversion.fourByteToOneInteger(bytes[6],bytes[7],bytes[8],bytes[9]);
        System.out.println("Message ID: "+messageId);

        //Source ID
        Integer sourceId = conversion.fourByteToOneInteger(bytes[10],bytes[11],bytes[12],bytes[13]);
        System.out.println("Source ID: "+sourceId);

        //Destination ID
        Integer destinationId = conversion.fourByteToOneInteger(bytes[14],bytes[15],bytes[16],bytes[17]);
        System.out.println("Destination ID: "+destinationId);

        //Payload Metadata
        Integer metaData = conversion.eightByteToOneInteger(bytes[18],bytes[19], bytes[20],bytes[21], bytes[22],bytes[23], bytes[24],bytes[25]);
        System.out.println("Payload Metadata: "+metaData);

        byte[] payloadBytes = Arrays.copyOfRange(bytes, 26, bytes.length-2);
        String payloadString = conversion.byteArrayToString(payloadBytes);
        System.out.println(payloadString);

        /*********** Retrieve Packet Data End  *********************/

        //MessageId: 1 for Registration Packet
        if (messageId == 1) {
            return registration(payloadString, payloadBytes);
        }
        //Message id: 3 for Configuration Device Packet
        else if (messageId == 3) {
            return  configuration(payloadBytes, sourceId);
        }
        //Message id: 5 for image data begin Packet
        else if (messageId == 5) {
            return  createImage.imageDataBegin(sourceId);
        }
        //Message id: 7 for image data store Packet
        else if (messageId == 7) {
            return  createImage.imageDataStore(sourceId, payloadBytes);
        }
        //Message id: 9 for image data end Packet
        else if (messageId == 9) {
            return  createImage.imageDataEnd(sourceId, metaData);
        }
        else if (messageId == 49) {
            return  binFile.readBinFileStart(sourceId);
        }
        else if (messageId == 51) {
            return  binFile.sendBinFileData(sourceId);
        }
        //Message id is not right
        else{
            return new byte[]{0x0,0x0,0x0,0x0};
        }
    }
}
