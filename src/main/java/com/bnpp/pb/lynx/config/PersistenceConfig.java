package com.bnpp.pb.lynx.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Random;

@Configuration
@EnableJpaRepositories(basePackages = "com.bnpp.pb.lynx.repository")
@EntityScan(basePackages = "com.bnpp.pb.lynx.entity")
@EnableTransactionManagement
public class PersistenceConfig {

    @Bean
    public Random random() {
        return new Random();
    }
} 