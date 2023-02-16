package com.example.videouploadapi.service;

import com.example.videouploadapi.persist.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class ThumbnailService {

    private static final String UPLOAD_PATH = "src/main/resources/static/video";
    private static final String ffmpegPath = "/opt/homebrew/bin/ffmpeg";  // -> 설정파일로 빼기
    private static final String ffprobePath = "/opt/homebrew/bin/ffprobe"; // -> 설정파일로 빼기


    @Async
    public CompletableFuture<String> createThumbnail(String originVideoPath, String resizedFileName) throws IOException {
        String thumbnailPath = UPLOAD_PATH + "/" + resizedFileName.replace("(resized).mp4", "") + "(thumbnail).gif";

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(originVideoPath)
                .addExtraArgs("-ss", "00:00:01")
                .addOutput(thumbnailPath)
                .setFrames(1)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(new FFmpeg(ffmpegPath), new FFprobe(ffprobePath));
        FFmpegJob job = executor.createJob(builder);
        job.run();

        String result = "true";
        System.out.println("-----Thumbnail-----");
        return CompletableFuture.completedFuture(result);

    }
}
