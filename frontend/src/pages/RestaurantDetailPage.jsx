import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getRestaurantById, getMenuByRestaurant } from '../api/restaurantApi'
import { toggleFavorite, getFavorites } from '../api/restaurantApi'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import Loader from '../components/common/Loader'
import { formatCurrency } from '../utils/formatters'

export default function RestaurantDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const { addToCart, cart } = useCart()
  const { showToast } = useToast()

  const [restaurant, setRestaurant] = useState(null)
  const [menuItems, setMenuItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [isFav, setIsFav] = useState(false)
  const [activeCategory, setActiveCategory] = useState('All')
  const [vegOnly, setVegOnly] = useState(false)

  useEffect(() => {
    Promise.all([
      getRestaurantById(id),
      getMenuByRestaurant(id),
    ]).then(([rRes, mRes]) => {
      setRestaurant(rRes.data?.data || rRes.data)
      setMenuItems(mRes.data?.data || mRes.data || [])
    }).catch(() => navigate('/restaurants'))
      .finally(() => setLoading(false))
  }, [id])

  useEffect(() => {
    if (user) {
      getFavorites().then((res) => {
        const favs = res.data?.data || res.data || []
        setIsFav(favs.some((f) => f.restaurantId === id || f.restaurant?.id === id))
      }).catch(() => {})
    }
  }, [user, id])

  const handleFavorite = async () => {
    if (!user) { showToast('Login to save favorites', 'error'); return }
    try {
      await toggleFavorite(id)
      setIsFav(!isFav)
      showToast(isFav ? 'Removed from favorites' : 'Added to favorites ❤️', 'success')
    } catch { showToast('Could not update favorites', 'error') }
  }

  const handleAddToCart = async (item) => {
    if (!user) { showToast('Please login to order', 'error'); navigate('/login'); return }
    try {
      await addToCart(item.id, 1, false)
      showToast(`${item.name} added to cart 🛒`, 'success')
    } catch (err) {
      const msg = err.message || (typeof err === 'string' ? err : 'Could not add to cart')
      if (msg.toLowerCase().includes('already contains')) {
        if (window.confirm('Your cart has items from another restaurant. Clear cart and add this item?')) {
          try {
            await addToCart(item.id, 1, true)
            showToast(`${item.name} added to cart 🛒`, 'success')
          } catch (e) {
            showToast(e.message || 'Could not replace cart items', 'error')
          }
        }
      } else {
        showToast(msg, 'error')
      }
    }
  }

  if (loading) return <Loader fullScreen />
  if (!restaurant) return null

  const categories = ['All', ...new Set(menuItems.map((m) => m.categoryName).filter(Boolean))]
  const displayed = menuItems.filter((m) => {
    const catMatch = activeCategory === 'All' || m.categoryName === activeCategory
    const vegMatch = !vegOnly || m.foodType === 'VEG' || m.foodType === 'VEGAN'
    return catMatch && vegMatch
  })

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="bg-white rounded-2xl shadow overflow-hidden mb-8">
        <div className="h-56 bg-orange-100 relative">
          {restaurant.imageUrl ? (
            <img src={restaurant.imageUrl} alt={restaurant.name} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-7xl">🍽️</div>
          )}
        </div>
        <div className="p-6">
          <div className="flex items-start justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">{restaurant.name}</h1>
              <p className="text-gray-500 mt-1">{restaurant.description}</p>
              <p className="text-gray-400 text-sm mt-1">{restaurant.cuisineTypes?.join(', ')}</p>
              <p className="text-gray-400 text-sm">{restaurant.address?.street}, {restaurant.address?.city}</p>
            </div>
            <button
              onClick={handleFavorite}
              className={`text-2xl transition ${isFav ? 'text-red-500' : 'text-gray-300 hover:text-red-400'}`}
            >
              {isFav ? '❤️' : '🤍'}
            </button>
          </div>
          <div className="flex gap-6 mt-4 text-sm text-gray-600">
            <span>⭐ {restaurant.averageRating ? Number(restaurant.averageRating).toFixed(1) : 'New'}</span>
            <span>🕐 {restaurant.estimatedDeliveryMinutes ?? 30} min</span>
            <span>🚚 {restaurant.deliveryFee > 0 ? formatCurrency(restaurant.deliveryFee) : 'Free delivery'}</span>
            <span className={restaurant.isActive ? 'text-green-600' : 'text-red-500'}>
              {restaurant.isActive ? '● Open' : '● Closed'}
            </span>
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 mb-6 items-center">
        <div className="flex gap-2 flex-wrap">
          {categories.map((c) => (
            <button
              key={c}
              onClick={() => setActiveCategory(c)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium transition ${
                activeCategory === c
                  ? 'bg-orange-500 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-orange-50'
              }`}
            >
              {c}
            </button>
          ))}
        </div>
        <label className="flex items-center gap-2 ml-auto text-sm text-gray-600 cursor-pointer">
          <input
            type="checkbox"
            checked={vegOnly}
            onChange={(e) => setVegOnly(e.target.checked)}
            className="accent-green-500"
          />
          Veg Only 🥗
        </label>
      </div>

      {/* Menu items */}
      {displayed.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <p className="text-5xl mb-3">🥲</p>
          <p>No items match your filter</p>
        </div>
      ) : (
        <div className="space-y-4">
          {displayed.map((item) => {
            const inCart = cart?.items?.find((ci) => ci.menuItemId === item.id)
            return (
              <div key={item.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 flex gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <span className={`text-xs font-bold px-2 py-0.5 rounded border ${
                      item.foodType === 'VEG' || item.foodType === 'VEGAN'
                        ? 'text-green-600 border-green-400'
                        : 'text-red-600 border-red-400'
                    }`}>
                      {item.foodType === 'VEG' || item.foodType === 'VEGAN' ? '●' : '●'}
                    </span>
                    <h3 className="font-semibold text-gray-900">{item.name}</h3>
                  </div>
                  <p className="text-gray-500 text-sm mt-1">{item.description}</p>
                  <div className="flex items-center gap-4 mt-3">
                    <span className="text-orange-600 font-bold text-lg">{formatCurrency(item.price)}</span>
                    <span className="text-gray-400 text-xs">⏱ {item.preparationTime ?? 15} min</span>
                  </div>
                </div>
                <div className="flex flex-col items-end justify-between">
                  {item.imageUrl && (
                    <img src={item.imageUrl} alt={item.name} className="w-20 h-20 rounded-xl object-cover mb-3" />
                  )}
                  {item.isAvailable && restaurant.isActive ? (
                    <button
                      onClick={() => handleAddToCart(item)}
                      className={`px-4 py-2 rounded-xl text-sm font-semibold transition ${
                        inCart
                          ? 'bg-orange-100 text-orange-600 border border-orange-400'
                          : 'bg-orange-500 text-white hover:bg-orange-600'
                      }`}
                    >
                      {inCart ? `In Cart (${inCart.quantity})` : '+ Add'}
                    </button>
                  ) : (
                    <span className="text-gray-400 text-sm">Unavailable</span>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}

      {/* Sticky cart button */}
      {cart?.items?.length > 0 && cart.restaurantId === id && (
        <div className="fixed bottom-6 left-0 right-0 flex justify-center z-40">
          <button
            onClick={() => navigate('/cart')}
            className="bg-orange-500 text-white px-8 py-4 rounded-2xl shadow-2xl font-bold text-lg hover:bg-orange-600 transition"
          >
            🛒 View Cart ({cart.items.length} items)
          </button>
        </div>
      )}
    </div>
  )
}
