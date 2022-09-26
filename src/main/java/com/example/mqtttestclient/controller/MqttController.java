package com.example.mqtttestclient.controller;


import com.example.mqtttestclient.MqttGateway;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttController {
    @Autowired
    private MqttGateway mqttGateway;
    @PostMapping("/sendMessage")
    public ResponseEntity<?> publish(@RequestBody String mqttMessage) {
        try {
            JsonObject convertObject = new Gson().fromJson(mqttMessage,JsonObject.class);
            mqttGateway.sendToMqtt(convertObject.get("message").toString(),convertObject.get("topic").toString());
            return ResponseEntity.ok("success!");
        } catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok("fail");
        }

    }
}
