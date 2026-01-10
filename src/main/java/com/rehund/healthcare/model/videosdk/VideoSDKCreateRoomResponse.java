package com.rehund.healthcare.model.videosdk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.Instant;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoSDKCreateRoomResponse {

    private String roomId;
    private String customRoomId;
    private String userId;
    private boolean disabled;
    private Instant createdAt;
    private Instant updatedAt;
    private String id;
    private Links links;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Links {
        private String getRoom;
        private String getSession;
    }
}
