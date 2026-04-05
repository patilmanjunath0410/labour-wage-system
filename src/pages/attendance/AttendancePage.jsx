import { useState, useEffect } from 'react'
import { getTodayAttendance } from '../../api/attendance'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, CheckCircle, XCircle } from 'lucide-react'
import toast from 'react-hot-toast'

const SITE_ID = '21ebf0f3-4142-4cb9-aed2-27ea4691e307'

export default function AttendancePage() {
  const navigate = useNavigate()
  const [records, setRecords] = useState([])

  useEffect(() => { fetchAttendance() }, [])

  const fetchAttendance = async () => {
    try {
      const res = await getTodayAttendance(SITE_ID)
      setRecords(res.data)
    } catch {
      toast.error('Failed to load attendance')
    }
  }

  const typeColor = (type) => {
    const map = {
      FULL_DAY:        'bg-green-50 text-green-700',
      HALF_DAY:        'bg-yellow-50 text-yellow-700',
      OVERTIME:        'bg-blue-50 text-blue-700',
      DOUBLE_OVERTIME: 'bg-purple-50 text-purple-700',
      ABSENT:          'bg-red-50 text-red-700',
    }
    return map[type] || 'bg-gray-50 text-gray-700'
  }

  const present = records.filter(
    r => r.attendanceType !== 'ABSENT').length
  const totalWage = records.reduce(
    (sum, r) => sum + (r.computedWage || 0), 0)

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-5xl mx-auto">

        <div className="flex items-center gap-3 mb-6">
          <button onClick={() => navigate('/dashboard')}
            className="text-gray-500 hover:text-gray-700">
            <ArrowLeft size={20}/>
          </button>
          <div>
            <h1 className="text-xl font-bold
              text-gray-900">Today's Attendance</h1>
            <p className="text-sm text-gray-500">
              {new Date().toLocaleDateString('en-IN', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
              })}
            </p>
          </div>
        </div>

        {/* Summary cards */}
        <div className="grid grid-cols-3 gap-4 mb-6">
          <div className="bg-white border
            border-gray-200 rounded-xl p-4">
            <p className="text-xs text-gray-500 mb-1">
              Present
            </p>
            <p className="text-2xl font-bold
              text-green-600">{present}</p>
          </div>
          <div className="bg-white border
            border-gray-200 rounded-xl p-4">
            <p className="text-xs text-gray-500 mb-1">
              Total Records
            </p>
            <p className="text-2xl font-bold
              text-gray-900">{records.length}</p>
          </div>
          <div className="bg-white border
            border-gray-200 rounded-xl p-4">
            <p className="text-xs text-gray-500 mb-1">
              Today's Payroll
            </p>
            <p className="text-2xl font-bold
              text-blue-600">
              ₹{totalWage.toFixed(2)}
            </p>
          </div>
        </div>

        {/* Attendance table */}
        <div className="bg-white border border-gray-200
          rounded-2xl overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b
              border-gray-200">
              <tr>
                {['Worker','Code','Type',
                  'Wage Rate','Earned',
                  'Scanned At'].map(h => (
                  <th key={h} className="text-left px-4
                    py-3 text-xs font-medium
                    text-gray-500 uppercase">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {records.map(r => (
                <tr key={r.id}
                  className="hover:bg-gray-50">
                  <td className="px-4 py-3 text-sm
                    font-medium text-gray-900">
                    {r.workerName}
                  </td>
                  <td className="px-4 py-3 text-xs
                    font-mono text-gray-500">
                    {r.workerCode}
                  </td>
                  <td className="px-4 py-3">
                    <span className={`text-xs font-medium
                      px-2 py-1 rounded-full
                      ${typeColor(r.attendanceType)}`}>
                      {r.attendanceType
                        .replace('_',' ')}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm
                    text-gray-600">
                    ₹{r.dailyWageRate}
                  </td>
                  <td className="px-4 py-3 text-sm
                    font-medium text-gray-900">
                    ₹{r.computedWage}
                  </td>
                  <td className="px-4 py-3 text-xs
                    text-gray-500">
                    {new Date(r.scannedAt)
                      .toLocaleTimeString('en-IN')}
                  </td>
                </tr>
              ))}
              {records.length === 0 && (
                <tr>
                  <td colSpan={6}
                    className="px-4 py-10 text-center
                      text-sm text-gray-400">
                    No attendance marked today yet
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}