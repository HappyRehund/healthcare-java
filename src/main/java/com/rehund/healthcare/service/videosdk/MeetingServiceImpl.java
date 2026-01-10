package com.rehund.healthcare.service.videosdk;

import com.rehund.healthcare.common.constant.AppointmentStatus;
import com.rehund.healthcare.config.videosdkconfig.VideoSDKClient;
import com.rehund.healthcare.entity.appointment.Appointment;
import com.rehund.healthcare.model.videosdk.VideoSDKCreateRoomRequest;
import com.rehund.healthcare.model.videosdk.VideoSDKCreateRoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MeetingServiceImpl implements MeetingService {

    private final VideoSDKClient videoSDKClient;

    @Override
    public void createMeetingRoom(Appointment appointment) {

        if (!appointment.getStatus().equals(AppointmentStatus.SCHEDULED)) {
            log.info("Appointment {} is not in SCHEDULED status. Skipping meeting room creation.", appointment.getAppointmentId());
            return;
        }

        if (appointment.getMeetingId() != null && !appointment.getMeetingId().isEmpty()) {
            log.info("Appointment {} already has a meeting ID. Skipping meeting room creation.", appointment.getAppointmentId());
        }

        VideoSDKCreateRoomRequest videoSDKCreateRoomRequest = VideoSDKCreateRoomRequest.builder().build();

        VideoSDKCreateRoomResponse response = videoSDKClient.createRoom(videoSDKCreateRoomRequest);

        appointment.setMeetingId(response.getRoomId());


    }
}
