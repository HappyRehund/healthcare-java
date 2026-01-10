package com.rehund.healthcare.config.videosdkconfig;

import com.rehund.healthcare.model.videosdk.VideoSDKCreateRoomRequest;
import com.rehund.healthcare.model.videosdk.VideoSDKCreateRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Configuration
public class VideoSDKClient {
    private final VideoSDKProperties videoSDKProperties;
    private final RestTemplate restTemplate;

    public VideoSDKCreateRoomResponse createRoom(VideoSDKCreateRoomRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", videoSDKProperties.getApiKey());

        HttpEntity<VideoSDKCreateRoomRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(
                videoSDKProperties.getBaseUrl(),
                entity,
                VideoSDKCreateRoomResponse.class
        );
    }
}
