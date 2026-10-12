import React, { useEffect, useState } from 'react'
import { getOwnerOrders, updateOrderStatus } from '../../api/orderApi'
import Loader from '../../components/common/Loader'
import { formatCurrency, formatDateTime } from '../../utils/formatters'
import { useToast } from '../../context/ToastContext'

const NEXT_STATUS = {
  PENDING: ['CONFIRMED', 'REJECTED'],
  CONFIRMED: ['PREPARING'],
  PREPARING: ['OUT_FOR_DELIVERY'],
  OUT_FOR_DELIVERY: ['DELIVERED'],
}

export default function OwnerOrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const { showToast } = useToast()

  const load = () => {
    setLoading(true)
    getOwnerOrders()
      .then((res) => {
        const d = res.data?.data || res.data
        setOrders(Array.isArray(d) ? d : d?.content || [])
      })
      .catch(() => setOrders([]))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleStatus = async (orderId, status) => {
    try {
      await updateOrderStatus(orderId, status)
      showToast(`Order updated to ${status}`, 'success')
      load()
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to update', 'error')
    }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Incoming Orders</h1>
      {orders.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <p className="text-5xl mb-3">📭</p>
          <p>No orders yet</p>
        </div>
      ) : (
        <div className="space-y-5">
          {orders.map((order) => {
            const nexts = NEXT_STATUS[order.orderStatus] || []
            return (
              <div key={order.id} className="bg-white rounded-2xl shadow p-6">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <p className="font-bold text-gray-900 text-lg">#{order.orderNumber}</p>
                    <p className="text-gray-400 text-sm">{formatDateTime(order.createdAt)}</p>
                  </div>
                  <span className={`px-3 py-1 text-sm rounded-full font-semibold ${
                    order.orderStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                    order.orderStatus === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                    order.orderStatus === 'CANCELLED' || order.orderStatus === 'REJECTED' ? 'bg-red-100 text-red-700' :
                    'bg-blue-100 text-blue-700'
                  }`}>
                    {order.orderStatus?.replace(/_/g, ' ')}
                  </span>
                </div>
                <div className="text-sm text-gray-600 mb-3">
                  {order.items?.map((i) => `${i.name} ×${i.quantity}`).join(', ')}
                </div>
                <div className="flex items-center justify-between">
                  <span className="font-bold text-gray-900">{formatCurrency(order.pricingBreakdown?.total)}</span>
                  <div className="flex gap-2">
                    {nexts.map((s) => (
                      <button
                        key={s}
                        onClick={() => handleStatus(order.id, s)}
                        className={`px-4 py-1.5 rounded-xl text-sm font-semibold transition ${
                          s === 'REJECTED' || s === 'CANCELLED'
                            ? 'bg-red-100 text-red-600 hover:bg-red-200'
                            : 'bg-orange-500 text-white hover:bg-orange-600'
                        }`}
                      >
                        → {s.replace(/_/g, ' ')}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
