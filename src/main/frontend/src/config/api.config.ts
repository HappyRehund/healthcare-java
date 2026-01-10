const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  ENDPOINTS: {
    LOGIN: '/api/v1/auth/login',
    REGISTER: '/api/v1/auth/register',
    DOCTORS: '/api/v1/doctors',
    APPOINTMENTS: '/api/v1/appointments',
    USERS: '/api/v1/users'
  },
  VIDEOSDK_TOKEN: import.meta.env.VITE_VIDEOSDK_TOKEN
} as const;

export default API_CONFIG;