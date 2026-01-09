import { useNavigate } from "react-router-dom";
import type { AppointmentResponse, DoctorResponse, ErrorResponse } from "../types/api.types"
import { useState, useMemo, type ChangeEvent } from "react";
import API_CONFIG from "../config/api.config";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

type ModalMode = 'booking' | 'reschedule'

interface AppointmentScheduleModalProps {
  doctor?: DoctorResponse;
  isOpen: boolean;
  onClose: () => void;
  mode: ModalMode;
  appointmentId?: number;
  initialDate?: string;
  initialTime?: string;
}

const AppointmentScheduleModal = ({
  doctor,
  isOpen,
  onClose,
  mode = 'booking',
  appointmentId,
  initialDate,
  initialTime
}: AppointmentScheduleModalProps
) => {

  const navigate = useNavigate();

  const [selectedSpecialization, setSelectedSpecialization] = useState('');
  const [selectedDate, setSelectedDate] = useState<Date | null>(initialDate ? new Date(initialDate): null);
  const [selectedTime, setSelectedTime] = useState(initialTime || '');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Get the selected specialization details to filter availabilities
  const selectedSpec = useMemo(() => {
    if (!doctor) return undefined;
    return doctor.specializations.find(
      spec => spec.doctor_specialization_id === parseInt(selectedSpecialization)
    );
  }, [selectedSpecialization, doctor]);

  // Filter availabilities based on selected date and consultation type
  const availableTimeSlots = useMemo(() => {
    if (!selectedDate || !selectedSpec || !doctor) return [];

    const selectedDateStr = selectedDate.toLocaleDateString("en-CA");

    return doctor.doctor_availabilities
      .filter(availability => {
        const availabilityDate = availability.start_date_time.split('T')[0];
        return (
          availability.available &&
          availability.consultation_type === selectedSpec.consultation_type &&
          availabilityDate === selectedDateStr
        );
      })
      .flatMap(availability => {

        const slots: string[] = [];
        const startHour = parseInt(availability.start_date_time.split('T')[1].split(':')[0]);
        const endHour = parseInt(availability.end_date_time.split('T')[1].split(':')[0]);

        for (let hour = startHour; hour < endHour; hour++) {
          slots.push(`${hour.toString().padStart(2, '0')}:00`);
        }
        return slots;
      });
  }, [selectedDate, selectedSpec, doctor]);


  const availableDates = useMemo(() => {
    if (!selectedSpec || !doctor) return [];

    return doctor.doctor_availabilities
      .filter(availability =>
        availability.available &&
        availability.consultation_type === selectedSpec.consultation_type
      )
      .map(availability => new Date(availability.start_date_time.split('T')[0]));
  }, [selectedSpec, doctor]);


  const handleSpecializationChange = (value: string) => {
    setSelectedSpecialization(value);
    setSelectedDate(null);
    setSelectedTime('');
  };

  const handleDateChange = (date: Date | null) => {
    setSelectedDate(date);
    setSelectedTime('');
  };

  const handleBook = async () => {

    if (mode === "booking") {
      if(!selectedSpecialization || !selectedDate || !selectedTime){
        setError('Please fill in all required fields');
        return;
      }
    } else {
      if(!selectedDate || !selectedTime){
        setError('Please fill in all required fields');
        return;
      }
    }

    setLoading(true);
    setError('');

    const endTime = new Date(`2000-01-01 ${selectedTime}`);
    endTime.setHours(endTime.getHours() + 1);

    const endTimeString = `${endTime.getHours().toString().padStart(2, '0')}:00`;

    try {
      const token = localStorage.getItem('token');
      let response;

      if (mode === "reschedule"){
        response = await fetch(
          `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.APPOINTMENTS}/${appointmentId}/reschedule`,
          {
            method: 'PUT',
            headers: {
              'Content-Type': "application/json",
              'Authorization': `Bearer ${token}`,
              'Accept': '*/*'
            },
            body: JSON.stringify({
              appointment_date: selectedDate.toLocaleDateString("en-CA"),
              start_time: selectedTime,
              end_time: endTimeString,
            })
          }
        )

        if (!response.ok){
          const errorData: ErrorResponse = await response.json();
          throw new Error(errorData.message);
        }
      } else {
        response = await fetch(
          `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.APPOINTMENTS}/book`,
          {
            method: 'POST',
            headers: {
              'Content-Type': "application/json",
              'Authorization': `Bearer ${token}`,
              'Accept': '*/*'
            },
            body: JSON.stringify({
              doctor_id: doctor?.doctor_id,
              doctor_specialization_id: parseInt(selectedSpecialization),
              appointment_date: selectedDate.toLocaleDateString("en-CA"),
              start_time: selectedTime,
              end_time: endTimeString,
              consultation_type: selectedSpec?.consultation_type
            })
          }
        )

        if (!response.ok){
          const errorData: ErrorResponse = await response.json();
          throw new Error(errorData.message);
        }
      }



      const data: AppointmentResponse = await response.json();

      navigate(`/appointments/${mode === "reschedule" ? appointmentId : data.appointment_id}`, {
        state: {
          message:
            mode === "reschedule"
              ? 'Appointment Rescheduled Successfully'
              : 'Appointment Booked Successfully',
          type: 'success'
        }
      })
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError("failed to Book")
      }
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/10 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">
            {
              mode === "reschedule"
                ? "Reschedule Appointment"
                : "Book Appointment"
            }
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            x
          </button>
        </div>

        <div className="space-y-4">
          {/* Specialization Selection (ONLY BOOKING)*/}

          {mode === "booking" && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Select Specialization
              </label>

              <select
                value={selectedSpecialization}
                onChange={(e: ChangeEvent<HTMLSelectElement>) => handleSpecializationChange(e.target.value)}
                className="w-full border border-gray-300 rounded-md p-2"
              >
                <option value="">Select a specialization</option>
                {doctor?.specializations.map((spec) => (
                  <option key={`${spec.doctor_specialization_id}-${spec.consultation_type}`} value={spec.doctor_specialization_id}>
                    {spec.specialization_name} - {spec.consultation_type}
                  </option>
                ))}
              </select>
            </div>
          )}


          {/* Date Selection */}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Select Date
            </label>

            <DatePicker
              selected={selectedDate}
              onChange={handleDateChange}
              minDate={new Date()}
              includeDates={availableDates}
              className="w-full border border-gray-300 rounded-md p-2"
              placeholderText={selectedSpec ? "Select available date" : "Select specialization first"}
              disabled={!selectedSpec}
            />
            {selectedSpec && availableDates.length === 0 && (
              <p className="text-sm text-orange-500 mt-1">No available dates for this consultation type</p>
            )}
          </div>

          {/* Time Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Select Time
            </label>

            <select
              value={selectedTime}
              onChange={(e: ChangeEvent<HTMLSelectElement>) => setSelectedTime(e.target.value)}
              className="w-full border border-gray-300 rounded-md p-2"
              disabled={!selectedDate || availableTimeSlots.length === 0}
            >
              <option value="">
                {!selectedDate
                  ? "Select date first"
                  : availableTimeSlots.length === 0
                    ? "No available time slots"
                    : "Select Time Slot"}
              </option>
              {availableTimeSlots.map((time) => (
                <option key={time} value={time}>
                  {time}
                </option>
              ))}
            </select>
          </div>

          {error && (
            <div className="text-red-500 text-sm">{error}</div>
          )}

          <button
            onClick={handleBook}
            disabled={loading || (mode === "booking" && !selectedSpecialization) || !selectedDate || !selectedTime}
            className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            {loading
              ? 'Processing...'
              : mode === "reschedule"
                ? 'Confirm Reschedule'
                : 'Confirm Booking'}
          </button>

        </div>


      </div>
    </div>
  )
}

export default AppointmentScheduleModal;