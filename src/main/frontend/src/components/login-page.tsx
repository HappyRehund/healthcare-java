import { useState } from "react";
import { useLocation, useNavigate} from "react-router-dom";
import type { ErrorResponse, LoginResponse } from "../types/api.types";

interface  LoginFormData {
  username: string;
  password: string;
}


const LoginPage = () => {

  const navigate = useNavigate();
  const location = useLocation();

  const [formData, setFormData] = useState<LoginFormData>({
    username: '',
    password: ''
  });

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const successMessage = location.state?.message;
  const messageType = location.state?.type;

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {

    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await fetch('http://localhost:8080/api/v1/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'accept': '*/*'
        },
        body: JSON.stringify(formData),
        credentials: 'include'
      });
      if (!response.ok){
        const errorData: ErrorResponse = await response.json();
        throw new Error(errorData.message);
      }

      const data: LoginResponse = await response.json();

      localStorage.setItem('token', data.token);
      localStorage.setItem('userData', JSON.stringify({
        userId: data.user_id,
        username: data.username,
        email: data.email,
        roles: data.roles
      }))

      navigate('/home');
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError("failed to login")
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sign in to your account
          </h2>
          {
            successMessage && messageType === 'success' && (
              <div className="mt-2 p-2 bg-green-100 border border-green-400 text-green-700 text-center rounded">
                {successMessage}
              </div>
            )
          }
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="rounded-md shadow-sm space-y-4">
            <div>
              <label htmlFor="username" className="sr-only">
                Username
              </label>
              <input
                id="username"
                name="username"
                type="text"
                required
                placeholder="your username"
                value={formData.username}
                className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900
                focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                onChange= {
                  (e: React.ChangeEvent<HTMLInputElement>) => setFormData({...formData, username: e.target.value})
                }
              />
            </div>
            <div>
              <label htmlFor="password" className="sr-only">
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                required
                placeholder="your password"
                value={formData.password}
                className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900
                focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                onChange= {
                  (e: React.ChangeEvent<HTMLInputElement>) => setFormData({...formData, password: e.target.value})
                }
              />
            </div>
          </div>

          {
            error && (
              <div className="text-red-500 text-sm text-center">{error}</div>
            )
          }

          <div className="flex flex-col space-y-4">
            <button
              type="submit"
              disabled={loading}
              className="w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700
              focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>

            <button
              type="button"
              onClick={() => navigate('/register')}
              className="w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700
              focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              Create New Account
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default LoginPage;