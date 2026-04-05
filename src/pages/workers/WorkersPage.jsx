import { useState, useEffect } from 'react'
import { getWorkersBySite, registerWorker }
  from '../../api/workers'
import api from '../../api/axios'
import toast from 'react-hot-toast'
import { UserPlus, Download, ArrowLeft } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

const SITE_ID = '21ebf0f3-4142-4cb9-aed2-27ea4691e307'

const SKILLS = [
  'MASON','CARPENTER','ELECTRICIAN',
  'PLUMBER','WELDER','PAINTER','HELPER','OTHER'
]

export default function WorkersPage() {
  const navigate = useNavigate()
  const [workers, setWorkers]   = useState([])
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading]   = useState(false)
  const [form, setForm] = useState({
    fullName: '', phone: '', aadhaar: '',
    dateOfBirth: '', gender: 'Male',
    address: '', skillType: 'MASON',
    dailyWageRate: '', siteId: SITE_ID
  })

  useEffect(() => { fetchWorkers() }, [])

  const fetchWorkers = async () => {
    try {
      const res = await getWorkersBySite(SITE_ID)
      setWorkers(res.data)
    } catch {
      toast.error('Failed to load workers')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await registerWorker({
        ...form,
        dailyWageRate: parseFloat(form.dailyWageRate)
      })
      toast.success('Worker registered!')
      setShowForm(false)
      fetchWorkers()
      setForm({
        fullName: '', phone: '', aadhaar: '',
        dateOfBirth: '', gender: 'Male',
        address: '', skillType: 'MASON',
        dailyWageRate: '', siteId: SITE_ID
      })
    } catch (err) {
      toast.error(
        err.response?.data?.error ||
        'Registration failed')
    }
    setLoading(false)
  }

  const downloadQR = async (id, code) => {
    try {
      const res = await api.get(
        `/workers/${id}/qr`,
        { responseType: 'blob' })
      const url = URL.createObjectURL(res.data)
      const a   = document.createElement('a')
      a.href     = url
      a.download = `${code}-QR.png`
      a.click()
      URL.revokeObjectURL(url)
      toast.success('QR downloaded!')
    } catch {
      toast.error('Failed to download QR')
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-5xl mx-auto">

        <div className="flex justify-between
          items-center mb-6">
          <div className="flex items-center gap-3">
            <button onClick={() => navigate('/dashboard')}
              className="text-gray-500
                hover:text-gray-700">
              <ArrowLeft size={20}/>
            </button>
            <div>
              <h1 className="text-xl font-bold
                text-gray-900">Workers</h1>
              <p className="text-sm text-gray-500">
                {workers.length} registered
              </p>
            </div>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className="flex items-center gap-2
              bg-blue-600 text-white px-4 py-2
              rounded-lg text-sm font-medium
              hover:bg-blue-700">
            <UserPlus size={16}/>
            Register Worker
          </button>
        </div>

        {showForm && (
          <div className="bg-white border
            border-gray-200 rounded-2xl p-6 mb-6">
            <h2 className="font-semibold
              text-gray-900 mb-4">New Worker</h2>
            <form onSubmit={handleSubmit}
              className="grid grid-cols-2 gap-4">
              {[
                ['Full Name','fullName','text',
                  'Suresh Kumar'],
                ['Phone','phone','tel','9123456780'],
                ['Aadhaar (12 digits)','aadhaar',
                  'text','123456789012'],
                ['Date of Birth','dateOfBirth','date',''],
                ['Address','address','text',
                  '12 MG Road, Bangalore'],
                ['Daily Wage (Rs.)','dailyWageRate',
                  'number','650'],
              ].map(([label, key, type, ph]) => (
                <div key={key}>
                  <label className="block text-xs
                    font-medium text-gray-600 mb-1">
                    {label}
                  </label>
                  <input
                    type={type}
                    placeholder={ph}
                    value={form[key]}
                    onChange={e => setForm({
                      ...form, [key]: e.target.value })}
                    className="w-full border
                      border-gray-300 rounded-lg
                      px-3 py-2 text-sm
                      focus:outline-none focus:ring-2
                      focus:ring-blue-500"
                    required
                  />
                </div>
              ))}

              <div>
                <label className="block text-xs
                  font-medium text-gray-600 mb-1">
                  Gender
                </label>
                <select
                  value={form.gender}
                  onChange={e => setForm({
                    ...form, gender: e.target.value })}
                  className="w-full border border-gray-300
                    rounded-lg px-3 py-2 text-sm
                    focus:outline-none focus:ring-2
                    focus:ring-blue-500">
                  <option>Male</option>
                  <option>Female</option>
                  <option>Other</option>
                </select>
              </div>

              <div>
                <label className="block text-xs
                  font-medium text-gray-600 mb-1">
                  Skill Type
                </label>
                <select
                  value={form.skillType}
                  onChange={e => setForm({
                    ...form, skillType: e.target.value })}
                  className="w-full border border-gray-300
                    rounded-lg px-3 py-2 text-sm
                    focus:outline-none focus:ring-2
                    focus:ring-blue-500">
                  {SKILLS.map(s => (
                    <option key={s}>{s}</option>
                  ))}
                </select>
              </div>

              <div className="col-span-2
                flex justify-end gap-3 mt-2">
                <button type="button"
                  onClick={() => setShowForm(false)}
                  className="px-4 py-2 text-sm
                    text-gray-600 hover:text-gray-900">
                  Cancel
                </button>
                <button type="submit"
                  disabled={loading}
                  className="bg-blue-600 text-white
                    px-6 py-2 rounded-lg text-sm
                    font-medium hover:bg-blue-700
                    disabled:opacity-50">
                  {loading ? 'Saving...' : 'Register'}
                </button>
              </div>
            </form>
          </div>
        )}

        <div className="bg-white border
          border-gray-200 rounded-2xl overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b
              border-gray-200">
              <tr>
                {['Code','Name','Phone','Skill',
                  'Daily Rate','Status','QR'].map(h => (
                  <th key={h} className="text-left
                    px-4 py-3 text-xs font-medium
                    text-gray-500 uppercase tracking-wide">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {workers.map(w => (
                <tr key={w.id}
                  className="hover:bg-gray-50">
                  <td className="px-4 py-3 text-xs
                    font-mono text-gray-600">
                    {w.workerCode}
                  </td>
                  <td className="px-4 py-3 text-sm
                    font-medium text-gray-900">
                    {w.fullName}
                  </td>
                  <td className="px-4 py-3 text-sm
                    text-gray-600">{w.phone}</td>
                  <td className="px-4 py-3">
                    <span className="bg-blue-50
                      text-blue-700 text-xs font-medium
                      px-2 py-1 rounded-full">
                      {w.skillType}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm
                    font-medium text-gray-900">
                    ₹{w.dailyWageRate}
                  </td>
                  <td className="px-4 py-3">
                    <span className="bg-green-50
                      text-green-700 text-xs font-medium
                      px-2 py-1 rounded-full">
                      {w.status}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() =>
                        downloadQR(w.id, w.workerCode)}
                      className="flex items-center gap-1
                        text-xs text-blue-600
                        hover:text-blue-800 font-medium">
                      <Download size={13}/>
                      QR
                    </button>
                  </td>
                </tr>
              ))}
              {workers.length === 0 && (
                <tr>
                  <td colSpan={7}
                    className="px-4 py-10 text-center
                      text-sm text-gray-400">
                    No workers registered yet.
                    Click Register Worker to add one.
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