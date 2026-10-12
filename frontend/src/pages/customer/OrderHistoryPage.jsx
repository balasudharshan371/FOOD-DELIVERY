import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyOrders } from '../../api/orderApi'
import Loader from '../../components/common/Loader'
import { formatCurrency, formatDateTime } from '../../utils/formatters'

const STATUS_COLORS = {
  PENDING: 'yellow', CONFIRMED: 'blue', PREPARING: 'indigo',
  OUT_FOR_DELIVERY: 'purple', DELIVERED: 'green', CANCELLED: 'red', REJECTED: 'red',
}

export default function OrderHistoryPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getMyOrders()
      .then((res) => {
        const d = res.data?.data || res.data
        setOrders(Array.isArray(d) ? d : d?.content || [])
      })
      .catch(() => setOrders([]))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Loader />

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Your Orders</h1>
      {orders.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <div className="text-6xl mb-4">📦</div>
          <p className="text-xl">No orders yet</p>
          <Link to="/restaurants" className="text-orange-500 font-semibold mt-3 block">Start ordering →</Link>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => {
            const color = STATUS_COLORS[order.orderStatus] || 'gray'
            return (
              <Link
                key={order.id}
                to={`/orders/${order.id}`}
                className="block bg-white rounded-2xl shadow hover:shadow-md transition p-5"
              >
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <p className="font-bold text-gray-900">Order #{order.orderNumber}</p>
                    <p className="text-gray-500 text-sm">{formatDateTime(order.createdAt)}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold bg-${color}-100 text-${color}-700`}>
                    {order.orderStatus?.replace(/_/g, ' ')}
                  </span>
                </div>
                <div className="text-sm text-gray-600">
                  <p>{order.items?.map((i) => `${i.name} ×${i.quantity}`).join(', ')}</p>
                </div>
                <div className="flex justify-between mt-3 text-sm">
                  <span className="text-gray-500">{order.items?.length} item(s)</span>
                  <span className="font-bold text-gray-900">{formatCurrency(order.pricingBreakdown?.total)}</span>
                </div>
              </Link>
            )
          })}
        </div>
      )}
    </div>
  )
}
