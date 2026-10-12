import React, { useEffect, useState } from 'react'
import { getAllAdminOrders } from '../../api/orderApi'
import Loader from '../../components/common/Loader'
import { formatCurrency, formatDateTime } from '../../utils/formatters'

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('ALL')

  useEffect(() => {
    setLoading(true)
    getAllAdminOrders()
      .then((res) => {
        const d = res.data?.data || res.data
        setOrders(Array.isArray(d) ? d : d?.content || [])
      })
      .catch(() => setOrders([]))
      .finally(() => setLoading(false))
  }, [])

  const statuses = ['ALL', 'PENDING', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED', 'REJECTED']
  const displayed = filter === 'ALL' ? orders : orders.filter((o) => o.orderStatus === filter)

  if (loading) return <Loader />

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">All Orders</h1>

      <div className="flex flex-wrap gap-2 mb-6">
        {statuses.map((s) => (
          <button
            key={s}
            onClick={() => setFilter(s)}
            className={`px-4 py-1.5 rounded-full text-sm font-medium transition ${
              filter === s ? 'bg-orange-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-orange-50'
            }`}
          >
            {s.replace(/_/g, ' ')}
          </button>
        ))}
      </div>

      <div className="bg-white rounded-2xl shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Order #', 'Customer', 'Restaurant', 'Items', 'Total', 'Status', 'Date'].map((h) => (
                <th key={h} className="px-4 py-3 text-left font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {displayed.map((o) => (
              <tr key={o.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-mono text-gray-900">#{o.orderNumber}</td>
                <td className="px-4 py-3 text-gray-600">{o.customerName || o.customerId?.substring(0, 8)}</td>
                <td className="px-4 py-3 text-gray-600">{o.restaurantName || '—'}</td>
                <td className="px-4 py-3 text-gray-400">{o.items?.length} item(s)</td>
                <td className="px-4 py-3 font-semibold">{formatCurrency(o.pricingBreakdown?.total)}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                    o.orderStatus === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                    o.orderStatus === 'CANCELLED' || o.orderStatus === 'REJECTED' ? 'bg-red-100 text-red-700' :
                    o.orderStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                    'bg-blue-100 text-blue-700'
                  }`}>
                    {o.orderStatus?.replace(/_/g, ' ')}
                  </span>
                </td>
                <td className="px-4 py-3 text-gray-400 text-xs">{formatDateTime(o.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {displayed.length === 0 && (
          <div className="text-center py-16 text-gray-400">No orders for this filter</div>
        )}
      </div>
    </div>
  )
}
