import { useMeeting } from '@videosdk.live/react-sdk';
import React, { useState } from 'react'

interface MeetingViewProps {
  onMeetingLeave: () => void;
}

const Controls = ({onLeave}: {onLeave: () => void}) => {
  const meeting = useMeeting();
  const { localMicOn, localWebcamOn } = meeting;

  return(
    <div className="fixed bottom-0 left-0 right-0 h-16 bg-gray-800 flex items-center justify-center px-4 space-x-4">
      <button
        onClick={() => {
          meeting.toggleMic();
        }}
        className={`px-4 py-2 ${
          localMicOn
            ? "bg-blue-600 hover:bg-blue-700"
            : "bg-red-600 hover:bg-red-700"
        } text-white rounded-md flex items-center gap-2`}
      >
        {localMicOn ? "Mic On" : "Mic Off"}
      </button>

      <button
        onClick={() => {
          meeting.toggleWebcam()
        }}
        className={`px-4 py-2 ${
          localWebcamOn
            ? "bg-blue-600 hover:bg-blue-700"
            : "bg-red-600 hover:bg-red-700"
        } text-white rounded-md flex items-center gap-2`}
      >
        {localWebcamOn ? "WebCam On" : "WebCam Off"}
      </button>

      <button
        onClick={() => {
          meeting.leave()
          onLeave()
        }}
        className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-md"
      >
        End Consultation
      </button>

    </div>
  )
}

const MeetingView = ({ onMeetingLeave }: MeetingViewProps) => {
  const [joined, setJoined] = useState(false);

  const { join } = useMeeting({
    onMeetingJoined: () => setJoined(true),
    onMeetingLeft: () => onMeetingLeave()
  })

  const { participants } = useMeeting();

  if(!joined) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900">
        <div className="bg-white p-8 rounded-lg shadow-xl max-w-md w-full">
          <h2 className="text-2xl font-bold mb-6 text-center">
            Join Consultation
          </h2>
          <button
            onClick={() => {
              join()
            }}
            className="w-full py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Join Meeting
          </button>
        </div>
      </div>
    )
  }

  return (
    <>
      <div className="min-h-screen flex items-center pb-16">
        <div className="w-full p-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {Array.from(participants.keys()).map((participantId) => (
              <ParticipantView
                participantId={participantId}
                key={participantId}
              />
            ))}
          </div>
        </div>
      </div>

      <Controls onLeave={onMeetingLeave} />
    </>
  )
}

export default MeetingView;