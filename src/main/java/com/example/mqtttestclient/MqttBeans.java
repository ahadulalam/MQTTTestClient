package com.example.mqtttestclient;

import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.function.PacketFormat;
import com.example.mqtttestclient.repository.*;
import com.example.mqtttestclient.service.DeviceService;
import com.example.mqtttestclient.service.MqttService;
import com.example.mqtttestclient.validation.PacketValidation;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.*;
import java.util.*;

@Configuration
public class MqttBeans implements MqttGateway{

    @Autowired
    private Conversion conversion;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private MqttService mqttService;
    @Autowired
    private PacketFormat packetFormat;
    @Autowired
    private PacketValidation packetValidation;

    @Autowired
    MqttGateway mqttGateway;
    public static int id =3;

    //Map for photo
    Map<String, Integer> map = new HashMap<>();

    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] {"tcp://192.168.137.169:1883"});
        options.setUserName("admin");
        String pass = "123456";
        options.setPassword(pass.toCharArray());
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("ServerIn",mqttClientFactory(),"/PC_1/#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(){
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
                System.out.println(topic);
                byte[] bytes = message.getPayload().toString().getBytes();
                System.out.println();

                //Packet Validation
                //If Packet Valid
                if(packetValidation.packetValidate(bytes)){
                    Publisher publisher = new Publisher();
                    byte[] publishPacket = new byte[]{};
                    try {
                        publishPacket = mqttService.mqttRequest(bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //Publish to MQTT
                    try {
                        publisher.publish("/registration/ID",publishPacket,1,false,mqttClientFactory());
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                //If packet not valid
                else{
                    Publisher publisher = new Publisher();
                    byte[] publishPacket = new byte[]{0x0, 0x0, 0x0,0x0};
                    try {
                        publishPacket = mqttService.mqttRequest(bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut",mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("/PC_1/#");
        return messageHandler;
    }

    @Override
    public void sendToMqtt(String data, String topic) {

    }

    public static byte[] parsePayload(byte[] payload) {
        byte[] result = new byte[payload.length/2];
        for(int i=0,j=0; i < payload.length; i += 2) {
            int temp = 0;
            temp = (temp | payload[i]);
            temp = ((temp << 4) | payload[i+1]);
            result[j] = (byte) temp;
            j++;
        }
        return result;

    }
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    private static void writeBytesToFile(String fileOutput, byte[] bytes)
            throws IOException {

        try (FileOutputStream fos = new FileOutputStream(fileOutput)) {
            fos.write(bytes);
        }

    }

}
