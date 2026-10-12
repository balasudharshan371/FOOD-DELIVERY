import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getOrderById } from '../../api/orderApi'
import Loader from '../../components/common/Loader'
import { formatCurrency, formatDateTime, orderStatusColor } from '../../utils/formatters'

export default function OrderConfirmPage() {
  const { id } = useParams()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getOrderById(id)
      .then((res) => setOrder(res.data?.data || res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader fullScreen message="Loading your order…" />

  return (
    <div className="max-w-2xl mx-auto px-4 py-12 text-center">
      <div className="text-7xl mb-4 animate-bounce">🎉</div>
      <h1 className="text-3xl font-bold text-gray-900 mb-2">Order Placed!</h1>
      <p className="text-gray-500 mb-8">
        Your order <span className="font-mono font-bold text-orange-600">#{order?.orderNumber}</span> has been received.
      </p>

      {order && (
        <div className="bg-white rounded-2xl shadow p-6 text-left mb-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="font-semibold text-gray-800">Order Summary</h2>
            <span className={`px-3 py-1 text-sm rounded-full font-medium bg-${orderStatusColor(order.orderStatus)}-100 text-${orderStatusColor(order.orderStatus)}-700`}>
              {order.orderStatus}
            </span>
          </div>
          <div className="space-y-2 text-sm text-gray-600 divide-y">
            {order.items?.map((item, i) => (
              <div key={i} className="flex justify-between py-2">
                <span>{item.name} × {item.quantity}</span>
                <span>{formatCurrency(item.price * item.quantity)}</span>
              </div>
            ))}
          </div>
          <div className="border-t pt-3 mt-3 space-y-1 text-sm">
            <div className="flex justify-between text-gray-600">
              <span>Delivery fee</span>
              <span>{formatCurrency(order.pricingBreakdown?.deliveryFee)}</span>
            </div>
            {order.pricingBreakdown?.discountAmount > 0 && (
              <div className="flex justify-between text-green-600">
                <span>Discount</span>
                <span>− {formatCurrency(order.pricingBreakdown.discountAmount)}</span>
              </div>
            )}
            <div className="flex justify-between font-bold text-gray-900 text-base pt-2 border-t">
              <span>Total Paid</span>
              <span>{formatCurrency(order.pricingBreakdown?.total)}</span>
            </div>
          </div>
          <div className="mt-4 text-xs text-gray-400">
            <p>Payment: {order.paymentMethod?.replace(/_/g, ' ')}</p>
            <p>Placed at: {formatDateTime(order.createdAt)}</p>
          </div>
        </div>
      )}

      <div className="flex gap-4 justify-center">
        <Link to={`/orders/${id}`} className="bg-orange-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-orange-600 transition">
          Track Order
        </Link>
        <Link to="/restaurants" className="bg-gray-100 text-gray-700 px-6 py-3 rounded-xl font-semibold hover:bg-gray-200 transition">
          Order More
        </Link>
      </div>
    </div>
  )
}
