import { BrowserRouter, Routes, Route, Navigate }
  from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from './context/AuthContext'
import LoginPage      from './pages/auth/LoginPage'
import RegisterPage   from './pages/auth/RegisterPage'
import DashboardPage  from './pages/dashboard/DashboardPage'
import WorkersPage    from './pages/workers/WorkersPage'
import AttendancePage from './pages/attendance/AttendancePage'
import WageSlipsPage  from './pages/wageslips/WageSlipsPage'

function ProtectedRoute({ children }) {
  const token = localStorage.getItem('token')
  return token ? children : <Navigate to="/login"/>
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login"
        element={<LoginPage/>}/>
      <Route path="/register"
        element={<RegisterPage/>}/>
      <Route path="/dashboard" element={
        <ProtectedRoute>
          <DashboardPage/>
        </ProtectedRoute>
      }/>
      <Route path="/workers" element={
        <ProtectedRoute>
          <WorkersPage/>
        </ProtectedRoute>
      }/>
      <Route path="/attendance" element={
        <ProtectedRoute>
          <AttendancePage/>
        </ProtectedRoute>
      }/>
      <Route path="/wageslips" element={
        <ProtectedRoute>
          <WageSlipsPage/>
        </ProtectedRoute>
      }/>
      <Route path="/"
        element={<Navigate to="/dashboard"/>}/>
    </Routes>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Toaster position="top-right"/>
        <AppRoutes/>
      </AuthProvider>
    </BrowserRouter>
  )
}