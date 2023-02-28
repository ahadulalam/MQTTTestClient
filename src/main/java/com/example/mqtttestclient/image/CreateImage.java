package com.example.mqtttestclient.image;

import com.example.mqtttestclient.entity.DeviceWiseParticularSensor;
import com.example.mqtttestclient.entity.Transaction;
import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.function.PacketFormat;
import com.example.mqtttestclient.repository.DeviceWiseParticularSensorRepository;
import com.example.mqtttestclient.repository.TransactionRepository;
import com.example.mqtttestclient.video.CreateVideo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Service
public class CreateImage {
    @Autowired
    private PacketFormat packetFormat;
    @Autowired
    private Conversion conversion;
    @Autowired
    private DeviceWiseParticularSensorRepository deviceWiseParticularSensorRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CreateVideo createVideo;
    public static Map<Integer, byte[]> imageData = new HashMap<>();
    public static Boolean videoBool = true;

    public byte[] imageDataBegin(Integer sourceId){
        imageData.remove(sourceId);
        byte[] publishPayload = new byte[]{0x0, 0x0, 0x0, 0x0};
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),6, 64250, sourceId, (publishPayload.length / 2), publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }
    public byte[] imageDataEnd(Integer sourceId, Integer metaData) throws IOException {
        System.out.println("Image Data");
        System.out.println("--------------------------");
        System.out.println("Image Total byte: "+ imageData.get(sourceId).length);

        byte[] imageFullData = imageData.get(sourceId);
        byte[] imageActualData = new byte[imageFullData.length/2];

        //Convert 2 byte to one byte
        for (int i = 0,j=0; i < imageFullData.length-1; i += 2) {
            byte a  = conversion.twoByteToOneByte(imageFullData[i], imageFullData[i+1]);
            //resByte[i] = a;
            imageActualData[j] = a;
            j++;
        }

        //Image name dynamic
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        LocalDateTime now = LocalDateTime.now();
        String imageName = "/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/images/"+sourceId+dtf.format(now)+".jpg";

        //Save Image to file
        String res = conversion.byteToImage(imageActualData, imageName);
        System.out.println(res);

        /*createVideo.photoQueue.add(imageName);
        System.out.println("Queue Size: "+createVideo.photoQueue.size());
        if(createVideo.photoQueue.size() > 10){
            //videoBool = false;
            //new Thread(() -> createVideo.createVideo() ).start();
            createVideo.createVideo();
        }*/
        /*if(res == "Image create successfully"){
            DeviceWiseParticularSensor deviceWiseParticularSensor = deviceWiseParticularSensorRepository.findById(metaData.longValue()).get();
            Transaction transaction = new Transaction();
            transaction.setPayload(imageName);
            transaction.setDeviceWiseParticularSensor(deviceWiseParticularSensor);
            transactionRepository.save(transaction);
        }*/

        System.out.println("--------------------------");

        //Publish result
        byte[] publishPayload = new byte[]{0x0, 0x0, 0x0, 0x0};
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),10, 64250, sourceId, (publishPayload.length / 2), publishPayload);

        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
        imageData.remove(sourceId);
        return publishPacket;
    }
    public byte[] imageDataStore(Integer sourceId, byte[] payloadBytes){
        if(!imageData.containsKey(sourceId)){
            imageData.put(sourceId, payloadBytes);
        }else{
            byte[] storeImageData = imageData.get(sourceId);

            byte[] result = new byte[storeImageData.length + payloadBytes.length];
            System.arraycopy(storeImageData, 0, result, 0, storeImageData.length);
            System.arraycopy(payloadBytes, 0, result, storeImageData.length, payloadBytes.length);

            imageData.put(sourceId, result);
        }

        byte[] publishPayload = new byte[]{0x0, 0x0, 0x0, 0x0};
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),8, 64250, sourceId, (publishPayload.length / 2), publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }
}
