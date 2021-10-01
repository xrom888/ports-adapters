package com.igorkhromov.portsadapters.config.akka;

import akka.actor.ActorSystem;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(akka.stream.javadsl.Source.class)
public class AkkaConfig {

    private final ActorSystem system;

    public AkkaConfig() {
        system = ActorSystem.create("SpringWebAkkaStreamsSystem");
    }

    @Bean
    @ConditionalOnMissingBean(ActorSystem.class)
    public ActorSystem getActorSystem() {
        return system;
    }
}