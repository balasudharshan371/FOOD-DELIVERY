import React, { useEffect, useState } from 'react'
import { getMyRestaurant, updateMyRestaurant } from '../../api/restaurantApi'
import Loader from '../../components/common/Loader'
import { useToast } from '../../context/ToastContext'

export default function OwnerRestaurantPage() {
  const { showToast } = useToast()
  const [form, setForm] = useState({
    name: '', description: '', cuisineTypes: '', city: '',
    deliveryFee: '', estimatedDeliveryMinutes: '', imageUrl: '', isActive: true,
  })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    getMyRestaurant()
      .then((res) => {
        const r = res.data?.data || res.data
        setForm({
          name: r.name || '',
          description: r.description || '',
          cuisineTypes: r.cuisineTypes?.join(', ') || '',
          city: r.address?.city || '',
          deliveryFee: r.deliveryFee || 0,
          estimatedDeliveryMinutes: r.estimatedDeliveryMinutes || 30,
          imageUrl: r.imageUrl || '',
          isActive: r.isActive ?? true,
        })
      })
      .catch(() => showToast('Could not load restaurant', 'error'))
      .finally(() => setLoading(false))
  }, [])

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await updateMyRestaurant({
        ...form,
        cuisineTypes: form.cuisineTypes.split(',').map((s) => s.trim()).filter(Boolean),
        deliveryFee: Number(form.deliveryFee),
        estimatedDeliveryMinutes: Number(form.estimatedDeliveryMinutes),
      })
      showToast('Restaurant updated ✅', 'success')
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to update', 'error')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">Restaurant Profile</h1>
      <div className="bg-white rounded-2xl shadow p-6">
        <form onSubmit={handleSave} className="space-y-5">
          {[
            { name: 'name', label: 'Restaurant Name' },
            { name: 'description', label: 'Description' },
            { name: 'cuisineTypes', label: 'Cuisines (comma-separated)', placeholder: 'North Indian, Chinese' },
            { name: 'city', label: 'City' },
            { name: 'imageUrl', label: 'Image URL' },
            { name: 'deliveryFee', label: 'Delivery Fee (₹)', type: 'number' },
            { name: 'estimatedDeliveryMinutes', label: 'Estimated Delivery (min)', type: 'number' },
          ].map(({ name, label, type = 'text', placeholder }) => (
            <div key={name}>
              <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
              <input
                type={type}
                value={form[name]}
                onChange={(e) => setForm({ ...form, [name]: e.target.value })}
                placeholder={placeholder}
                className="w-full border border-gray-300 rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-orange-400"
              />
            </div>
          ))}
          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              checked={form.isActive}
              onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
              className="accent-orange-500 w-4 h-4"
            />
            <span className="text-sm font-medium text-gray-700">Restaurant is Open</span>
          </label>
          <button
            type="submit"
            disabled={saving}
            className="w-full bg-orange-500 text-white font-semibold py-3 rounded-xl hover:bg-orange-600 transition disabled:opacity-60"
          >
            {saving ? 'Saving…' : 'Save Changes'}
          </button>
        </form>
      </div>
    </div>
  )
}
