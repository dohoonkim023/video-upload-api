package com.example.videouploadapi.api;

import com.example.videouploadapi.dto.VideoInfolDto;
import com.example.videouploadapi.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RequestMapping("/video")
@RequiredArgsConstructor
@Slf4j
@RestController
public class VideoController {

    private final VideoService videoService;

    private static final String CONTENT_TYPE = "video/mp4";

    @PostMapping
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile multipartFile,
                                         @RequestParam("title") String title) throws IOException {
        if (!Objects.equals(multipartFile.getContentType(), CONTENT_TYPE)) {
            return new ResponseEntity<>("Invalid content-type", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        videoService.uploadVideo(multipartFile, title);
        System.out.println("--------3--------");
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoDetail(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoInfo(id));
    }
}
