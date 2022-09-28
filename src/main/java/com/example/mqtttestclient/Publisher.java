package com.example.mqtttestclient;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

public class Publisher {
    public void publish(final String topic, final String payload, int qos, boolean retained, MqttPahoClientFactory clientFactory)
            throws MqttPersistenceException, MqttException {
        IMqttClient client = null;
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retained);
        client = clientFactory.getClientInstance("tcp://192.168.101.240:1883","admin");
        client.connect();
        client.publish(topic, mqttMessage);
        //Mqtt.getInstance().publish(topic, mqttMessage);


        //Mqtt.getInstance().disconnect();
    }
}
