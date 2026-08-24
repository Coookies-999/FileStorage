package com.FileStorage.Model;


import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.LocalDateTime;

    @DynamoDbBean
    @Setter
    @Builder
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public class FileInfo {


        private String fileId;

        private String userId;

        private String fileName;

        private String s3Key;

        private LocalDateTime uploadTime;

        private String fileSize;

        private String fileType;

        @DynamoDbPartitionKey
        @DynamoDbAttribute(value = "fileid")
        public String getFileId() {
            return fileId;
        }

        @DynamoDbAttribute(value = "userid")
        @DynamoDbSecondaryPartitionKey(indexNames = "userid-index")   //to get files uploaded by user
        public String getUserId() {
            return userId;
        }

        @DynamoDbAttribute(value = "filename")
        public String getFileName() {
            return fileName;
        }

        @DynamoDbAttribute(value = "s3key")
        public String getS3Key() {
            return s3Key;
        }

        @DynamoDbAttribute(value = "filesize")
        public String getFileSize() {
            return fileSize;
        }

        @DynamoDbAttribute(value = "uploadtime")
        public LocalDateTime getUploadTime() {
            return uploadTime;
        }

        @DynamoDbAttribute(value = "filetype")
        public String getFileType() {
            return fileType;
        }



    }
