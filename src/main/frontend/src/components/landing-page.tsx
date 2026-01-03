import { useState } from "react";
import { useNavigate } from "react-router-dom"
import type { UserData } from "../types/local-storage.types";


const LandingPage = () => {

  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState<string>('')

  const userData: UserData = JSON.parse(localStorage.getItem('userData') || '{}')

  const handleLogout = () => {
    localStorage.removeItem('userData')
    localStorage.removeItem('token')
    navigate('/login')
  };

  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    console.log('Searching for', searchQuery)
  }

  return (
    <div className="min-h-screen bg-gray-500">
      {/* Nav bar */}
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="text-xl font-semibold">Healthcare Rehund App</div>
            <div className="flex items-center space-x-4">
              <span className="text-gray-700">Welcome, {userData.username}</span>
              <button
                onClick={handleLogout}
                className="text-gray-600 hover:text-gray-900"
              >Logout</button>
            </div>
          </div>
        </div>
      </nav>

      {/* Search section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="max-w-3xl mx-auto">

          <form onSubmit={handleSearch} className="space-y-4">
            <div className="flex flex-col items-center space-y-4">
              <h2 className="text-2xl font-bold text-gray-900">Find a doctor</h2>
              <div className="w-full">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setSearchQuery(e.target.value)}
                  placeholder="Search for Doctor..."
                  className="w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <button
                type="submit"
                className="w-full sm:w-auto px-6 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none
                focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Search
              </button>
            </div>
          </form>
        </div>
      </div>

    </div>
  )

}

export default LandingPage;