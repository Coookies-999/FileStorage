package com.FileStorage.Repository;

import com.FileStorage.Model.User;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class userRepository {


    private DynamoDbTable<User> usertable;

    public userRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.usertable = dynamoDbEnhancedClient.table("User",TableSchema.fromBean(User.class));
    }

    public void saveUser(User user){
        usertable.putItem(user);
    }

    public List<User> getUsers(){
        return usertable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public Optional<User> findByEmail(String email){

        //access the index created on email
        DynamoDbIndex<User> emailIndex=usertable.index("email-index");

        //This is the query condition
        QueryConditional condition=QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue(email)
                        .build()
        );

        //Execute the query
//        PageIterable<User> result=emailIndex.query(condition);

        return StreamSupport.stream(
                emailIndex.query(condition).spliterator(),
                false
        )
                .flatMap(page->page.items().stream())
                .findFirst();
    }
}
