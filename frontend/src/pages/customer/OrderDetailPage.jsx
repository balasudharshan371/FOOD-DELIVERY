import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getOrderById, cancelOrder, addReview } from '../../api/orderApi'
import Loader from '../../components/common/Loader'
import { formatCurrency, formatDateTime } from '../../utils/formatters'
import { useToast } from '../../context/ToastContext'

const STATUS_STEPS = ['PENDING', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED']

export default function OrderDetailPage() {
  const { id } = useParams()
  const { showToast } = useToast()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [cancelling, setCancelling] = useState(false)
  const [review, setReview] = useState({ rating: 5, comment: '' })
  const [reviewSubmitted, setReviewSubmitted] = useState(false)

  const load = () => {
    setLoading(true)
    getOrderById(id)
      .then((res) => setOrder(res.data?.data || res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [id])

  const handleCancel = async () => {
    const reason = window.prompt('Reason for cancellation (optional):') ?? ''
    if (reason === null) return
    setCancelling(true)
    try {
      await cancelOrder(id, reason)
      showToast('Order cancelled', 'info')
      load()
    } catch (err) {
      showToast(err.response?.data?.message || 'Could not cancel order', 'error')
    } finally {
      setCancelling(false)
    }
  }

  const handleReview = async (e) => {
    e.preventDefault()
    try {
      await addReview({ orderId: id, restaurantId: order.restaurantId, ...review })
      setReviewSubmitted(true)
      showToast('Review submitted! Thank you 🙏', 'success')
    } catch (err) {
      showToast(err.response?.data?.message || 'Could not submit review', 'error')
    }
  }

  if (loading) return <Loader />
  if (!order) return <div className="text-center py-20 text-gray-400">Order not found</div>

  const stepIdx = STATUS_STEPS.indexOf(order.orderStatus)
  const isCancellable = ['PENDING', 'CONFIRMED'].includes(order.orderStatus)
  const isDelivered = order.orderStatus === 'DELIVERED'

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-2">Order #{order.orderNumber}</h1>
      <p className="text-gray-500 text-sm mb-6">{formatDateTime(order.createdAt)}</p>

      {/* Tracker */}
      {!['CANCELLED', 'REJECTED'].includes(order.orderStatus) && (
        <div className="bg-white rounded-2xl shadow p-6 mb-6">
          <h2 className="font-semibold text-gray-800 mb-4">Order Progress</h2>
          <div className="relative">
            <div className="absolute top-4 left-0 right-0 h-1 bg-gray-200 rounded-full" />
            <div
              className="absolute top-4 left-0 h-1 bg-orange-400 rounded-full transition-all duration-700"
              style={{ width: `${stepIdx >= 0 ? (stepIdx / (STATUS_STEPS.length - 1)) * 100 : 0}%` }}
            />
            <div className="relative flex justify-between">
              {STATUS_STEPS.map((s, i) => (
                <div key={s} className="flex flex-col items-center">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold z-10 ${
                    i <= stepIdx ? 'bg-orange-500 text-white' : 'bg-gray-200 text-gray-400'
                  }`}>
                    {i <= stepIdx ? '✓' : i + 1}
                  </div>
                  <p className="text-xs text-gray-500 mt-2 text-center max-w-14">{s.replace(/_/g, ' ')}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Items */}
      <div className="bg-white rounded-2xl shadow p-6 mb-5">
        <h2 className="font-semibold text-gray-800 mb-3">Items</h2>
        <div className="space-y-2 divide-y text-sm text-gray-600">
          {order.items?.map((item, i) => (
            <div key={i} className="flex justify-between py-2">
              <span>{item.name} × {item.quantity}</span>
              <span>{formatCurrency(item.price * item.quantity)}</span>
            </div>
          ))}
        </div>
        <div className="border-t pt-3 mt-3 space-y-1 text-sm">
          <div className="flex justify-between text-gray-600">
            <span>Subtotal</span><span>{formatCurrency(order.pricingBreakdown?.subtotal)}</span>
          </div>
          <div className="flex justify-between text-gray-600">
            <span>Delivery fee</span><span>{formatCurrency(order.pricingBreakdown?.deliveryFee)}</span>
          </div>
          {order.pricingBreakdown?.discountAmount > 0 && (
            <div className="flex justify-between text-green-600">
              <span>Discount</span><span>− {formatCurrency(order.pricingBreakdown.discountAmount)}</span>
            </div>
          )}
          <div className="flex justify-between font-bold text-base pt-2 border-t">
            <span>Total</span><span>{formatCurrency(order.pricingBreakdown?.total)}</span>
          </div>
        </div>
      </div>

      {/* Delivery address */}
      {order.deliveryAddressSnapshot && (
        <div className="bg-white rounded-2xl shadow p-5 mb-5 text-sm text-gray-600">
          <h2 className="font-semibold text-gray-800 mb-2">Delivery Address</h2>
          <p>{order.deliveryAddressSnapshot.street}, {order.deliveryAddressSnapshot.city}</p>
          <p>{order.deliveryAddressSnapshot.state} {order.deliveryAddressSnapshot.postalCode}</p>
        </div>
      )}

      {/* Cancel button */}
      {isCancellable && (
        <button
          onClick={handleCancel}
          disabled={cancelling}
          className="w-full bg-red-50 border border-red-300 text-red-600 py-3 rounded-xl font-semibold hover:bg-red-100 transition mb-5"
        >
          {cancelling ? 'Cancelling…' : 'Cancel Order'}
        </button>
      )}

      {/* Review form */}
      {isDelivered && !reviewSubmitted && (
        <div className="bg-white rounded-2xl shadow p-6">
          <h2 className="font-semibold text-gray-800 mb-4">Rate your experience ⭐</h2>
          <form onSubmit={handleReview} className="space-y-4">
            <div className="flex gap-2">
              {[1, 2, 3, 4, 5].map((s) => (
                <button
                  key={s} type="button"
                  onClick={() => setReview({ ...review, rating: s })}
                  className={`text-2xl transition ${s <= review.rating ? 'text-yellow-400' : 'text-gray-300'}`}
                >
                  ★
                </button>
              ))}
            </div>
            <textarea
              value={review.comment}
              onChange={(e) => setReview({ ...review, comment: e.target.value })}
              placeholder="Share your experience…"
              rows={3}
              className="w-full border border-gray-300 rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-orange-400 text-sm"
            />
            <button type="submit" className="bg-orange-500 text-white px-6 py-2.5 rounded-xl font-semibold hover:bg-orange-600 transition">
              Submit Review
            </button>
          </form>
        </div>
      )}

      {isDelivered && reviewSubmitted && (
        <div className="bg-green-50 text-green-700 rounded-2xl p-5 text-center">
          ✅ Thank you for your review!
        </div>
      )}
    </div>
  )
}
