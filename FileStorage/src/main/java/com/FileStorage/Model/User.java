package com.FileStorage.Model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDate;


@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Data
@Builder
public class User {



    private String userId;

    private String name;

    private String email;

    private String hashedPass;

    private LocalDate createdAt;

    @DynamoDbPartitionKey  //It's like Primary key in dynamodb
    @DynamoDbAttribute("userid")
    public String getUserId(){
        return this.userId;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("email")
    @DynamoDbSecondaryPartitionKey(indexNames = "email-index")   //created Global Secondary Index GSI to query thorough email
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("password")
    public String getHashedPass() {
        return hashedPass;
    }

    @DynamoDbAttribute("createddate")
    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
