package com.example.videouploadapi.service;

import com.example.videouploadapi.dto.VideoInfolDto;
import com.example.videouploadapi.persist.entity.Upload;
import com.example.videouploadapi.persist.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoService {
    private final ResizeService resizeService;
    private final ThumbnailService thumbnailService;

    private final UploadRepository uploadRepository;

    private static final String UPLOAD_PATH = "src/main/resources/static/video";
    private static final String ffmpegPath = "/opt/homebrew/bin/ffmpeg";  // -> 설정파일로 빼기
    private static final String ffprobePath = "/opt/homebrew/bin/ffprobe"; // -> 설정파일로 빼기

    private static final int resizedWidth = 360;

    public void uploadVideo(MultipartFile multipartFile, String title) throws IOException {
        log.info("### upload");
        String fileName = UUID.randomUUID() +"_" + multipartFile.getOriginalFilename();
        uploadVideoToServer(multipartFile, fileName);

        //upload 부분 저장 -> 빌더 사용하기
        String originalVideoPath = UPLOAD_PATH + "/" + fileName;
        FFprobe ffprobec = new FFprobe(ffprobePath);
        FFmpegProbeResult probeResult = ffprobec.probe(originalVideoPath);

        Upload upload = Upload.builder()
                .title(title)
                .originalFilesize(probeResult.getFormat().size)
                .originalWidth((long) probeResult.getStreams().get(0).width)
                .originalHeight((long) probeResult.getStreams().get(0).height)
                .originalVideoUrl("http://localhost:8080/video/" + fileName)
                .createdAt(LocalDateTime.now())
                .build();

        Long uploadId = uploadRepository.save(upload).getId();
        String resizedFileName = fileName.replace(".mp4", "") + "(resized).mp4";

        System.out.println("--------1--------");
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


    private void tmp() {
//        FFprobe ffprobec = new FFprobe(ffprobePath);
//        FFmpegProbeResult probeResult = ffprobec.probe(originalVideoPath);
//        log.info("========== VideoFileUtils.getMediaInfo() ==========");
//        log.info("filename : {}", probeResult.getFormat().filename);
//        log.info("format_name : {}", probeResult.getFormat().format_name);
//        log.info("format_long_name : {}", probeResult.getFormat().format_long_name);
//        log.info("tags : {}", probeResult.getFormat().tags.toString());
//        log.info("duration : {} second", probeResult.getFormat().duration);
//        log.info("size : {} byte", probeResult.getFormat().size);
//
//        log.info("width : {} px", probeResult.getStreams().get(0).width);
//        log.info("height : {} px", probeResult.getStreams().get(0).height);
//        log.info("===================================================");
    }

    public VideoInfolDto getVideoInfo(Long id) {
        Upload upload = uploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Found"));

        return new VideoInfolDto(upload);


    }

    // 썸네일
//    private void createResizedVideo(String orig inVideoPath, String resizedVideoPath) throws IOException {
//        String inputPath = "originVideos/test.mp4";
//        String outputPath = "resizedVideos/";
//
//        String ffmpegPath = "/opt/homebrew/bin/ffmpeg";
//        String ffprobePath = "/opt/homebrew/bin/ffprobe";
//
//
//        FFmpegBuilder builder = new FFmpegBuilder()
//                .overrideOutputFiles(true)
//                .setInput(inputPath)
//                .addExtraArgs("-ss", "00:00:01") 			// 영상에서 추출하고자 하는 시간 - 00:00:01은 1초를 의미
//                .addOutput(outputPath + "test.gif") 		// 저장 절대 경로(확장자 미 지정 시 예외 발생 - [NULL @ 000002cc1f9fa500] Unable to find a suitable output format for 'C:/Users/Desktop/test')
//                .setFrames(1)
//                .done();
//
//        FFmpegExecutor executor = new FFmpegExecutor(new FFmpeg(ffmpegPath), new FFprobe(ffprobePath));
//        FFmpegJob job = executor.createJob(builder);
//        job.run();
//
//    }

}
