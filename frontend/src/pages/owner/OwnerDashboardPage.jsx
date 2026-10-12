import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getOwnerDashboard } from '../../api/dashboardApi'
import Loader from '../../components/common/Loader'
import { formatCurrency } from '../../utils/formatters'

export default function OwnerDashboardPage() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getOwnerDashboard()
      .then((res) => setData(res.data?.data || res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Loader />

  const stats = [
    { label: 'Total Orders', value: data?.totalOrders ?? 0, icon: '📦', color: 'blue' },
    { label: 'Pending Orders', value: data?.pendingOrders ?? 0, icon: '⏳', color: 'yellow' },
    { label: "Today's Revenue", value: formatCurrency(data?.todayRevenue), icon: '💰', color: 'green' },
    { label: 'Total Revenue', value: formatCurrency(data?.totalRevenue), icon: '💵', color: 'orange' },
  ]

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Owner Dashboard</h1>
        <div className="flex gap-3">
          <Link to="/owner/orders" className="bg-orange-500 text-white px-4 py-2 rounded-xl font-medium hover:bg-orange-600 transition text-sm">
            View Orders
          </Link>
          <Link to="/owner/menu" className="bg-gray-100 text-gray-700 px-4 py-2 rounded-xl font-medium hover:bg-gray-200 transition text-sm">
            Manage Menu
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-5 mb-8">
        {stats.map(({ label, value, icon }) => (
          <div key={label} className="bg-white rounded-2xl shadow p-5 text-center">
            <div className="text-3xl mb-2">{icon}</div>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            <p className="text-gray-500 text-sm mt-1">{label}</p>
          </div>
        ))}
      </div>

      {data?.restaurantName && (
        <div className="bg-white rounded-2xl shadow p-5 mb-6">
          <h2 className="font-semibold text-gray-800 mb-2">🍽️ Your Restaurant</h2>
          <p className="text-gray-700 font-medium text-lg">{data.restaurantName}</p>
          <div className="flex gap-4 mt-2 text-sm text-gray-500">
            <Link to="/owner/restaurant" className="text-orange-500 hover:underline">Edit Details</Link>
            <Link to="/owner/menu" className="text-orange-500 hover:underline">Manage Menu</Link>
          </div>
        </div>
      )}

      {data?.recentOrders?.length > 0 && (
        <div className="bg-white rounded-2xl shadow p-5">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-semibold text-gray-800">Recent Orders</h2>
            <Link to="/owner/orders" className="text-orange-500 text-sm hover:underline">View all</Link>
          </div>
          <div className="space-y-3">
            {data.recentOrders.slice(0, 5).map((o) => (
              <div key={o.id} className="flex items-center justify-between py-2 border-b last:border-0">
                <div>
                  <p className="font-medium text-gray-800">#{o.orderNumber}</p>
                  <p className="text-gray-400 text-xs">{o.items?.length} item(s)</p>
                </div>
                <span className={`px-3 py-1 text-xs font-semibold rounded-full ${
                  o.orderStatus === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                  o.orderStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                  'bg-blue-100 text-blue-700'
                }`}>
                  {o.orderStatus}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
