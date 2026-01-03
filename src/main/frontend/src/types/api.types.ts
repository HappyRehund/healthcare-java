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

export interface ErrorResponse {
  code: number;
  message: string;
  timestamp: string;
}