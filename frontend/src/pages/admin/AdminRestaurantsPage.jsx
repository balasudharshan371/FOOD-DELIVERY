import React, { useEffect, useState } from 'react'
import { getAllRestaurantsAdmin, toggleRestaurantStatus } from '../../api/restaurantApi'
import Loader from '../../components/common/Loader'
import { useToast } from '../../context/ToastContext'

export default function AdminRestaurantsPage() {
  const [restaurants, setRestaurants] = useState([])
  const [loading, setLoading] = useState(true)
  const { showToast } = useToast()

  const load = () => {
    setLoading(true)
    getAllRestaurantsAdmin()
      .then((res) => {
        const d = res.data?.data || res.data
        setRestaurants(Array.isArray(d) ? d : d?.content || [])
      })
      .catch(() => setRestaurants([]))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleToggle = async (id) => {
    try {
      await toggleRestaurantStatus(id)
      showToast('Status updated', 'success')
      load()
    } catch { showToast('Failed to toggle', 'error') }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Manage Restaurants</h1>
      <div className="bg-white rounded-2xl shadow overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Name', 'City', 'Cuisines', 'Rating', 'Status', 'Action'].map((h) => (
                <th key={h} className="px-5 py-3 text-left font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {restaurants.map((r) => (
              <tr key={r.id} className="hover:bg-gray-50">
                <td className="px-5 py-3 font-medium text-gray-900">{r.name}</td>
                <td className="px-5 py-3 text-gray-500">{r.city || r.address?.city}</td>
                <td className="px-5 py-3 text-gray-400 max-w-xs truncate">{r.cuisineTypes?.join(', ')}</td>
                <td className="px-5 py-3">⭐ {r.averageRating ? Number(r.averageRating).toFixed(1) : 'New'}</td>
                <td className="px-5 py-3">
                  <span className={`px-2 py-1 text-xs rounded-full font-medium ${r.isActive ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
                    {r.isActive ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="px-5 py-3">
                  <button
                    onClick={() => handleToggle(r.id)}
                    className={`text-xs font-medium px-3 py-1.5 rounded-lg transition ${
                      r.isActive ? 'bg-red-50 text-red-600 hover:bg-red-100' : 'bg-green-50 text-green-700 hover:bg-green-100'
                    }`}
                  >
                    {r.isActive ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {restaurants.length === 0 && (
          <div className="text-center py-16 text-gray-400">No restaurants found</div>
        )}
      </div>
    </div>
  )
}
