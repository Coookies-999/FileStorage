package com.FileStorage.Dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class fileInfoResponseDto {

    private String fileName;
    private String s3Key;
    private String fileSize;
    private String uploadedBy;

}
