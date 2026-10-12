import React, { useEffect, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../context/ToastContext'
import axiosClient from '../../api/axiosClient'
import Loader from '../../components/common/Loader'

export default function ProfilePage() {
  const { user, setUser } = useAuth()
  const { showToast } = useToast()
  const [form, setForm] = useState({ fullName: '', phone: '' })
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosClient.get('/users/me')
      .then((res) => {
        const d = res.data?.data || res.data
        setForm({ fullName: d.fullName || '', phone: d.phone || '' })
      })
      .finally(() => setLoading(false))
  }, [])

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const res = await axiosClient.put('/users/me', form)
      showToast('Profile updated ✅', 'success')
    } catch (err) {
      showToast(err.response?.data?.message || 'Could not update profile', 'error')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">My Profile</h1>
      <div className="bg-white rounded-2xl shadow p-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center text-3xl">
            👤
          </div>
          <div>
            <p className="font-semibold text-gray-900">{user?.fullName}</p>
            <p className="text-gray-500 text-sm">{user?.email}</p>
            <p className="text-xs text-orange-600 font-medium mt-0.5">
              {user?.roles?.map((r) => r.replace('ROLE_', '')).join(', ')}
            </p>
          </div>
        </div>

        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
            <input
              value={form.fullName}
              onChange={(e) => setForm({ ...form, fullName: e.target.value })}
              className="w-full border border-gray-300 rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-orange-400"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              value={user?.email || ''}
              disabled
              className="w-full border border-gray-200 bg-gray-50 rounded-xl px-4 py-2.5 text-gray-400 cursor-not-allowed"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
            <input
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              className="w-full border border-gray-300 rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-orange-400"
            />
          </div>
          <button
            type="submit"
            disabled={saving}
            className="w-full bg-orange-500 text-white font-semibold py-3 rounded-xl hover:bg-orange-600 transition disabled:opacity-60"
          >
            {saving ? 'Saving…' : 'Save Changes'}
          </button>
        </form>
      </div>
    </div>
  )
}
