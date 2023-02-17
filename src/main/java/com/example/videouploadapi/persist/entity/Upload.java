package com.example.videouploadapi.persist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Upload {

    @Id @GeneratedValue
    @Column(name = "uploadId")
    private Long id;

    private String title;

    private Long originalFilesize;

    private Long originalWidth;

    private Long originalHeight;

    private String originalVideoUrl;

    private Long resizedFilesize;

    private Long resizedWidth;

    private Long resizedHeight;

    private String resizedVideoUrl;

    private LocalDateTime createdAt;

}
