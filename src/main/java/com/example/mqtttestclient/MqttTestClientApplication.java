package com.example.mqtttestclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

@SpringBootApplication
public class MqttTestClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttTestClientApplication.class, args);
    }
    /*@Bean
    public IntegrationFlow mqttOutboundFlow() {
        return f -> f.handle(new MqttPahoMessageHandler("tcp://192.168.102.5:1883", "ESP32_2"));
    }
*/
}
