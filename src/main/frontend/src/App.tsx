import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import LoginPage from "./components/login-page";
import LandingPage from "./components/landing-page";
import RegisterPage from "./components/register-page";

interface ProtectedRouteProps {
  children: React.ReactNode
}

const ProtectedRoute = ({children}: ProtectedRouteProps) => {
  const token = localStorage.getItem('token');

  if(!token){
    return <Navigate to="/login" replace />
  }

  return children;
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage/>} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/home"
          element={
            <ProtectedRoute>
              <LandingPage />
            </ProtectedRoute>
          }
        />
        <Route path="/" element={<Navigate to="/home" replace />} />
      </Routes>
    </Router>
  )
}

export default App
