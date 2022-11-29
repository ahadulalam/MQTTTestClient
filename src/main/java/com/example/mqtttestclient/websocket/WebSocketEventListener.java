package com.example.mqtttestclient.websocket;

import lombok.extern.apachecommons.CommonsLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class WebSocketEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(final SessionConnectedEvent event){
        LOGGER.info("We have new connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event){

        LOGGER.info("We have disconnection");

        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        simpMessageSendingOperations.convertAndSend("/topic/public", "Done");
    }
}
