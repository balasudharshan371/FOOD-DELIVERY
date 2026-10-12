import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useCart } from '../../context/CartContext'
import { useToast } from '../../context/ToastContext'
import { formatCurrency } from '../../utils/formatters'

export default function CartPage() {
  const { cart, updateItem, removeItem, clearCart, loading } = useCart()
  const { showToast } = useToast()
  const navigate = useNavigate()

  if (!cart || cart.items?.length === 0) {
    return (
      <div className="max-w-lg mx-auto px-4 py-24 text-center">
        <div className="text-7xl mb-4">🛒</div>
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Your cart is empty</h2>
        <p className="text-gray-500 mb-6">Add items from a restaurant to get started</p>
        <Link to="/restaurants" className="bg-orange-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-orange-600 transition">
          Browse Restaurants
        </Link>
      </div>
    )
  }

  const subtotal = cart.items.reduce((sum, i) => sum + (i.price || 0) * i.quantity, 0)

  const handleUpdate = async (itemId, qty) => {
    if (qty < 1) {
      await removeItem(itemId)
      showToast('Item removed', 'info')
    } else {
      await updateItem(itemId, qty)
    }
  }

  const handleClear = async () => {
    if (window.confirm('Clear entire cart?')) {
      await clearCart()
      showToast('Cart cleared', 'info')
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Your Cart</h1>
        <button onClick={handleClear} className="text-red-500 text-sm hover:underline">
          Clear cart
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow divide-y">
        {cart.items.map((item) => (
          <div key={item.menuItemId} className="flex items-center gap-4 p-5">
            <div className="flex-1">
              <p className="font-semibold text-gray-900">{item.name}</p>
              <p className="text-orange-600 font-bold mt-1">{formatCurrency(item.price)}</p>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={() => handleUpdate(item.menuItemId, item.quantity - 1)}
                disabled={loading}
                className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center font-bold text-gray-700 hover:bg-orange-100 transition"
              >
                −
              </button>
              <span className="w-6 text-center font-semibold">{item.quantity}</span>
              <button
                onClick={() => handleUpdate(item.menuItemId, item.quantity + 1)}
                disabled={loading}
                className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center font-bold text-gray-700 hover:bg-orange-100 transition"
              >
                +
              </button>
            </div>
            <div className="text-right w-20">
              <p className="font-bold">{formatCurrency((item.price || 0) * item.quantity)}</p>
              <button
                onClick={() => handleUpdate(item.menuItemId, 0)}
                className="text-red-400 text-xs hover:text-red-600 mt-1"
              >
                Remove
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Summary */}
      <div className="bg-white rounded-2xl shadow mt-5 p-6">
        <div className="flex justify-between text-gray-600 mb-3">
          <span>Subtotal</span>
          <span>{formatCurrency(subtotal)}</span>
        </div>
        <p className="text-xs text-gray-400 mb-4">
          Delivery fee and taxes calculated at checkout
        </p>
        <button
          onClick={() => navigate('/checkout')}
          className="w-full bg-orange-500 text-white font-bold py-3 rounded-xl hover:bg-orange-600 transition text-lg"
        >
          Proceed to Checkout →
        </button>
      </div>
    </div>
  )
}
