export type RoleType = 'PATIENT' | 'DOCTOR' | 'HOSPITAL_ADMIN' | 'SUPER_ADMIN';


export interface LoginResponse {
  token: string;
  user_id: number;
  username: string;
  email: string;
  roles: RoleType[]
}

export interface UserResponse {
  user_id: number;
  username: string;
  email: string;
  enabled: boolean;
  roles: RoleType[];
}

export interface RegisterResponse {
  user_id: number;
  username: string;
  email: string;
  roles: RoleType[];
  enabled: boolean;
}

interface DoctorSpecialization {
  doctor_specialization_id: number;
  specialization_id: number;
  specialization_name: string;
  description: string;
  fee: number;
  consultation_type: string;
}

interface DoctorAvailability {
  doctor_availability_id: number;
  start_date_time: string;
  end_date_time: string;
  consultation_type: string;
  available: boolean;
}

export interface DoctorResponse {
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
  content: DoctorResponse[];
  total_elements: number;
  total_pages: number;
  current_page: number;
  page_size: number;
}

export interface PaymentResponse {
  payment_id: number;
  appointment_id: number;
  amount: number;
  payment_method: string;
  transaction_id: string;
  payment_status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  created_at: string;
  external_id?: string;
  external_status?: string;
  payment_url?: string;
}

export interface AppointmentResponse {
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
  payment_detail: PaymentResponse;
}

export interface ErrorResponse {
  code: number;
  message: string;
  timestamp: string;
}