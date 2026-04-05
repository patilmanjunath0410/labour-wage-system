import { createContext, useContext, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login as loginApi,
         register as registerApi } from '../api/auth'
import toast from 'react-hot-toast'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user')
    return saved ? JSON.parse(saved) : null
  })

  const navigate = useNavigate()

  const login = async (data) => {
    try {
      const res = await loginApi(data)
      const { token, role, name, phone } = res.data
      localStorage.setItem('token', token)
      localStorage.setItem('user',
        JSON.stringify({ role, name, phone }))
      setUser({ role, name, phone })
      toast.success(`Welcome back, ${name}!`)
      navigate('/dashboard')
    } catch (err) {
      toast.error(
        err.response?.data?.error || 'Login failed')
    }
  }

  const register = async (data) => {
    try {
      const res = await registerApi(data)
      const { token, role, name, phone } = res.data
      localStorage.setItem('token', token)
      localStorage.setItem('user',
        JSON.stringify({ role, name, phone }))
      setUser({ role, name, phone })
      toast.success('Account created!')
      navigate('/dashboard')
    } catch (err) {
      toast.error(
        err.response?.data?.error || 'Registration failed')
    }
  }

  const logout = () => {
    localStorage.clear()
    setUser(null)
    navigate('/login')
    toast.success('Logged out')
  }

  return (
    <AuthContext.Provider
      value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)