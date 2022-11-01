package com.example.mqtttestclient.function;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Component
public class BinFile {
    @Autowired
    private Conversion conversion;
    @Autowired
    private PacketFormat packetFormat;

    //For update firmware
    public static List<Byte> byteArrays = new ArrayList<Byte>();
    public static Integer firmwarePacketSize = 20000;

    public boolean readBinFile(){
        try (
                InputStream inputStream = new FileInputStream("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/firmware.bin");
                //OutputStream outputStream = new FileOutputStream("output.bin");
        ) {

            long fileSize = new File("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/firmware.bin").length();
            byte[] allBytes = new byte[(int) fileSize];
            byte[] allData = Files.readAllBytes(Paths.get("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/firmware.bin"));
            for (int i = 0; i < allData.length; i++) {
                byte[] conversionResponse = conversion.oneByteToTwoByte(allData[i]);
                byteArrays.add(conversionResponse[0]);
                byteArrays.add(conversionResponse[1]);
            }
            /*for (int i = 0; i < byteArrays.size(); i++) {
                System.out.print(byteArrays.get(i)+" ");
            }*/
            System.out.println();
            System.out.println("File and Array Information");
            System.out.println("Actual File Size: "+fileSize);
            System.out.println("Convert Array Size: "+byteArrays.size());

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public byte[] readBinFileStart(Integer sourceId){
        byteArrays.clear();
        byte[] publishPayload;
        if(readBinFile()){
            publishPayload = new byte[]{0x0, 0x0, 0x0, 0x1};
        }else{
            publishPayload = new byte[]{0x0, 0x0, 0x0, 0x0};
        }

        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),50, 64250, sourceId, (publishPayload.length / 2), publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }

    public byte[] sendBinFileData(Integer sourceId){
        //Check Array File empty or not
        if(byteArrays.isEmpty()){
            return readBinFileEnd(sourceId);
        }else{
            Integer arrayLength;
            int byteArraySize = byteArrays.size();

            if(byteArraySize > firmwarePacketSize){
                arrayLength = firmwarePacketSize;
            }else{
                arrayLength = byteArraySize;
            }

            byte[] publishPayload = new byte[arrayLength];

            for (int i = 0; i < arrayLength; i++) {
                publishPayload[i] = byteArrays.get(i);
            }
            for (int i = 0; i < arrayLength; i++) {
                byteArrays.remove(0);
            }

            byte[] publishPacket = new byte[]{};
            try {
                //Create Publish Packet
                publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),52, 64250, sourceId, (publishPayload.length / 2), publishPayload);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return publishPacket;
        }
    }

    public byte[] readBinFileEnd(Integer sourceId){

        byte[] publishPayload = new byte[]{0x0, 0x0, 0x0, 0x0};
        byte[] publishPacket = new byte[]{};
        try {
            //Create Publish Packet
            publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),54, 64250, sourceId, (publishPayload.length / 2), publishPayload);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return publishPacket;
    }
}
