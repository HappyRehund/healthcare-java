package com.rehund.healthcare.model.videosdk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoSDKCreateRoomRequest {

    private String customRoomId;
    private String webhook;
    private String autoCloseConfig;
    private String autoStartConfig;
}
