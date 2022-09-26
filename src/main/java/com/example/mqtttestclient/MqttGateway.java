package com.example.mqtttestclient;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
//@Service
public interface MqttGateway  {

    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
