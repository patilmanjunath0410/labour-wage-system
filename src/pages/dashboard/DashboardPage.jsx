import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import {
  Users, QrCode, FileText,
  LogOut, Building2, HardHat
} from 'lucide-react'

export default function DashboardPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const cards = [
    {
      title: 'Workers',
      desc: 'Register and manage site workers',
      icon: Users,
      color: 'bg-blue-50 text-blue-600',
      route: '/workers'
    },
    {
      title: 'Attendance',
      desc: 'View today\'s attendance records',
      icon: QrCode,
      color: 'bg-green-50 text-green-600',
      route: '/attendance'
    },
    {
      title: 'Wage Slips',
      desc: 'Generate and download wage slips',
      icon: FileText,
      color: 'bg-purple-50 text-purple-600',
      route: '/wageslips'
    }
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white border-b
        border-gray-200 px-6 py-4
        flex justify-between items-center">
        <div className="flex items-center gap-2">
          <HardHat className="text-blue-600" size={22}/>
          <span className="font-bold text-gray-900">
            Labour Wage System
          </span>
        </div>
        <div className="flex items-center gap-4">
          <span className="text-sm text-gray-600">
            {user?.name}
          </span>
          <button onClick={logout}
            className="flex items-center gap-1
              text-sm text-red-500 hover:text-red-700">
            <LogOut size={16}/>
            Logout
          </button>
        </div>
      </nav>

      <div className="max-w-4xl mx-auto p-6">
        <div className="mb-8">
          <h2 className="text-xl font-bold
            text-gray-900">
            Welcome, {user?.name}
          </h2>
          <p className="text-gray-500 text-sm mt-1">
            Manage your construction site
          </p>
        </div>

        <div className="grid grid-cols-1
          md:grid-cols-3 gap-4">
          {cards.map(card => (
            <button
              key={card.title}
              onClick={() => navigate(card.route)}
              className="bg-white border border-gray-200
                rounded-2xl p-6 text-left
                hover:shadow-md transition-shadow">
              <div className={`w-12 h-12 rounded-xl
                flex items-center justify-center
                mb-4 ${card.color}`}>
                <card.icon size={22}/>
              </div>
              <h3 className="font-semibold
                text-gray-900">{card.title}</h3>
              <p className="text-sm text-gray-500 mt-1">
                {card.desc}
              </p>
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}