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

        return ResponseEntity.ok(dto);
    }

    public byte[] downloadFile(String fileName){
        S3Object object=s3Client.getObject(bucketName,fileName);
        S3ObjectInputStream objectInputStream=object.getObjectContent();

        try{
            byte[] content= IOUtils.toByteArray(objectInputStream);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
