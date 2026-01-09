import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API_CONFIG from "../config/api.config";
import { type AppointmentResponse, type DoctorResponse, type ErrorResponse } from "../types/api.types";
import { formatTime } from "../utils/date-time.utils";
import Navbar from "./navbar";
import AppointmentScheduleModal from "./appointment-schedule-modal";
import AppointmentCard from "./appointment-card";

const AppointmentList = () => {

  const navigate = useNavigate();
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cancellingId, setCancellingId] = useState<number | null>(null);
  const [showRescheduleModal, setShowRescheduleModal] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState<AppointmentResponse | null>(null);
  const [selectedDoctor, setSelectedDoctor] = useState<DoctorResponse | null>(null);
  const [loadingDoctor, setLoadingDoctor] = useState(false);


  useEffect(() => {

    const fetchAppointments = async () => {
      try {
        const token = localStorage.getItem('token')
        const response = await fetch(
          `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.APPOINTMENTS}`,
          {
            method: "GET",
            headers: {
              'accept': "*/*",
              'Authorization': `Bearer ${token}`
            }
          }
        );

        if (!response.ok){
          const errorData: ErrorResponse = await response.json();
          throw new Error(errorData.message);
        }

        const appointmentsData: AppointmentResponse[] = await response.json();
        setAppointments(appointmentsData);

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

    fetchAppointments();

  }, [])

  const handleRescheduleClick = async (appointment: AppointmentResponse) => {
    setSelectedAppointment(appointment);
    setLoadingDoctor(true);

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DOCTORS}/${appointment.doctor_id}`,
        {
          method: "GET",
          headers: {
            'accept': "*/*",
            'Authorization': `Bearer ${token}`
          }
        }
      );

      if (!response.ok) {
        const errorData: ErrorResponse = await response.json();
        throw new Error(errorData.message);
      }

      const doctorData: DoctorResponse = await response.json();
      setSelectedDoctor(doctorData);
      setShowRescheduleModal(true);
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError("Failed to load doctor data");
      }
    } finally {
      setLoadingDoctor(false);
    }
  }

  const handleCancel = async (appointmentId: number) => {
    if(!window.confirm('Are you sure you want to cancel this appointment?')) {
      return;
    }

    setCancellingId(appointmentId);

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.APPOINTMENTS}/${appointmentId}/cancel`,
        {
          method: "PUT",
          headers: {
            accept: "*/*",
            Authorization: `Bearer ${token}`
          }
        }
      )

      if (!response.ok){
        const errorData: ErrorResponse = await response.json();
        throw new Error(errorData.message);
      }

      navigate(
        `/appointments/${appointmentId}`,
        {
          state: {
            message: "Appointment cancelled successfuly",
            type: "success"
          }
        }
      )
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError("failed to login")
      }
    } finally {
      setCancellingId(null);
    }

  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">
            Loading Appointments...
          </p>
        </div>
      </div>
    )
  }

  if(error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-500">{error}</p>
          <button
            onClick={() => navigate('/home')}
            className="mt-4 text-indigo-600 hover:text-indigo-500"
          >
            Return to Home
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

          {/* Header */}
          <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">My Appointments</h1>
          <p className="mt-2 text-sm text-gray-700">View and Manage Your Appointments</p>
        </div>

        {/* Appointment List */}
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          {appointments.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-gray-500">No Appointments Yet</p>
            </div>
          ) : (
            <ul className="divide-y divide-gray-200">
              {appointments.map((appointment) => (
                <AppointmentCard
                  key={appointment.appointment_id}
                  appointment={appointment}
                  onReschedule={handleRescheduleClick}
                  onCancel={handleCancel}
                  isCancelling={cancellingId === appointment.appointment_id}
                />
              ))}
            </ul>
          )}
        </div>

        {selectedAppointment && selectedDoctor && (
          <AppointmentScheduleModal
            doctor={selectedDoctor}
            isOpen={showRescheduleModal}
            onClose={() => {
              setShowRescheduleModal(false)
              setSelectedAppointment(null)
              setSelectedDoctor(null)
            }}
            mode="reschedule"
            appointmentId={selectedAppointment.appointment_id}
            initialDate={selectedAppointment.appointment_date}
            initialTime={formatTime(selectedAppointment.start_time)}
            initialSpecializationId={selectedAppointment.doctor_specialization_id}
            initialConsultationType={selectedAppointment.consultation_type}
          />
        )}

        {/* Back Button */}
        <div className="mt-6 text-center">
          <button
            onClick={() => navigate("/home")}
            className="text-indigo-600 hover:text-indigo-500"
          >
            Back to Home
          </button>
        </div>
        </div>
      </div>
    </div>
  )
}

export default AppointmentList;