import { useParticipant } from "@videosdk.live/react-sdk";
import { useEffect, useRef } from "react";

interface ParticipantViewProps {
  participantId: string
}

const ParticipantView = ({ participantId } : ParticipantViewProps) => {
  const micRef = useRef<HTMLAudioElement>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const { webcamStream, micStream, webcamOn, micOn, isLocal, displayName } = useParticipant(participantId)

  useEffect(() => {
    if (micRef.current) {
      if (micOn && micStream){
        const mediaStream = new MediaStream();
        mediaStream.addTrack(micStream.track);

        micRef.current.srcObject = mediaStream;
        micRef.current
          .play()
          .catch((error: Error) => console.error("Audio playback failed", error))
      } else {
        micRef.current.srcObject = null;
      }
    }
  }, [micStream, micOn])

  useEffect(() => {
    if (videoRef.current) {
      if (webcamOn && webcamStream) {
        const mediaStream = new MediaStream();
        mediaStream.addTrack(webcamStream.track);

        videoRef.current.srcObject = mediaStream;
        videoRef.current
          .play()
          .catch((error: Error) => console.error("Video playback failed", error))
      } else {
        videoRef.current.srcObject = null;
      }
    }
  }, [webcamStream, webcamOn])

  return (
    <div className="relative">
      <audio ref={micRef} autoPlay playsInline muted={isLocal} />
      <div className="relative w-full aspect-video bg-gray-800 rounded-lg overflow-hidden">
        {webcamOn ? (
          <video
            ref={videoRef}
            autoPlay
            playsInline
            muted
            className="w-full h-full object-cover"
          />
        ): (
          <div className="flex items-center justify-center h-full">
            <div className="w-20 h-20 rounded-full bg-gray-600 flex items-center justify-center">
              <span className="text-2xl text-white font-semibold">
                {displayName?.charAt(0)?.toUpperCase() || "?"}
              </span>
            </div>
          </div>
        )}
      </div>
      {/* Participant name overlay */}
      <div className="absolute bottom-2 left-2 bg-black bg-opacity-50 px-2 py-1 rounded text-white text-sm">
        {displayName || "Participant"} {isLocal && "(You)"}
      </div>
      {/* Mic status indicator */}
      <div className={`absolute top-2 right-2 w-6 h-6 rounded-full flex items-center justify-center ${micOn ? 'bg-green-500' : 'bg-red-500'}`}>
        <span className="text-white text-xs">{micOn ? '🎤' : '🔇'}</span>
      </div>
    </div>
  )
}

export default ParticipantView;