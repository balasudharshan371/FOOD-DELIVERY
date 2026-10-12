import React, { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'

export default function LoginPage() {
  const { login } = useAuth()
  const { showToast } = useToast()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from?.pathname || '/'

  const [form, setForm] = useState({ email: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.email || !form.password) {
      setError('Please fill in all fields.')
      return
    }
    setLoading(true)
    try {
      const user = await login(form.email, form.password)
      showToast('Welcome back! 🎉', 'success')
      // Redirect based on role
      if (user.roles?.includes('ROLE_ADMIN')) navigate('/admin')
      else if (user.roles?.includes('ROLE_OWNER')) navigate('/owner')
      else navigate(from, { replace: true })
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-gray-900">Welcome back 👋</h1>
            <p className="text-gray-500 mt-2">Sign in to continue to FoodieHub</p>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-5 text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="you@example.com"
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-orange-400 transition"
                autoComplete="email"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="Your password"
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-orange-400 transition"
                autoComplete="current-password"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold py-3 rounded-xl transition disabled:opacity-60"
            >
              {loading ? 'Signing in…' : 'Sign In'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-gray-500 text-sm">
              Don't have an account?{' '}
              <Link to="/register" className="text-orange-500 font-semibold hover:underline">
                Register
              </Link>
            </p>
          </div>

          <div className="mt-6 border-t pt-5">
            <p className="text-xs text-gray-400 text-center mb-3">Demo credentials</p>
            <div className="grid grid-cols-3 gap-2 text-xs">
              {[
                { label: 'Customer', email: 'bala@example.com' },
                { label: 'Owner', email: 'rajesh@foodiehub.com' },
                { label: 'Admin', email: 'admin@foodiehub.com' },
              ].map(({ label, email }) => (
                <button
                  key={label}
                  type="button"
                  onClick={() => setForm({ email, password: 'Password@123' })}
                  className="bg-gray-100 hover:bg-orange-50 border border-gray-200 rounded-lg p-2 text-gray-600 transition"
                >
                  <div className="font-semibold">{label}</div>
                  <div className="text-gray-400 truncate">{email.split('@')[0]}</div>
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
