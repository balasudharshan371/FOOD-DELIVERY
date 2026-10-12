import React from 'react'
import { Link } from 'react-router-dom'

export default function AccessDeniedPage() {
  return (
    <div className="min-h-[70vh] flex flex-col items-center justify-center text-center px-4">
      <div className="text-7xl mb-4">🚫</div>
      <h1 className="text-3xl font-bold text-gray-900 mb-2">Access Denied</h1>
      <p className="text-gray-500 mb-6 max-w-md">
        You do not have permission to view this resource. Please ensure you are logged in with an authorized account.
      </p>
      <div className="flex gap-4">
        <Link
          to="/"
          className="bg-orange-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-orange-600 transition"
        >
          Go to Home
        </Link>
        <Link
          to="/login"
          className="bg-gray-100 text-gray-700 px-6 py-3 rounded-xl font-semibold hover:bg-gray-200 transition"
        >
          Switch Account
        </Link>
      </div>
    </div>
  )
}
