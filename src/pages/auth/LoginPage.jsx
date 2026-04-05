import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { Link } from 'react-router-dom'
import { Building2 } from 'lucide-react'

export default function LoginPage() {
  const { login } = useAuth()
  const [form, setForm] = useState({
    phone: '', password: ''
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    await login(form)
    setLoading(false)
  }

  return (
    <div className="min-h-screen bg-gray-50
      flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-sm
        border border-gray-200 p-8 w-full max-w-md">

        <div className="flex items-center gap-2 mb-8">
          <Building2 className="text-blue-600" size={28}/>
          <div>
            <h1 className="text-xl font-bold
              text-gray-900">Labour Wage System</h1>
            <p className="text-xs text-gray-500">
              Sign in to your account
            </p>
          </div>
        </div>

        <form onSubmit={handleSubmit}
          className="space-y-4">
          <div>
            <label className="block text-sm
              font-medium text-gray-700 mb-1">
              Phone Number
            </label>
            <input
              type="tel"
              placeholder="9876543210"
              value={form.phone}
              onChange={e => setForm({
                ...form, phone: e.target.value })}
              className="w-full border border-gray-300
                rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2
                focus:ring-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm
              font-medium text-gray-700 mb-1">
              Password
            </label>
            <input
              type="password"
              placeholder="••••••••"
              value={form.password}
              onChange={e => setForm({
                ...form, password: e.target.value })}
              className="w-full border border-gray-300
                rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2
                focus:ring-blue-500"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white
              rounded-lg py-2.5 text-sm font-medium
              hover:bg-blue-700 disabled:opacity-50
              transition-colors">
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <p className="text-center text-sm
          text-gray-500 mt-6">
          No account?{' '}
          <Link to="/register"
            className="text-blue-600 hover:underline">
            Register here
          </Link>
        </p>
      </div>
    </div>
  )
}