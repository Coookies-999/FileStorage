package com.FileStorage.Controller;


import com.FileStorage.Dto.fileInfoResponseDto;
import com.FileStorage.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> insertTos3(@RequestBody MultipartFile file) throws IOException {
        return fileService.insertTos3(file);
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String fileName){
        System.out.println("Filename "+ fileName);
        return ResponseEntity.ok(fileService.downloadFile(fileName));
    }

    @GetMapping("/getFiles")
    public ResponseEntity<?> getAllFilesUploadedByUser(){
        return fileService.getAllFilesUploadedByLoggedInUser();
    }





















    @PostMapping("/test")
    public String TestingDynamoDb(){
        return fileService.testingfileInfoTable();
    }
}
