package com.example.mqtttestclient.function;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
@Component
public class Conversion {

    public Integer byteToDecimal(byte hex){
        String s = String.valueOf(hex);
        return Integer.parseInt(s,16);
    }

    public byte twoByteToOneByte(byte one, byte two){
        return (byte) ((one << 4) | two);
    }

    public Integer twoByteToOneInteger(byte one, byte two){
        return ((one << 4) | two);
    }

    public byte fourByteToOneByte(byte one, byte two, byte three, byte four){
        byte a = (byte)((one << 4) | two);
        byte b = (byte)((three << 4) | four);
        return (byte) ((a << 4) | b);
    }

    public Integer fourByteToOneInteger(byte one, byte two, byte three, byte four){
        return (one << 12)| (two << 8) | (three << 4) | four;
    }

    public String byteArrayToString(byte[] bytes){
        String byteString = new String();
        for(int i = 0; i < bytes.length; i++){
            byteString += Integer.toHexString(bytes[i] & 0xFF);
        }
        return byteString;
    }
    /*public byte twoStringOneByte(String a, String b){
        byte c = (byte) Integer.parseInt(a, 16);
        return (byte) ((a << 4) | b);
    }*/
}
