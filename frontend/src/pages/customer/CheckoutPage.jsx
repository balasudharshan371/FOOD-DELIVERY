import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCart } from '../../context/CartContext'
import { useAuth } from '../../context/AuthContext'
import { useToast } from '../../context/ToastContext'
import { createOrder } from '../../api/orderApi'
import { validateCoupon } from '../../api/couponApi'
import { formatCurrency } from '../../utils/formatters'
import { PAYMENT_METHODS } from '../../utils/constants'

export default function CheckoutPage() {
  const { cart, clearCart } = useCart()
  const { user } = useAuth()
  const { showToast } = useToast()
  const navigate = useNavigate()

  const [address, setAddress] = useState({
    street: '', city: '', state: '', postalCode: '', country: 'India',
  })
  const [paymentMethod, setPaymentMethod] = useState('CASH_ON_DELIVERY')
  const [couponCode, setCouponCode] = useState('')
  const [couponResult, setCouponResult] = useState(null)
  const [couponLoading, setCouponLoading] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  const subtotal = cart?.items?.reduce((s, i) => s + (i.price || 0) * i.quantity, 0) || 0

  const handleCoupon = async () => {
    if (!couponCode.trim()) return
    setCouponLoading(true)
    try {
      const res = await validateCoupon({ code: couponCode, orderAmount: subtotal })
      const data = res.data?.data || res.data
      setCouponResult(data)
      showToast(`Coupon applied! You save ${formatCurrency(data.discountAmount)}`, 'success')
    } catch (err) {
      setCouponResult(null)
      showToast(err.response?.data?.message || 'Invalid coupon', 'error')
    } finally {
      setCouponLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!address.street || !address.city || !address.postalCode) {
      showToast('Please fill in the delivery address', 'error')
      return
    }
    if (!cart?.items?.length) { navigate('/cart'); return }
    setSubmitting(true)
    try {
      const res = await createOrder({
        deliveryAddress: address,
        couponCode: couponResult ? couponCode : undefined,
        paymentMethod,
      })
      const order = res.data?.data || res.data
      await clearCart()
      showToast('Order placed successfully! 🎉', 'success')
      navigate(`/order-confirm/${order.id}`)
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to place order', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  if (!cart?.items?.length) {
    navigate('/cart')
    return null
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">Checkout</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* Left form */}
        <form onSubmit={handleSubmit} className="md:col-span-2 space-y-6">
          {/* Address */}
          <div className="bg-white rounded-2xl shadow p-6">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">📍 Delivery Address</h2>
            <div className="grid grid-cols-1 gap-4">
              {[
                { name: 'street', label: 'Street Address', placeholder: '123 MG Road, Apt 4' },
                { name: 'city', label: 'City', placeholder: 'Hyderabad' },
                { name: 'state', label: 'State', placeholder: 'Telangana' },
                { name: 'postalCode', label: 'Postal Code', placeholder: '500001' },
              ].map(({ name, label, placeholder }) => (
                <div key={name}>
                  <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
                  <input
                    value={address[name]}
                    onChange={(e) => setAddress({ ...address, [name]: e.target.value })}
                    placeholder={placeholder}
                    className="w-full border border-gray-300 rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-orange-400"
                  />
                </div>
              ))}
            </div>
          </div>

          {/* Payment */}
          <div className="bg-white rounded-2xl shadow p-6">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">💳 Payment Method</h2>
            <div className="grid grid-cols-3 gap-3">
              {PAYMENT_METHODS.map((pm) => (
                <label
                  key={pm}
                  className={`border-2 rounded-xl p-3 cursor-pointer text-center text-sm font-medium transition ${
                    paymentMethod === pm
                      ? 'border-orange-500 bg-orange-50 text-orange-700'
                      : 'border-gray-200 text-gray-600 hover:border-orange-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="pm"
                    value={pm}
                    checked={paymentMethod === pm}
                    onChange={() => setPaymentMethod(pm)}
                    className="sr-only"
                  />
                  {pm === 'CASH_ON_DELIVERY' ? '💵 Cash' : pm === 'UPI' ? '📱 UPI' : '💳 Card'}
                </label>
              ))}
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full bg-orange-500 hover:bg-orange-600 text-white font-bold py-4 rounded-xl transition disabled:opacity-60 text-lg"
          >
            {submitting ? 'Placing Order…' : 'Place Order 🎉'}
          </button>
        </form>

        {/* Right summary */}
        <div className="space-y-4">
          <div className="bg-white rounded-2xl shadow p-5">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">Order Summary</h2>
            <div className="space-y-2 text-sm text-gray-600 divide-y">
              {cart.items.map((i) => (
                <div key={i.menuItemId} className="flex justify-between py-2">
                  <span>{i.name} × {i.quantity}</span>
                  <span>{formatCurrency((i.price || 0) * i.quantity)}</span>
                </div>
              ))}
            </div>
            <div className="border-t mt-3 pt-3 space-y-2 text-sm">
              <div className="flex justify-between text-gray-700">
                <span>Subtotal</span><span>{formatCurrency(subtotal)}</span>
              </div>
              {couponResult && (
                <div className="flex justify-between text-green-600">
                  <span>Discount ({couponCode})</span>
                  <span>− {formatCurrency(couponResult.discountAmount)}</span>
                </div>
              )}
              <div className="flex justify-between font-bold text-gray-900 text-base pt-2 border-t">
                <span>Total</span>
                <span>{formatCurrency(subtotal - (couponResult?.discountAmount || 0))}</span>
              </div>
            </div>
          </div>

          {/* Coupon */}
          <div className="bg-white rounded-2xl shadow p-5">
            <h2 className="text-sm font-semibold text-gray-800 mb-3">🎟️ Apply Coupon</h2>
            <div className="flex gap-2">
              <input
                value={couponCode}
                onChange={(e) => { setCouponCode(e.target.value.toUpperCase()); setCouponResult(null) }}
                placeholder="FOODIE50"
                className="flex-1 border border-gray-300 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
              />
              <button
                type="button"
                onClick={handleCoupon}
                disabled={couponLoading}
                className="bg-orange-500 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-orange-600 transition disabled:opacity-60"
              >
                {couponLoading ? '…' : 'Apply'}
              </button>
            </div>
            {couponResult && (
              <p className="text-green-600 text-xs mt-2">
                ✅ Coupon applied — you save {formatCurrency(couponResult.discountAmount)}
              </p>
            )}
            <p className="text-gray-400 text-xs mt-2">Try: FOODIE50, WELCOME20, TASTY100</p>
          </div>
        </div>
      </div>
    </div>
  )
}
