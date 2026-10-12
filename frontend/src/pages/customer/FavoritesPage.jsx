import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getFavorites, removeFavorite } from '../../api/restaurantApi'
import Loader from '../../components/common/Loader'
import { useToast } from '../../context/ToastContext'

export default function FavoritesPage() {
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)
  const { showToast } = useToast()

  const load = () => {
    setLoading(true)
    getFavorites()
      .then((res) => setFavorites(res.data?.data || res.data || []))
      .catch(() => setFavorites([]))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleRemove = async (restaurantId) => {
    try {
      await removeFavorite(restaurantId)
      setFavorites((prev) => prev.filter((f) => (f.restaurantId || f.restaurant?.id) !== restaurantId))
      showToast('Removed from favorites', 'info')
    } catch {
      showToast('Could not remove favorite', 'error')
    }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">❤️ My Favorites</h1>
      {favorites.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <div className="text-6xl mb-4">🤍</div>
          <p className="text-xl">No favorites yet</p>
          <Link to="/restaurants" className="text-orange-500 font-semibold mt-3 block">
            Browse restaurants →
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          {favorites.map((fav) => {
            const r = fav.restaurant || fav
            const rId = fav.restaurantId || r.id
            return (
              <div key={fav.id} className="bg-white rounded-2xl shadow hover:shadow-md transition p-5 flex gap-4">
                <div className="w-16 h-16 bg-orange-100 rounded-xl flex items-center justify-center text-3xl flex-shrink-0">
                  🍽️
                </div>
                <div className="flex-1">
                  <Link to={`/restaurants/${rId}`} className="font-semibold text-gray-900 hover:text-orange-600">
                    {r.name || 'Restaurant'}
                  </Link>
                  <p className="text-gray-400 text-xs mt-0.5">{r.cuisineTypes?.join(', ')}</p>
                </div>
                <button
                  onClick={() => handleRemove(rId)}
                  className="text-red-400 hover:text-red-600 self-start"
                >
                  ✕
                </button>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
