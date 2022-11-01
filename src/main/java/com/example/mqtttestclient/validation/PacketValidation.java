package com.example.mqtttestclient.validation;

import com.example.mqtttestclient.function.Conversion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Service
public class PacketValidation {
    @Autowired
    private Conversion conversion;

    public boolean packetValidate(byte[] bytes) {
        Integer startOfFrameByte = conversion.twoByteToOneInteger(bytes[0], bytes[1]);
        //Check Packet Starting Byte (HEX) 5B = (int) 91
        if (startOfFrameByte != 91) {
            return false;
        }

        Integer packetLength = conversion.fourByteToOneInteger(bytes[2],bytes[3],bytes[4],bytes[5]);
        //Check is twice packet length equal to MQTT receive length
        if((packetLength * 2) != bytes.length){
            return false;
        }

        Integer endOfFrameByte = conversion.twoByteToOneInteger(bytes[(packetLength*2) - 2], bytes[(packetLength*2) - 1]);
        //Check Packet end Byte (HEX) 5D = (int) 93
        if(endOfFrameByte != 93){
            return false;
        }

        return true;
    }
}
