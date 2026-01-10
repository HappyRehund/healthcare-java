import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom"
import API_CONFIG from "../config/api.config";
import type { AppointmentMeetingResponse, ErrorResponse } from "../types/api.types";
import { MeetingProvider } from "@videosdk.live/react-sdk";
import MeetingView from "./meeting-view";

const MeetingPage = () => {
  const { appointmentId } = useParams();
  const navigate = useNavigate();

  const [meetingDetails, setMeetingDetails] = useState<AppointmentMeetingResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const initializeMeeting = async () => {
      try {
        const authToken = localStorage.getItem("token");
        const response = await fetch(
          `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.APPOINTMENTS}/${appointmentId}/meeting`,
          {
            headers: {
              accept: "*/*",
              Authorization: `Bearer ${authToken}`
            }
          }
        )

      if (!response.ok){
        const errorData: ErrorResponse = await response.json();
        throw new Error(errorData.message);
      }

      const data: AppointmentMeetingResponse = await response.json();

      setMeetingDetails({
        meeting_id: data.meeting_id,
        patient_id: data.patient_id,
        doctor_id: data.doctor_id,
        appointment_status: data.appointment_status
      })

      } catch (error) {
        if (error instanceof Error) {
          setError(error.message);
        } else {
          setError("failed to login")
        }
      } finally {
        setLoading(false)
      }
    }

    initializeMeeting();
  }, [appointmentId]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">
            Loading Appointment details
          </p>
        </div>
      </div>
    )
  }

  if(error || !meetingDetails?.meeting_id || !API_CONFIG.VIDEOSDK_TOKEN) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-500">{error || "Something Error Happened"}</p>
          <button
            onClick={() => navigate('/appointments')}
            className="mt-4 text-indigo-600 hover:text-indigo-500"
          >
            Return to Appointment
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="h-screen bg-gray-900">
      <MeetingProvider
        config={{
          meetingId: meetingDetails.meeting_id,
          micEnabled: true,
          webcamEnabled:true,
          mode: "SEND_AND_RECV",
          multiStream: true,
          name: `Consultation Meeting for ${meetingDetails.patient_id} with ${meetingDetails.meeting_id}`,
          debugMode: false
        }}
        token={API_CONFIG.VIDEOSDK_TOKEN}
      >
        <MeetingView onMeetingLeave={() => navigate("/appointments")} />
      </MeetingProvider>
    </div>
  )
}

export default MeetingPage;