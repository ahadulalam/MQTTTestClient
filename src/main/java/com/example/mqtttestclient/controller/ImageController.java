package com.example.mqtttestclient.controller;

import com.example.mqtttestclient.MqttBeans;
import com.example.mqtttestclient.Publisher;
import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.service.MqttService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Service
public class ImageController {

    @Autowired
    private MqttService mqttService;
    @Autowired
    private MqttBeans mqttBeans;

    @Autowired
    private Conversion conversion;

    @PostMapping("/image-upload")
    public void imageUpload(@RequestParam("imageFile") MultipartFile file) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/images_http/", file.getOriginalFilename());
        fileNames.append(file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
    }
    @PostMapping("/image")
    public void image(@RequestBody  byte[] bytes) throws IOException {

        Publisher publisher = new Publisher();
        byte[] publishPacket = new byte[]{};

        Integer messageId = conversion.fourByteToOneInteger(bytes[6],bytes[7],bytes[8],bytes[9]);

        try {
            publishPacket = mqttService.mqttRequest(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Publish to MQTT
        if(messageId != 7 && messageId != 5 && messageId != 9 ) {
            System.out.println("Message ID: "+messageId);
            try {
                publisher.publish("/registration/ID", publishPacket, 1, false, mqttBeans.mqttClientFactory());
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
