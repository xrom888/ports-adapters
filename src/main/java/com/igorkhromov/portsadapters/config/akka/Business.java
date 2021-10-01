package com.igorkhromov.portsadapters.config.akka;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.SendProducer;
import akka.stream.javadsl.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Business {

    private static final Logger log = LoggerFactory.getLogger(Business.class);

    private final Sink<String, CompletionStage<Done>> sink = Sink.foreach(log::info);
    private final Flow<ConsumerRecord<String, String>, String, NotUsed> flow = Flow.fromFunction(record -> record.value().toUpperCase());

    private final AtomicInteger counter = new AtomicInteger(0);

    private final ActorSystem actorSystem;
    private final Source<ConsumerRecord<String, String>, Consumer.Control> source;
    private final SendProducer<String, String> producer;

    public Business(ActorSystem actorSystem, Source<ConsumerRecord<String, String>, Consumer.Control> source, SendProducer<String, String> producer) {
        this.actorSystem = actorSystem;
        this.source = source;
        this.producer = producer;
    }

    @PostConstruct
    private void init() {
        source.via(flow)
                .toMat(sink, Keep.right())
                .run(actorSystem);
    }

    /**
     * Push 10 messages to "event" topic every 3 seconds
     */
    @Scheduled(fixedDelay = 100L)
    private void publish() {
        producer.send(new ProducerRecord<>("events", "message #" + counter.addAndGet(1)));
    }

}