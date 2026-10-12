import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getPublicRestaurants } from '../api/restaurantApi'
import RestaurantCard from '../components/restaurants/RestaurantCard'
import Loader from '../components/common/Loader'

const HERO_CUISINES = ['Pizza', 'Biryani', 'Burgers', 'Sushi', 'Pasta', 'Tacos', 'Desserts', 'Salads']

export default function HomePage() {
  const [restaurants, setRestaurants] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  useEffect(() => {
    getPublicRestaurants({ page: 0, size: 8 })
      .then((res) => setRestaurants(res.data?.data?.content || res.data?.content || []))
      .catch(() => setRestaurants([]))
      .finally(() => setLoading(false))
  }, [])

  const filtered = restaurants.filter((r) =>
    r.name.toLowerCase().includes(search.toLowerCase()) ||
    r.cuisineTypes?.some((c) => c.toLowerCase().includes(search.toLowerCase()))
  )

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-orange-500 to-red-500 text-white py-20 px-4">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-5xl font-extrabold mb-4 drop-shadow">
            Hungry? We've got you 🍔
          </h1>
          <p className="text-xl text-orange-100 mb-8">
            Order food from the best restaurants around you
          </p>
          <div className="flex gap-3 max-w-lg mx-auto">
            <input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search restaurants or cuisines…"
              className="flex-1 px-5 py-3 rounded-xl text-gray-900 focus:outline-none shadow-lg text-base"
            />
            <Link
              to={`/restaurants${search ? `?q=${encodeURIComponent(search)}` : ''}`}
              className="bg-white text-orange-600 font-bold px-6 py-3 rounded-xl shadow hover:bg-orange-50 transition"
            >
              Search
            </Link>
          </div>
        </div>
      </section>

      {/* Cuisine quick-picks */}
      <section className="max-w-6xl mx-auto px-4 py-10">
        <h2 className="text-2xl font-bold text-gray-800 mb-5">What are you craving?</h2>
        <div className="flex gap-3 flex-wrap">
          {HERO_CUISINES.map((c) => (
            <Link
              key={c}
              to={`/restaurants?cuisine=${encodeURIComponent(c)}`}
              className="bg-orange-50 border border-orange-200 text-orange-700 px-5 py-2 rounded-full text-sm font-medium hover:bg-orange-100 transition"
            >
              {c}
            </Link>
          ))}
        </div>
      </section>

      {/* Restaurant list */}
      <section className="max-w-6xl mx-auto px-4 pb-16">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Featured Restaurants</h2>
          <Link to="/restaurants" className="text-orange-500 font-semibold hover:underline text-sm">
            View all →
          </Link>
        </div>

        {loading ? (
          <Loader />
        ) : filtered.length === 0 ? (
          <div className="text-center py-20 text-gray-400">
            <div className="text-6xl mb-4">🍽️</div>
            <p className="text-xl">No restaurants found</p>
            <p className="text-sm mt-2">Try a different search</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filtered.map((r) => (
              <RestaurantCard key={r.id} restaurant={r} />
            ))}
          </div>
        )}
      </section>
    </div>
  )
}
