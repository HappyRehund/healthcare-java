import { useNavigate } from "react-router-dom";
import type { AppointmentResponse } from "../types/api.types";
import { formatDate, formatTime } from "../utils/date-time.utils";
import { getAppointmentStatusBadgeClass, getPaymentStatusBadgeClass } from "../utils/status-badge.utils";
import { formatToIDR } from "../utils/currency.utils";

interface AppointmentCardProps {
  appointment: AppointmentResponse;
  onReschedule: (appointment: AppointmentResponse) => void;
  onCancel: (appointmentId: number) => void;
  isCancelling: boolean;
}

const AppointmentCard = ({
  appointment,
  onReschedule,
  onCancel,
  isCancelling
}: AppointmentCardProps) => {
  const navigate = useNavigate();

  return (
    <li className="p-6 hover:bg-gray-50">
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

          {appointment.status === 'PENDING' && (
            <>
              <button
                onClick={() => onReschedule(appointment)}
                className="text-yellow-600 hover:text-yellow-900 font-medium text-sm"
              >
                Reschedule
              </button>

              <button
                onClick={() => onCancel(appointment.appointment_id)}
                disabled={isCancelling}
                className="text-red-600 hover:text-red-900 disabled:opacity-50 font-medium text-sm"
              >
                {isCancelling ? 'Cancelling...' : 'Cancel'}
              </button>
            </>
          )}

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
  );
};

export default AppointmentCard;
