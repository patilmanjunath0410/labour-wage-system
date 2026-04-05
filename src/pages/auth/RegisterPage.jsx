import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { Link } from 'react-router-dom'
import { Building2 } from 'lucide-react'

export default function RegisterPage() {
  const { register } = useAuth()
  const [form, setForm] = useState({
    name: '', phone: '', password: '',
    companyName: '', state: ''
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    await register(form)
    setLoading(false)
  }

  const states = [
    'Karnataka','Maharashtra','Delhi',
    'Tamil Nadu','Telangana','Gujarat',
    'Rajasthan','Uttar Pradesh','Punjab'
  ]

  return (
    <div className="min-h-screen bg-gray-50
      flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-sm
        border border-gray-200 p-8 w-full max-w-md">

        <div className="flex items-center gap-2 mb-8">
          <Building2 className="text-blue-600" size={28}/>
          <div>
            <h1 className="text-xl font-bold
              text-gray-900">Create Account</h1>
            <p className="text-xs text-gray-500">
              Register your construction company
            </p>
          </div>
        </div>

        <form onSubmit={handleSubmit}
          className="space-y-4">
          {[
            ['Your Name','name','text','Ravi Kumar'],
            ['Company Name','companyName','text',
              'Ravi Constructions'],
            ['Phone Number','phone','tel','9876543210'],
            ['Password','password','password','••••••••'],
          ].map(([label, key, type, ph]) => (
            <div key={key}>
              <label className="block text-sm
                font-medium text-gray-700 mb-1">
                {label}
              </label>
              <input
                type={type}
                placeholder={ph}
                value={form[key]}
                onChange={e => setForm({
                  ...form, [key]: e.target.value })}
                className="w-full border border-gray-300
                  rounded-lg px-3 py-2 text-sm
                  focus:outline-none focus:ring-2
                  focus:ring-blue-500"
                required
              />
            </div>
          ))}

          <div>
            <label className="block text-sm
              font-medium text-gray-700 mb-1">
              State
            </label>
            <select
              value={form.state}
              onChange={e => setForm({
                ...form, state: e.target.value })}
              className="w-full border border-gray-300
                rounded-lg px-3 py-2 text-sm
                focus:outline-none focus:ring-2
                focus:ring-blue-500"
              required>
              <option value="">Select state</option>
              {states.map(s => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white
              rounded-lg py-2.5 text-sm font-medium
              hover:bg-blue-700 disabled:opacity-50
              transition-colors">
            {loading ? 'Creating...' : 'Create Account'}
          </button>
        </form>

        <p className="text-center text-sm
          text-gray-500 mt-6">
          Already have an account?{' '}
          <Link to="/login"
            className="text-blue-600 hover:underline">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  )
}