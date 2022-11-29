package com.example.mqtttestclient.websocket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/ws-endpoint")
    @SendTo("/topic/messages")
    public void greet(){
        System.out.println("Successful");
    }
}
