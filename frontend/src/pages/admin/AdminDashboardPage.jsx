import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getAdminDashboard } from '../../api/dashboardApi'
import Loader from '../../components/common/Loader'
import { formatCurrency } from '../../utils/formatters'

export default function AdminDashboardPage() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getAdminDashboard()
      .then((res) => setData(res.data?.data || res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Loader />

  const stats = [
    { label: 'Total Users', value: data?.totalUsers ?? 0, icon: '👤', to: '/admin/users' },
    { label: 'Restaurants', value: data?.totalRestaurants ?? 0, icon: '🍽️', to: '/admin/restaurants' },
    { label: 'Total Orders', value: data?.totalOrders ?? 0, icon: '📦', to: '/admin/orders' },
    { label: 'Total Revenue', value: formatCurrency(data?.totalRevenue), icon: '💰', to: '/admin/orders' },
  ]

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">Admin Dashboard</h1>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-5 mb-8">
        {stats.map(({ label, value, icon, to }) => (
          <Link key={label} to={to} className="bg-white rounded-2xl shadow hover:shadow-md transition p-5 text-center block">
            <div className="text-3xl mb-2">{icon}</div>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            <p className="text-gray-500 text-sm mt-1">{label}</p>
          </Link>
        ))}
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {[
          { label: 'Manage Users', to: '/admin/users', icon: '👥' },
          { label: 'Manage Restaurants', to: '/admin/restaurants', icon: '🍽️' },
          { label: 'Manage Categories', to: '/admin/categories', icon: '🏷️' },
          { label: 'Manage Coupons', to: '/admin/coupons', icon: '🎟️' },
          { label: 'View All Orders', to: '/admin/orders', icon: '📋' },
        ].map(({ label, to, icon }) => (
          <Link
            key={label}
            to={to}
            className="bg-white rounded-2xl shadow hover:bg-orange-50 hover:shadow-md transition p-5 flex items-center gap-3"
          >
            <span className="text-2xl">{icon}</span>
            <span className="font-medium text-gray-800">{label}</span>
            <span className="ml-auto text-gray-400">→</span>
          </Link>
        ))}
      </div>
    </div>
  )
}
