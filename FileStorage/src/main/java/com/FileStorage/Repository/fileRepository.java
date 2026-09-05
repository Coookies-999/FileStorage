package com.FileStorage.Repository;

import com.FileStorage.Model.FileInfo;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class fileRepository {

    private DynamoDbTable<FileInfo> fileInfoDynamoDbTable;

    public fileRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.fileInfoDynamoDbTable = dynamoDbEnhancedClient.table("FileInfo", TableSchema.fromBean(FileInfo.class));
    }

    public void saveFile(FileInfo file){
        fileInfoDynamoDbTable.putItem(file);
    }

    public Optional<FileInfo> getFileByFileId(String fileId){
        Key key=Key.builder()
                .partitionValue(fileId)
                .build();
        FileInfo file=fileInfoDynamoDbTable.getItem(key);
        return Optional.ofNullable(file);
    }

    public List<FileInfo> getAllFilesUploadedByUser(String userId){
        DynamoDbIndex<FileInfo> userindex=fileInfoDynamoDbTable.index("userid-index");

        QueryConditional queryConditional=QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue(userId)
                        .build()
        );

        return StreamSupport.stream(
                userindex.query(queryConditional).spliterator(),
                false
        ).flatMap(page->page.items().stream())
                .toList();
    }


    public void deleteFile(String fileId){
        Key key=Key.builder()
                .partitionValue(fileId)
                .build();
        fileInfoDynamoDbTable.deleteItem(
                r ->r.key(key)
        );
    }

}
