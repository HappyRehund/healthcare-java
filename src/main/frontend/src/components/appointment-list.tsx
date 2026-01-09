import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API_CONFIG from "../config/api.config";
import { type AppointmentResponse, type ErrorResponse } from "../types/api.types";
import { formatDate, formatTime } from "../utils/date-time.utils";
import { getAppointmentStatusBadgeClass, getPaymentStatusBadgeClass } from "../utils/status-badge.utils";
import { formatToIDR } from "../utils/currency.utils";

const AppointmentList = () => {

  const navigate = useNavigate();
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

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
    <div className="min-h-screen bg-gray-50 py-8">
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
                <li key={appointment.appointment_id} className="p-6 hover:bg-gray-50">
                  <div className="space-y-4">
                    {/* Date & Time */}
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-lg font-medium text-gray-900">
                          {formatDate(appointment.appointment_date)}
                        </p>
                        <p className="text-sm text-gray-500">
                          {formatTime(appointment.start_time)} - {formatTime(appointment.end_time)}
                        </p>
                      </div>
                      <button
                        onClick={() => navigate(`/appointments/${appointment.appointment_id}`)}
                        className="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
                      >
                        View Details
                      </button>
                    </div>

                    {/* Status Badges */}
                    <div className="flex flex-wrap gap-2">
                      {/* Consultation Type */}
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        {appointment.consultation_type}
                      </span>

                      {/* Appointment Status */}
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getAppointmentStatusBadgeClass(appointment.status)}`}>
                        {appointment.status}
                      </span>

                      {/* Payment Status */}
                      {appointment.payment_detail && (
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getPaymentStatusBadgeClass(appointment.payment_detail.payment_status)}`}>
                          Payment: {appointment.payment_detail.payment_status}
                        </span>
                      )}
                    </div>

                    {/* Payment Amount */}
                    {appointment.payment_detail && (
                      <div className="text-sm text-gray-500">
                        Amount: {formatToIDR(appointment.payment_detail.amount)}
                      </div>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Back Button */}
        <div className="mt-6 text-center">
          <button
            onClick={() => navigate("/appointments")}
            className="text-indigo-600 hover:text-indigo-500"
          >
            Back to my Appointment
          </button>
        </div>
      </div>
    </div>
  )
}

export default AppointmentList;