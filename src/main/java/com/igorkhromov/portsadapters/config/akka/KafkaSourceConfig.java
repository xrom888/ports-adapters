package com.igorkhromov.portsadapters.config.akka;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.kafka.javadsl.SendProducer;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class KafkaSourceConfig {

    @Value("${kafka.bootstrapServers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.eventTopic}")
    private String eventTopic;

    private final ActorSystem actorSystem;

    public KafkaSourceConfig(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Bean
    public ConsumerSettings<String, String> getKafkaConsumerSettings() {
        return ConsumerSettings.create(actorSystem, new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers(kafkaBootstrapServers)
                .withGroupId("events-group")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                .withStopTimeout(Duration.ofSeconds(5));
    }

    @Bean
    public Source<ConsumerRecord<String, String>, Consumer.Control> getKafkaCommittableSource() {
        return Consumer.plainSource(getKafkaConsumerSettings(), Subscriptions.topics(eventTopic));
    }

    @Bean
    public ProducerSettings<String, String> getKafkaProducerSettings() {
        return ProducerSettings.create(actorSystem, new StringSerializer(), new StringSerializer())
                .withBootstrapServers(kafkaBootstrapServers);
    }

    @Bean
    public SendProducer<String, String> getKafkaProducer() {
        return new SendProducer<>(getKafkaProducerSettings(), actorSystem);
    }
}