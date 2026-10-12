import React, { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { getPublicRestaurants } from '../api/restaurantApi'
import RestaurantCard from '../components/restaurants/RestaurantCard'
import Loader from '../components/common/Loader'

export default function RestaurantsPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [restaurants, setRestaurants] = useState([])
  const [loading, setLoading] = useState(true)
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(0)
  const PAGE_SIZE = 12

  const q = searchParams.get('q') || ''
  const cuisine = searchParams.get('cuisine') || ''
  const [search, setSearch] = useState(q)

  useEffect(() => {
    setLoading(true)
    getPublicRestaurants({ page, size: PAGE_SIZE, search: q, cuisine })
      .then((res) => {
        const d = res.data?.data || res.data
        setRestaurants(d?.content || [])
        setTotal(d?.totalElements || 0)
      })
      .catch(() => setRestaurants([]))
      .finally(() => setLoading(false))
  }, [q, cuisine, page])

  const handleSearch = (e) => {
    e.preventDefault()
    setSearchParams(search ? { q: search } : {})
    setPage(0)
  }

  const totalPages = Math.ceil(total / PAGE_SIZE)

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">All Restaurants</h1>

      {/* Search bar */}
      <form onSubmit={handleSearch} className="flex gap-3 mb-8 max-w-lg">
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search restaurants…"
          className="flex-1 border border-gray-300 rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-orange-400"
        />
        <button
          type="submit"
          className="bg-orange-500 text-white px-5 py-2.5 rounded-xl font-semibold hover:bg-orange-600 transition"
        >
          Search
        </button>
        {(q || cuisine) && (
          <button
            type="button"
            onClick={() => { setSearch(''); setSearchParams({}); setPage(0) }}
            className="text-gray-500 px-3 py-2.5 rounded-xl border border-gray-300 hover:bg-gray-50 transition"
          >
            Clear
          </button>
        )}
      </form>

      {cuisine && (
        <p className="mb-4 text-orange-600 font-medium">Showing results for: {cuisine} 🍽️</p>
      )}

      {loading ? (
        <Loader />
      ) : restaurants.length === 0 ? (
        <div className="text-center py-24 text-gray-400">
          <div className="text-6xl mb-4">🔍</div>
          <p className="text-xl">No restaurants found</p>
          <p className="text-sm mt-2">Try different search terms</p>
        </div>
      ) : (
        <>
          <p className="text-gray-500 text-sm mb-4">{total} restaurants found</p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {restaurants.map((r) => (
              <RestaurantCard key={r.id} restaurant={r} />
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-10">
              <button
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
                className="px-4 py-2 rounded-lg border border-gray-300 disabled:opacity-40 hover:bg-gray-50 transition"
              >
                ← Prev
              </button>
              <span className="px-4 py-2 text-gray-600">
                Page {page + 1} of {totalPages}
              </span>
              <button
                disabled={page >= totalPages - 1}
                onClick={() => setPage(page + 1)}
                className="px-4 py-2 rounded-lg border border-gray-300 disabled:opacity-40 hover:bg-gray-50 transition"
              >
                Next →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
