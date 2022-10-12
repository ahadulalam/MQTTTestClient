package com.example.mqtttestclient.function;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
@AllArgsConstructor
@Component
public class PacketFormat {
    @Autowired
    private Conversion conversion;


    public byte[] createPacketFormat(int packetLength, int messageId, int sourceId, int destinationId, int payloadLength, byte[] payload) throws IOException {
        byte[] bytePacketLength = conversion.oneByteToFourByte(packetLength);
        byte[] byteMessageId = conversion.oneByteToFourByte(messageId);
        byte[] byteSourceId = conversion.oneByteToFourByte(sourceId);
        byte[] byteDestinationId = conversion.oneByteToFourByte(destinationId);
        byte[] bytePayloadLength = conversion.oneByteToEightByte(payloadLength);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( 0x5 );outputStream.write( 0xB );
        outputStream.write( bytePacketLength );
        outputStream.write( byteMessageId );
        outputStream.write( byteSourceId );
        outputStream.write( byteDestinationId );
        outputStream.write( bytePayloadLength );
        outputStream.write( payload );
        outputStream.write( 0x5 ); outputStream.write( 0xD );

        byte [] bytePacket = outputStream.toByteArray();

        return  bytePacket;
        /*String packet = "";
        for (int i = 0; i < bytePacket.length; i++) {
            packet += bytePacket[i]+" ";
        }
        return packet;*/
    }
}
