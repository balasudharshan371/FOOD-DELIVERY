import React from 'react'

export default function Loader({ fullScreen = false, message = 'Loading…' }) {
  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-white bg-opacity-80 z-50 flex flex-col items-center justify-center">
        <div className="w-16 h-16 border-4 border-orange-400 border-t-transparent rounded-full animate-spin mb-4" />
        <p className="text-gray-600 font-medium">{message}</p>
      </div>
    )
  }
  return (
    <div className="flex flex-col items-center justify-center py-16">
      <div className="w-12 h-12 border-4 border-orange-400 border-t-transparent rounded-full animate-spin mb-3" />
      <p className="text-gray-500">{message}</p>
    </div>
  )
}
