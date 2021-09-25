package com.igorkhromov.portsadapters.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService sheduler = Executors.newScheduledThreadPool(1);
    private final AtomicLong counter = new AtomicLong(0L);

    @Value(value = "${kafka.topic.name.search}")
    private String searchTopicName;

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.sheduler.scheduleWithFixedDelay(() -> {
            sendSearchMessage("Message #" + counter.addAndGet(1L));
            }, 5L, 1L, TimeUnit.SECONDS);
    }

    public void sendSearchMessage(String message) {
        kafkaTemplate.send(searchTopicName, message).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable e) {
                log.info("Unable to send message=[" + message + "] due to : " + e.getMessage());
            }
        });
    }
}
