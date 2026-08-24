package com.FileStorage.Service;



import com.FileStorage.Dto.fileInfoResponseDto;
import com.FileStorage.Model.FileInfo;
import com.FileStorage.Repository.fileRepository;
import com.FileStorage.Repository.userRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class FileService {

    @Autowired
    private fileRepository fileRepo;

    @Value("${spring.cloud.aws.bucketname}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    private userRepository repo;

    public ResponseEntity<fileInfoResponseDto> insertTos3(MultipartFile multipartFile) throws IOException {

        File file=convertMultiPartFileToFile(multipartFile);

        //getting userid from spring security context
        String userId=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //random generated s3Key for uniqueness
        String s3Key=UUID.randomUUID().toString()+"_"+multipartFile.getOriginalFilename();

        //add file in s3 bucket
        s3Client.putObject(new PutObjectRequest(bucketName,s3Key,file));

        fileInfoResponseDto dto=fileInfoResponseDto.builder()
                .fileSize(multipartFile.getSize()/1024+"kb")
                .uploadedBy(userId)
                .s3Key(s3Key)
                .fileName(multipartFile.getOriginalFilename())
                .build();

        //File info to save in dynamoDB
        FileInfo info=FileInfo.builder()
                .fileName(multipartFile.getOriginalFilename())
                .fileId(UUID.randomUUID().toString())
                .fileSize(multipartFile.getSize()/1024+"kb")
                .uploadTime(LocalDateTime.now())
                .s3Key(s3Key)
                .fileType(multipartFile.getContentType())
                .userId(userId)
                .build();

        //save in dynamodb
        fileRepo.saveFile(info);

        file.delete();

        return ResponseEntity.ok(dto);
    }

    public byte[] downloadFile(String fileId){
        //get s3 key for that passed fileId
        Optional<FileInfo> info=fileRepo.getFileByFileId(fileId);

        if(info.isEmpty())
            throw new IllegalArgumentException("Invalid file");

        FileInfo file=info.get();

        S3Object object=s3Client.getObject(bucketName,file.getS3Key());
        S3ObjectInputStream objectInputStream=object.getObjectContent();

        try{
            byte[] content= IOUtils.toByteArray(objectInputStream);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getAllFilesUploadedByLoggedInUser(){

        //loggedIn user
        String loggedInUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //All files uploaded by user
        List<FileInfo> ls=fileRepo.getAllFilesUploadedByUser(loggedInUser);

        if(ls.isEmpty())
            return ResponseEntity.ok("User have not uploaded any files");

        List<fileInfoResponseDto> list=new ArrayList<>();

        for(FileInfo file:ls){
            fileInfoResponseDto dto=fileInfoResponseDto.builder()
                    .fileSize(file.getFileSize())
                    .uploadedBy(file.getUserId())
                    .fileName(file.getFileName())
                    .s3Key(file.getS3Key())
                    .build();
            list.add(dto);
        }

        return ResponseEntity.ok(list);
    }


    public ResponseEntity<String> deleteFile(String fileId){
        //get fileid from request and serach in dynamodb and get the s3key and pass that to delete
        Optional<FileInfo> info=fileRepo.getFileByFileId(fileId);

        if(info.isEmpty())
            throw new IllegalArgumentException("Invalid file");

        FileInfo file=info.get();

        //delete file
        s3Client.deleteObject(bucketName,file.getS3Key());
        return ResponseEntity.ok("File deleted");
    }



    private File convertMultiPartFileToFile(MultipartFile file){
        File convertedFile=new File(file.getOriginalFilename());
        try(FileOutputStream fos=new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
            return convertedFile;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String testingfileInfoTable(){

        String userId= (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FileInfo info=FileInfo.builder()
                .s3Key("keyyy")
                .fileId(UUID.randomUUID().toString())
                .fileName("Tester")
                .fileSize("23kb")
                .userId(userId)
                .uploadTime(LocalDateTime.now())
                .fileType(".Test")
                .build();
        fileRepo.saveFile(info);
        return "SuckSex";
    }


}
