import React from 'react'
import { Link } from 'react-router-dom'
import { truncate, formatCurrency } from '../../utils/formatters'

export default function RestaurantCard({ restaurant }) {
  const {
    id,
    name,
    description,
    cuisineTypes = [],
    imageUrl,
    averageRating,
    deliveryFee,
    estimatedDeliveryMinutes,
    isActive,
  } = restaurant

  return (
    <Link to={`/restaurants/${id}`} className="block group">
      <div className="bg-white rounded-2xl shadow hover:shadow-lg transition-shadow overflow-hidden border border-gray-100">
        <div className="relative h-48 bg-gray-200 overflow-hidden">
          {imageUrl ? (
            <img
              src={imageUrl}
              alt={name}
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-5xl bg-orange-50">
              🍽️
            </div>
          )}
          {!isActive && (
            <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
              <span className="text-white font-semibold text-lg">Currently Closed</span>
            </div>
          )}
          <div className="absolute top-3 right-3 bg-white text-gray-800 text-sm font-bold px-2 py-1 rounded-lg shadow flex items-center gap-1">
            ⭐ {averageRating ? Number(averageRating).toFixed(1) : 'New'}
          </div>
        </div>
        <div className="p-4">
          <h3 className="font-semibold text-gray-900 text-lg leading-tight">{name}</h3>
          <p className="text-gray-500 text-sm mt-1">{truncate(description, 60)}</p>
          <p className="text-gray-400 text-xs mt-1">{cuisineTypes.join(', ')}</p>
          <div className="flex items-center justify-between mt-3 text-sm text-gray-600">
            <span>🕐 {estimatedDeliveryMinutes ?? 30} min</span>
            <span>
              {deliveryFee > 0 ? `Delivery: ${formatCurrency(deliveryFee)}` : 'Free Delivery'}
            </span>
          </div>
        </div>
      </div>
    </Link>
  )
}
