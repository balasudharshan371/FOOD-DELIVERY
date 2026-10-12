import React, { useEffect, useState } from 'react'
import { getAllUsers, updateUserStatus } from '../../api/dashboardApi'
import Loader from '../../components/common/Loader'
import { useToast } from '../../context/ToastContext'
import { formatDate } from '../../utils/formatters'

export default function AdminUsersPage() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const { showToast } = useToast()

  const load = () => {
    setLoading(true)
    getAllUsers()
      .then((res) => {
        const d = res.data?.data || res.data
        setUsers(Array.isArray(d) ? d : d?.content || [])
      })
      .catch(() => setUsers([]))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleToggle = async (user) => {
    const newStatus = user.accountStatus === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE'
    try {
      await updateUserStatus(user.id, newStatus)
      showToast(`User ${newStatus === 'ACTIVE' ? 'activated' : 'suspended'}`, 'success')
      load()
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to update user', 'error')
    }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Manage Users</h1>
      <div className="bg-white rounded-2xl shadow overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Name', 'Email', 'Phone', 'Roles', 'Joined', 'Status', 'Action'].map((h) => (
                <th key={h} className="px-5 py-3 text-left font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {users.map((u) => (
              <tr key={u.id} className="hover:bg-gray-50">
                <td className="px-5 py-3 font-medium text-gray-900">{u.fullName}</td>
                <td className="px-5 py-3 text-gray-600">{u.email}</td>
                <td className="px-5 py-3 text-gray-500">{u.phone}</td>
                <td className="px-5 py-3">
                  {u.roles?.map((r) => (
                    <span key={r} className="px-2 py-0.5 bg-blue-100 text-blue-700 text-xs rounded-full mr-1">
                      {r.replace('ROLE_', '')}
                    </span>
                  ))}
                </td>
                <td className="px-5 py-3 text-gray-400">{formatDate(u.createdAt)}</td>
                <td className="px-5 py-3">
                  <span className={`px-2 py-1 text-xs rounded-full font-medium ${
                    u.accountStatus === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                  }`}>
                    {u.accountStatus}
                  </span>
                </td>
                <td className="px-5 py-3">
                  <button
                    onClick={() => handleToggle(u)}
                    className={`text-xs font-medium px-3 py-1.5 rounded-lg transition ${
                      u.accountStatus === 'ACTIVE'
                        ? 'bg-red-50 text-red-600 hover:bg-red-100'
                        : 'bg-green-50 text-green-600 hover:bg-green-100'
                    }`}
                  >
                    {u.accountStatus === 'ACTIVE' ? 'Suspend' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {users.length === 0 && (
          <div className="text-center py-16 text-gray-400">No users found</div>
        )}
      </div>
    </div>
  )
}
