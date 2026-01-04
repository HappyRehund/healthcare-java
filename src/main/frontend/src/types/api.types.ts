export interface LoginResponse {
  token: string;
  user_id: number;
  username: string;
  email: string;
  roles: string[]
}

export interface RegisterResponse {
  user_id: number;
  username: string;
  email: string;
  roles: string[];
  enabled: boolean;
}

interface DoctorSpecialization {
  specialization_id: number;
  specialization_name: string;
  description: string;
  base_fee: number;
  hospital_fee: number;
  consultation_type: string;
}

interface DoctorAvailability {
  doctor_availability_id: number;
  start_date_time: string;
  end_date_time: string;
  consultation_type: string;
  available: boolean;
}

export interface DoctorInfo {
  doctor_id: number;
  user_id: number;
  bio: string;
  name: string;
  email: string;
  hospital_id: number;
  hospital_name: string;
  specializations: DoctorSpecialization[];
  doctor_availabilities: DoctorAvailability[];
}

export interface GetDoctorsResponse {
  content: DoctorInfo[];
  total_elements: number;
  total_pages: number;
  current_page: number;
  page_size: number;
}

export interface PaymentDetail {
  payment_id: number;
  appointment_id: number;
  amount: number;
  payment_method: string;
  transaction_id: string;
  payment_status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  created_at: string;
  external_id: string;
  external_status: string;
  payment_url?: string;
}

export interface BookAppointmentResponse {
  appointment_id: number;
  patient_id: number;
  patient_name: string;
  doctor_id: number;
  doctor_name: string;
  doctor_specialization_id: number;
  hospital_id: number;
  hospital_name: string;
  consultation_type: string;
  appointment_date: string;
  start_time: string;
  end_time: string;
  status: 'PENDING' | 'SCHEDULED' | 'CANCELLED' | 'COMPLETED' | 'NO_SHOW';
  payment_detail: PaymentDetail;
}

export interface ErrorResponse {
  code: number;
  message: string;
  timestamp: string;
}