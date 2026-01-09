import { useNavigate } from "react-router-dom";
import type { UserData } from "../types/local-storage.types";

const Navbar = () => {
  const navigate = useNavigate();
  const userData: UserData = JSON.parse(localStorage.getItem('userData') || '{}');

  const handleLogout = () => {
    localStorage.removeItem('userData');
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          <div className="text-xl font-semibold">Healthcare Rehund App</div>
          <div className="flex items-center space-x-4">
            <button
              className="text-gray-600 hover:text-gray-900"
              onClick={() => navigate("/home")}
            >
              Home
            </button>
            <button
              className="text-gray-600 hover:text-gray-900"
              onClick={() => navigate("/appointments")}
            >
              My Appointments
            </button>
            <span className="text-gray-700">Welcome, {userData.username}</span>
            <button
              onClick={handleLogout}
              className="text-gray-600 hover:text-gray-900"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
