package com.example.videouploadapi.persist.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
