package com.FileStorage.Dto;


import lombok.*;

import java.time.LocalDateTime;

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
    private String fileId;
    private LocalDateTime uploadedDate;

}
