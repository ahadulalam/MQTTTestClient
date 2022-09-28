package com.example.mqtttestclient;

import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.model.Device;
import com.example.mqtttestclient.repository.DeviceRepository;
import com.example.mqtttestclient.service.DeviceService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqttBeans implements MqttGateway{

    @Autowired
    private Conversion conversion;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceRepository deviceRepository;
    public static int id =3;
    Map<String, Integer> map = new HashMap<>();
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] {"tcp://192.168.101.240:1883"});
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
                byte[] bytes = message.getPayload().toString().getBytes();

                System.out.println();
                //System.out.println(message.getPayload());
                // MessageConverter messageConverter = new MessageConverter();
                //System.out.println(message.getPayload().toString().length());


                /*byte[] results = parsePayload(bytes);
                for (byte result : results) {
                    System.out.print((result & 0xff) + " ");
                }*/
                // byte[] data = SerializationUtils.serialize(message);
                /*String temp = "/myTopic/testpub";
                if(topic.equals(temp)) {
                    System.out.println("This is our topic");
                }*/
                String str = "";
                for(int i = 0; i < bytes.length; i++){
                    System.out.print(bytes[i]+" ");
                    str += bytes[i]+" ";
                }
                str += " \n";

                try {
                        //File log = new File("bytearray.txt");
                        //FileWriter myWriter = new FileWriter(String.valueOf(new FileWriter(log, true)));
                        FileWriter myWriter = new FileWriter("bytearray.txt", true);
                        myWriter.write(str);

                        myWriter.close();
                        //System.out.println("Successfully wrote to the file.");
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }

                //Packet Start
                Integer startOfFrameByte = conversion.twoByteToOneInteger(bytes[0], bytes[1]);
                if(startOfFrameByte == 91) {
                    System.out.println("Packet start");
                }

                //Packet Length
                Integer packetLength = conversion.fourByteToOneInteger(bytes[2],bytes[3],bytes[4],bytes[5]);
                System.out.println("Packet Length: "+packetLength);

                //Message ID
                Integer messageId = conversion.fourByteToOneInteger(bytes[6],bytes[7],bytes[8],bytes[9]);
                System.out.println("Message ID: "+messageId);

                //Source ID
                Integer sourceId = conversion.fourByteToOneInteger(bytes[10],bytes[11],bytes[12],bytes[13]);
                System.out.println("Source ID: "+sourceId);

                //Destination ID
                Integer destinationId = conversion.fourByteToOneInteger(bytes[14],bytes[15],bytes[16],bytes[17]);
                System.out.println("Destination ID: "+destinationId);

                //Payload Metadata
                Integer metaData = conversion.twoByteToOneInteger(bytes[18],bytes[19]);
                System.out.println("Payload Metadata: "+metaData);

                byte[] payloadBytes = Arrays.copyOfRange(bytes, 20, (20+(metaData*2)));
                String payloadString = conversion.byteArrayToString(payloadBytes);
                Publisher publisher = new Publisher();
                String[] words = topic.split("/");
                Device device = deviceRepository.findByName(payloadString).orElse(null);
                Long deviceId = (device == null)?0:device.getId();


                if(topic.matches("/PC_1/(.*)/registration")) {
                    if(deviceId == 0) {
                        deviceId = deviceService.addDevice(payloadString);
                        System.out.println(deviceId);
                        ++id;
                        try {
                            publisher.publish("/"+words[2]+"/registration/ID","id: "+deviceId,1,false,mqttClientFactory());
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        try {
                            publisher.publish("/"+words[2]+"/registration/ID","id: "+deviceId,1,false,mqttClientFactory());
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    /*try {
                        if(!map.containsKey(words[2])) {
                            map.put(words[2],id);
                            publisher.publish("/"+words[2]+"/registration/ID","id: "+id,1,false,mqttClientFactory());
                        }

                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }*/
                }
                else if(topic.matches("/(.*)/Sensor/Temp")) {
                    words = topic.split("/");
                    System.out.println((String) message.getPayload());
                    publisher = new Publisher();
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

    private static void writeBytesToFile(String fileOutput, byte[] bytes)
            throws IOException {

        try (FileOutputStream fos = new FileOutputStream(fileOutput)) {
            fos.write(bytes);
        }

    }

}
