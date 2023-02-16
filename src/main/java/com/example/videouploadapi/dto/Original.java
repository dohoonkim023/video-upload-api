package com.example.videouploadapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Original {
    private Long filesize;
    private Long width;
    private Long height;
    private String videoUrl;
}
