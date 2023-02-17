package com.example.videouploadapi.dto;

import com.example.videouploadapi.persist.entity.Upload;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoInfolDto {

    private Long id;
    private String title;
    private Original original;
    private Resized resized;
    private LocalDateTime createdAt;

    public VideoInfolDto(Upload upload) {
        this.id = upload.getId();
        this.title = upload.getTitle();
        this.original = new Original(upload.getOriginalFilesize(),
                                        upload.getOriginalWidth(),
                                        upload.getOriginalHeight(),
                                        upload.getOriginalVideoUrl());
        this.resized = new Resized(upload.getResizedFilesize(),
                                    upload.getResizedWidth(),
                                    upload.getOriginalHeight(),
                                    upload.getResizedVideoUrl());
        this.createdAt = upload.getCreatedAt();
    }
}
