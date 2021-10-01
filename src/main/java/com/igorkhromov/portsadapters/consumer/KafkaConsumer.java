package com.igorkhromov.portsadapters.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//@Service
public class KafkaConsumer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @KafkaListener(topics = "search", groupId = "search")
    public void listenSearchMessages(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        log.info("Received Message: " + message + ", from partition: " + partition);
    }

}