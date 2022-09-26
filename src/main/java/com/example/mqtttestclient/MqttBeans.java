package com.example.mqtttestclient;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
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
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqttBeans implements MqttGateway{
    public static int id =3;
    Map<String, Integer> map = new HashMap<>();
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] {"tcp://192.168.101.228:1883"});
        options.setUserName("admin");
        String pass = "password";
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
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("ServerIn",mqttClientFactory(),"#");
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
                System.out.println(message.getPayload());
                // MessageConverter messageConverter = new MessageConverter();
                System.out.println(message.getPayload().toString().length());
                byte[] data = message.getPayload().toString().getBytes();
                byte[] results = parsePayload(data);
                for (byte result : results) {
                    System.out.print((result & 0xff) + " ");
                }
                System.out.println();
                // byte[] data = SerializationUtils.serialize(message);
                /*String temp = "/myTopic/testpub";
                if(topic.equals(temp)) {
                    System.out.println("This is our topic");
                }*/
                if(topic.matches("/PC_1/(.*)/registration")) {
                    System.out.println((String) message.getPayload());
                    Publisher publisher = new Publisher();
                    ++id;
                    String[] words = topic.split("/");
                    try {
                        if(!map.containsKey(words[2])) {
                            map.put(words[2],id);
                            publisher.publish("/"+words[2]+"/registration/ID","id: "+id,1,false,mqttClientFactory());
                        }

                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if(topic.matches("/(.*)/Sensor/Temp")) {
                    String[] words = topic.split("/");
                    System.out.println((String) message.getPayload());
                    Publisher publisher = new Publisher();
                    try {
                        publisher.publish(topic+"/Acknowledgement","acknowledged data from "+words[1],1,false,mqttClientFactory());
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if(topic.matches("(.*)/Acknowledgement")) {
                    System.out.println(message.getPayload());
                    /*Publisher publisher = new Publisher();
                    try {
                        publisher.publish("/Acknowledgement","acknowledged",1,false,mqttClientFactory());
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }*/
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
        messageHandler.setDefaultTopic("#");
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

}
