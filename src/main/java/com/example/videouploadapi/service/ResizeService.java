package com.example.videouploadapi.service;

import com.example.videouploadapi.persist.entity.Upload;
import com.example.videouploadapi.persist.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ResizeService {

    private final UploadRepository uploadRepository;

    private static final String UPLOAD_PATH = "src/main/resources/static/video";
    private static final String ffmpegPath = "/opt/homebrew/bin/ffmpeg";  // -> 설정파일로 빼기
    private static final String ffprobePath = "/opt/homebrew/bin/ffprobe"; // -> 설정파일로 빼기

    private static final int resizedWidth = 360;

    @Async
    public CompletableFuture<String> createResizedVideo(Long uploadId, String originalVideoPath, String resizedFileName) throws IOException {
        //동영상 메타정보 추출??
        FFprobe ffprobec = new FFprobe(ffprobePath);
        FFmpegProbeResult probeResult = ffprobec.probe(originalVideoPath);

        //resizedHeight 구하기
        int width = probeResult.getStreams().get(0).width;
        int height = probeResult.getStreams().get(0).height;
        int resizedHeight = (height * resizedWidth) / width;

        String resizedVideoPath = UPLOAD_PATH + "/" + resizedFileName;

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(originalVideoPath)// 영상에서 추출하고자 하는 시간 - 00:00:01은 1초를 의미
                .addOutput(resizedVideoPath)        // 저장 절대 경로(확장자 미 지정 시 예외 발생 - [NULL @ 000002cc1f9fa500] Unable to find a suitable output format for 'C:/Users/Desktop/test')
                .setVideoWidth(resizedWidth)
                .setVideoHeight(resizedHeight)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(new FFmpeg(ffmpegPath), new FFprobe(ffprobePath));
        FFmpegJob job = executor.createJob(builder);
        job.run();

        probeResult = ffprobec.probe(resizedVideoPath);
        Optional<Upload> optionalUpload = uploadRepository.findById(uploadId);
        Upload upload = optionalUpload.get();
        upload.setResizedFilesize(probeResult.getFormat().size);
        upload.setResizedWidth((long) probeResult.getStreams().get(0).width);
        upload.setResizedHeight((long) probeResult.getStreams().get(0).height);
        upload.setResizedVideoUrl("http://localhost:8080/video/" + resizedFileName);

        uploadRepository.save(upload);
        String result = "true";
        System.out.println("--------2--------");
        return CompletableFuture.completedFuture(result);

    }
}
