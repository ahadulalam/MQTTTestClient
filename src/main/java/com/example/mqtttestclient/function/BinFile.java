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
    public static byte[][] realData;

    public static int allDataHalfLength;
    public static int numberOfPacket;
    public static int flag;
    public static Integer firmwarePacketSize = 30000;

    public boolean readBinFile(){
        try (
                InputStream inputStream = new FileInputStream("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/firmware.bin");
                //OutputStream outputStream = new FileOutputStream("output.bin");
        ) {

            //long fileSize = new File("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/firmware8.bin").length();
            //byte[] allBytes = new byte[(int) fileSize];
            byte[] allData = Files.readAllBytes(Paths.get("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/firmware.bin"));

            allDataHalfLength = allData.length;
            int allDataFullLength = allDataHalfLength * 2;
            numberOfPacket = allDataFullLength / firmwarePacketSize;
            if(allDataFullLength % firmwarePacketSize != 0){
                numberOfPacket++;
            }
            flag = 0;

            realData = new byte[numberOfPacket][];

            int j = 0; int k = 0;
            for (int i = 0; i < allDataHalfLength; i++, k+=2) {
                byte[] conversionResponse = conversion.oneByteToTwoByte(allData[i]);

                //Managing insert value in 2D array
                if(k == firmwarePacketSize){
                    j++;
                    k=0;
                    //System.out.println("J = "+j+" K = "+k);
                }

                if(j == (numberOfPacket-1) && k == 0){
                    realData[j] = new byte[allDataFullLength - (i*2)];
                }else if(k == 0){
                    realData[j] = new byte[firmwarePacketSize];
                }
                realData[j][k] = conversionResponse[0];
                realData[j][k+1] = conversionResponse[1];

                /*byteArrays.add(conversionResponse[0]);
                byteArrays.add(conversionResponse[1]);*/
            }
            /*for (int i = 0; i < byteArrays.size(); i++) {
                System.out.print(byteArrays.get(i)+" ");
            }*/
            System.out.println();
            System.out.println("File and Array Information");
            System.out.println("Actual File Size: "+allDataFullLength);
            System.out.println("Array Size: "+realData.length);
            System.out.println("2D Array Size of position:"+(1)+" Data Length: "+realData[0].length);
            System.out.println();

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public byte[] readBinFileStart(Integer sourceId){

        byte[] publishPayload;
        if(readBinFile()){
            publishPayload = conversion.oneByteToFourByte(allDataHalfLength);
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
        if(numberOfPacket <= flag){
            return readBinFileEnd(sourceId);
        }else{
            byte[] publishPacket = new byte[]{};
            try {
                //Create Publish Packet
                publishPacket = packetFormat.createPacketFormat(14+(realData[flag].length / 2),52, 64250, sourceId, (realData[flag].length / 2), realData[flag]);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            flag++;
            System.out.println("Number of Packet: "+numberOfPacket+" Send Packet: "+(flag+1));
            return publishPacket;
        }
        /*if(byteArrays.isEmpty()){
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
            System.out.println("Successfully send: "+arrayLength);
            System.out.println("Remaining Bytes: "+byteArrays.size());
            System.out.println();

            byte[] publishPacket = new byte[]{};
            try {
                //Create Publish Packet
                publishPacket = packetFormat.createPacketFormat(14+(publishPayload.length / 2),52, 64250, sourceId, (publishPayload.length / 2), publishPayload);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return publishPacket;
        }*/
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
