package com.example.videouploadapi.service;

import com.example.videouploadapi.dto.VideoInfolDto;
import com.example.videouploadapi.persist.entity.Upload;
import com.example.videouploadapi.persist.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoService {
    private final ResizeService resizeService;
    private final ThumbnailService thumbnailService;

    private final UploadRepository uploadRepository;

    @Value("${video.uploadPath}")
    private String UPLOAD_PATH;
    @Value("${video.ffprobePath}")
    private String ffprobePath;
    @Value("${video.staticResourcePath}")
    private String staticResourcePath;

    public void uploadVideo(MultipartFile multipartFile, String title) throws IOException {
        log.info("### upload");
        String fileName = UUID.randomUUID() +"_" + multipartFile.getOriginalFilename();
        uploadVideoToServer(multipartFile, fileName);

        String originalVideoPath = UPLOAD_PATH + "/" + fileName;
        FFprobe ffprobec = new FFprobe(ffprobePath);
        FFmpegProbeResult probeResult = ffprobec.probe(originalVideoPath);

        Upload upload = Upload.builder()
                .title(title)
                .originalFilesize(probeResult.getFormat().size)
                .originalWidth((long) probeResult.getStreams().get(0).width)
                .originalHeight((long) probeResult.getStreams().get(0).height)
                .originalVideoUrl(staticResourcePath + fileName)
                .createdAt(LocalDateTime.now())
                .build();

        Long uploadId = uploadRepository.save(upload).getId();
        String resizedFileName = fileName.replace(".mp4", "") + "(resized).mp4";

        final CompletableFuture<String> resizeResult =  resizeService.createResizedVideo(uploadId, originalVideoPath, resizedFileName);
//      resizeResult.thenAccept(
//                result -> {
//                    if (result.equals("true")) {
//
//                    }
//                }
//        );
        final CompletableFuture<String> thumnailResult =  thumbnailService.createThumbnail(originalVideoPath, resizedFileName);
    }

    private void uploadVideoToServer(MultipartFile multipartFile, String fileName) {
        File targetFile = new File(UPLOAD_PATH, fileName);
        try {
            InputStream fileStream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(fileStream, targetFile);
        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);
            e.printStackTrace();
        }
    }

    public VideoInfolDto getVideoInfo(Long id) {
        Upload upload = uploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Found"));

        return new VideoInfolDto(upload);
    }
}
