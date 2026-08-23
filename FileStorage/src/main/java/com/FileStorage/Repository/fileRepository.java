package com.FileStorage.Repository;

import com.FileStorage.Model.FileInfo;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class fileRepository {

    private DynamoDbTable<FileInfo> fileInfoDynamoDbTable;

    public fileRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.fileInfoDynamoDbTable = dynamoDbEnhancedClient.table("FileInfo", TableSchema.fromBean(FileInfo.class));
    }

    public void saveFile(FileInfo file){
        fileInfoDynamoDbTable.putItem(file);
    }


}
