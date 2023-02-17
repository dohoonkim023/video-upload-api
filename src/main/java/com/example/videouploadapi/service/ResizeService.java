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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ResizeService {

    private final UploadRepository uploadRepository;

    @Value("${video.uploadPath}")
    private String UPLOAD_PATH;
    @Value("${video.ffmpegPath}")
    private String ffmpegPath;
    @Value("${video.ffprobePath}")
    private String ffprobePath;
    @Value("${video.staticResourcePath}")
    private String staticResourcePath;

    private static final int resizedWidth = 360;

    @Async
    public CompletableFuture<String> createResizedVideo(Long uploadId, String originalVideoPath, String resizedFileName) throws IOException {
        FFprobe ffprobec = new FFprobe(ffprobePath);
        FFmpegProbeResult probeResult = ffprobec.probe(originalVideoPath);

        int width = probeResult.getStreams().get(0).width;
        int height = probeResult.getStreams().get(0).height;
        int resizedHeight = (height * resizedWidth) / width;

        String resizedVideoPath = UPLOAD_PATH + "/" + resizedFileName;

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(originalVideoPath)
                .addOutput(resizedVideoPath)
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
        upload.setResizedVideoUrl(staticResourcePath + resizedFileName);

        uploadRepository.save(upload);
        String result = "true";
        return CompletableFuture.completedFuture(result);

    }
}
