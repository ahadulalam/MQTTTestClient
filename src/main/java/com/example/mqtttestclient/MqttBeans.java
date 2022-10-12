package com.example.mqtttestclient;

import com.example.mqtttestclient.function.Conversion;
import com.example.mqtttestclient.function.PacketFormat;
import com.example.mqtttestclient.repository.*;
import com.example.mqtttestclient.service.DeviceService;
import com.example.mqtttestclient.service.MqttService;
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
    @Autowired
    private DataTypeRepository dataTypeRepository;
    @Autowired
    private FactoryRepository factoryRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private MqttService mqttService;
    @Autowired
    private PacketFormat packetFormat;

    @Autowired
    MqttGateway mqttGateway;
    public static int id =3;
    Map<String, Integer> map = new HashMap<>();
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] {"tcp://192.168.102.135:1883"});
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

                /*********** Retrieve Packet Data Start  *********************/
                //Packet Start
                Integer startOfFrameByte = conversion.twoByteToOneInteger(bytes[0], bytes[1]);

                //Check Packet Starting Byte (HEX) 5B = (int) 91
                if(startOfFrameByte == 91) {
                    System.out.println("Packet start");

                    //Packet Length
                    Integer packetLength = conversion.fourByteToOneInteger(bytes[2],bytes[3],bytes[4],bytes[5]);
                    System.out.println("Packet Length: "+packetLength);

                    //Check is twice packet length equal to MQTT receive length
                    if((packetLength * 2) == bytes.length){
                        //End of Bytes
                        Integer endOfFrameByte = conversion.twoByteToOneInteger(bytes[(packetLength*2) - 2], bytes[(packetLength*2) - 1]);
                        System.out.println("End of Packet: "+endOfFrameByte);

                        //Check Packet end Byte (HEX) 5D = (int) 93
                        if(endOfFrameByte == 93){
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
                            Integer metaData = conversion.eightByteToOneInteger(bytes[18],bytes[19], bytes[20],bytes[21], bytes[22],bytes[23], bytes[24],bytes[25]);
                            System.out.println("Payload Metadata: "+metaData);

                            byte[] payloadBytes = Arrays.copyOfRange(bytes, 26, bytes.length-2);
                            String payloadString = conversion.byteArrayToString(payloadBytes);
                            System.out.println(payloadString);
                            Publisher publisher = new Publisher();
                            String[] words = topic.split("/");

                            /*********** Retrieve Packet Data End  *********************/

                            byte[] publishPacket = new byte[]{};
                            try {
                                publishPacket = mqttService.mqttRequest(messageId,sourceId,destinationId,  payloadString, payloadBytes);
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
