package com.example.employeeService.mongoConfig;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class mongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClientFactory());
    }

    private SimpleMongoClientDatabaseFactory mongoClientFactory() {
        MongoClient mongoClient = MongoClients.create(new ConnectionString(mongoUri));
        return new SimpleMongoClientDatabaseFactory(mongoClient, getDatabaseNameFromUri());
    }

    private String getDatabaseNameFromUri() {
        return new ConnectionString(mongoUri).getDatabase();
    }
}
