export interface LoginResponse {
  token: string;
  user_id: number;
  username: string;
  email: string;
  roles: string[]
}

export interface ErrorResponse {
  code: number;
  message: string;
  timestamp: string;
}