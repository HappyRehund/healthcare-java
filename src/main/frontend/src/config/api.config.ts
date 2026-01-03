import { config } from 'dotenv'

config();

const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_BASE_URL || 'localhost:8080',
  ENDPOINTS: {
    LOGIN: '/api/v1/auth/login',
    REGISTER: '/api/v1/auth/register',
    DOCTORS: '/api/v1/doctors'
  }
}

export default API_CONFIG;