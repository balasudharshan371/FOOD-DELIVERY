import React from 'react'
import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-300 mt-auto">
      <div className="max-w-7xl mx-auto px-4 py-10">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <h2 className="text-2xl font-bold text-orange-400 mb-3">🍔 FoodieHub</h2>
            <p className="text-sm text-gray-400">
              Your favorite food, delivered fast. Discover restaurants near you and enjoy the best meals.
            </p>
          </div>
          <div>
            <h3 className="font-semibold text-white mb-3">Company</h3>
            <ul className="space-y-2 text-sm">
              <li><Link to="/" className="hover:text-orange-400 transition-colors">Home</Link></li>
              <li><Link to="/restaurants" className="hover:text-orange-400 transition-colors">Restaurants</Link></li>
            </ul>
          </div>
          <div>
            <h3 className="font-semibold text-white mb-3">Account</h3>
            <ul className="space-y-2 text-sm">
              <li><Link to="/login" className="hover:text-orange-400 transition-colors">Login</Link></li>
              <li><Link to="/register" className="hover:text-orange-400 transition-colors">Register</Link></li>
              <li><Link to="/profile" className="hover:text-orange-400 transition-colors">Profile</Link></li>
              <li><Link to="/orders" className="hover:text-orange-400 transition-colors">Orders</Link></li>
            </ul>
          </div>
          <div>
            <h3 className="font-semibold text-white mb-3">Support</h3>
            <ul className="space-y-2 text-sm">
              <li><span className="text-gray-400">support@foodiehub.com</span></li>
              <li><span className="text-gray-400">+91 98765 43210</span></li>
            </ul>
          </div>
        </div>
        <div className="border-t border-gray-800 mt-8 pt-6 text-center text-sm text-gray-500">
          © {new Date().getFullYear()} FoodieHub. All rights reserved.
        </div>
      </div>
    </footer>
  )
}
