// Copyright 2023 Kozlov Alexey

package com.example.project.config;

import lombok.Data;
import org.apache.camel.component.kafka.KafkaConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@EnableConfigurationProperties
@ConfigurationProperties("kafka")
public class KafkaConfig {

    private String bootstrapServers;
    private String requestTopic;
    private String resultTopic;
    private String statusTopic;
    private String groupId;

    @Bean
    public KafkaConfiguration kafka() {
        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
        kafkaConfiguration.setBrokers(bootstrapServers);
        return kafkaConfiguration;
    }

}
