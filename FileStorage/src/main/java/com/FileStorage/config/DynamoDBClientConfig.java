package com.FileStorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Configuration
public class DynamoDBClientConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(@Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
                                                         @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey){
        DynamoDbClient dynamoDbClient= DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create((AwsBasicCredentials.create(accessKey,secretKey))))
                .build();
//        System.out.println("Dynamo db connected");
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}
