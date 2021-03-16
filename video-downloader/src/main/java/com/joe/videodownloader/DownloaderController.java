package com.joe.videodownloader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioVideoFormat;
import com.github.kiulian.downloader.model.formats.Format;
import com.github.kiulian.downloader.model.quality.VideoQuality;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DownloaderController {

    @PostMapping("/download")
    ResponseEntity<?> downloader(@RequestParam String url) throws IOException, YoutubeException {
        YoutubeDownloader downloader = new YoutubeDownloader();
        downloader.setParserRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);

        String videoId = extractVideoId(url);
        YoutubeVideo video = downloader.getVideo(videoId);
        VideoDetails details = video.details();
        System.out.println(details.viewCount());
        // TODO: This is where the videos will be stored
        File outputDir = new File("my_videos");
        List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
        Format format = videoWithAudioFormats.get(0);
        for (AudioVideoFormat audioVideoFormat : videoWithAudioFormats) {
            // TODO: work out a method to call the highest quality available
            if (audioVideoFormat.videoQuality() == VideoQuality.hd720) {
                format = audioVideoFormat;
            }
        }

        int index = 2;
        File file = video.download(format, outputDir, ("video" + index), true);

        return ResponseEntity.ok(file.getPath());
    }

    private String extractVideoId(String url) {
        int index = url.indexOf("v=");
        index += 2;
        System.out.println(url.charAt(index));
        String id = "";
        for (int i = index; i < url.length(); i++) {
            id += url.charAt(i);
        }
        return id;
    }

}
