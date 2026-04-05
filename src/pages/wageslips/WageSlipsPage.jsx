import { useState } from 'react'
import {
  generateWageSlip,
  generateAllWageSlips,
  getWageSlipsBySite,
  downloadWageSlip
} from '../../api/wageslips'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Download, FileText } from 'lucide-react'
import toast from 'react-hot-toast'

const SITE_ID  = '21ebf0f3-4142-4cb9-aed2-27ea4691e307'
const WORKER_ID = ' 047efc24-68c2-468a-9b5e-a94d8587695f'

export default function WageSlipsPage() {
  const navigate = useNavigate()
  const [slips, setSlips]     = useState([])
  const [loading, setLoading] = useState(false)
  const [year, setYear]   = useState(2026)
  const [month, setMonth] = useState(4)

  const fetchSlips = async () => {
    try {
      const res = await getWageSlipsBySite(
        SITE_ID, year, month)
      setSlips(res.data)
    } catch {
      toast.error('Failed to load wage slips')
    }
  }

  const handleGenerate = async () => {
    setLoading(true)
    try {
      await generateWageSlip(WORKER_ID, year, month)
      toast.success('Wage slip generated!')
      fetchSlips()
    } catch (err) {
      toast.error(
        err.response?.data?.error ||
        'Generation failed')
    }
    setLoading(false)
  }

  const handleGenerateAll = async () => {
    setLoading(true)
    try {
      await generateAllWageSlips(SITE_ID, year, month)
      toast.success('All wage slips generated!')
      fetchSlips()
    } catch (err) {
      toast.error('Generation failed')
    }
    setLoading(false)
  }

  const handleDownload = async (slipId, workerCode) => {
    try {
      const res = await downloadWageSlip(slipId)
      const url = URL.createObjectURL(
        new Blob([res.data],
          { type: 'application/pdf' }))
      const a   = document.createElement('a')
      a.href     = url
      a.download = `${workerCode}-wageslip.pdf`
      a.click()
      URL.revokeObjectURL(url)
      toast.success('PDF downloaded!')
    } catch {
      toast.error('Download failed')
    }
  }

  const months = [
    'Jan','Feb','Mar','Apr','May','Jun',
    'Jul','Aug','Sep','Oct','Nov','Dec'
  ]

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-5xl mx-auto">

        <div className="flex items-center gap-3 mb-6">
          <button onClick={() => navigate('/dashboard')}
            className="text-gray-500
              hover:text-gray-700">
            <ArrowLeft size={20}/>
          </button>
          <h1 className="text-xl font-bold
            text-gray-900">Wage Slips</h1>
        </div>

        {/* Controls */}
        <div className="bg-white border border-gray-200
          rounded-2xl p-6 mb-6">
          <div className="flex gap-4 items-end flex-wrap">
            <div>
              <label className="block text-xs
                font-medium text-gray-600 mb-1">
                Year
              </label>
              <input
                type="number"
                value={year}
                onChange={e =>
                  setYear(parseInt(e.target.value))}
                className="border border-gray-300
                  rounded-lg px-3 py-2 text-sm
                  w-24 focus:outline-none
                  focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-xs
                font-medium text-gray-600 mb-1">
                Month
              </label>
              <select
                value={month}
                onChange={e =>
                  setMonth(parseInt(e.target.value))}
                className="border border-gray-300
                  rounded-lg px-3 py-2 text-sm
                  focus:outline-none focus:ring-2
                  focus:ring-blue-500">
                {months.map((m, i) => (
                  <option key={m} value={i + 1}>
                    {m}
                  </option>
                ))}
              </select>
            </div>
            <button
              onClick={fetchSlips}
              className="bg-gray-100 text-gray-700
                px-4 py-2 rounded-lg text-sm
                font-medium hover:bg-gray-200">
              Load
            </button>
            <button
              onClick={handleGenerate}
              disabled={loading}
              className="bg-blue-600 text-white
                px-4 py-2 rounded-lg text-sm
                font-medium hover:bg-blue-700
                disabled:opacity-50">
              Generate for Worker
            </button>
            <button
              onClick={handleGenerateAll}
              disabled={loading}
              className="bg-green-600 text-white
                px-4 py-2 rounded-lg text-sm
                font-medium hover:bg-green-700
                disabled:opacity-50">
              Generate All
            </button>
          </div>
        </div>

        {/* Wage slips table */}
        <div className="bg-white border border-gray-200
          rounded-2xl overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b
              border-gray-200">
              <tr>
                {['Worker','Days','Gross',
                  'PF','ESI','Net','PDF'].map(h => (
                  <th key={h} className="text-left px-4
                    py-3 text-xs font-medium
                    text-gray-500 uppercase">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {slips.map(s => (
                <tr key={s.id}
                  className="hover:bg-gray-50">
                  <td className="px-4 py-3">
                    <div className="text-sm font-medium
                      text-gray-900">{s.workerName}</div>
                    <div className="text-xs text-gray-400
                      font-mono">{s.workerCode}</div>
                  </td>
                  <td className="px-4 py-3 text-sm
                    text-gray-600">
                    {s.totalDaysPresent}
                  </td>
                  <td className="px-4 py-3 text-sm
                    text-gray-900">
                    ₹{s.grossWage}
                  </td>
                  <td className="px-4 py-3 text-sm
                    text-red-500">
                    -₹{s.pfDeduction}
                  </td>
                  <td className="px-4 py-3 text-sm
                    text-red-500">
                    -₹{s.esiDeduction}
                  </td>
                  <td className="px-4 py-3 text-sm
                    font-bold text-green-600">
                    ₹{s.netWage}
                  </td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() =>
                        handleDownload(
                          s.id, s.workerCode)}
                      className="flex items-center gap-1
                        text-xs text-blue-600
                        hover:text-blue-800 font-medium">
                      <Download size={13}/>
                      PDF
                    </button>
                  </td>
                </tr>
              ))}
              {slips.length === 0 && (
                <tr>
                  <td colSpan={7}
                    className="px-4 py-10 text-center
                      text-sm text-gray-400">
                    Select year and month then
                    click Load or Generate
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