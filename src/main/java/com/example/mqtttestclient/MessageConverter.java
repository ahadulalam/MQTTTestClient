package com.example.mqtttestclient;

import org.springframework.core.convert.converter.Converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MessageConverter implements Converter<Object, byte[]> {
    @Override
    public byte[] convert(Object source) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] byteArray = bos.toByteArray();
        return byteArray;
    }
}
