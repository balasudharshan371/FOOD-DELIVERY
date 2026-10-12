import React, { useEffect, useState } from 'react'
import { getMenuItems, createMenuItem, updateMenuItem, deleteMenuItem, toggleItemAvailability } from '../../api/restaurantApi'
import { getMyRestaurant } from '../../api/restaurantApi'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import { useToast } from '../../context/ToastContext'
import { formatCurrency } from '../../utils/formatters'
import axiosClient from '../../api/axiosClient'

const EMPTY_ITEM = {
  name: '', description: '', price: '', categoryId: '', foodType: 'VEG',
  preparationTime: 15, imageUrl: '', isAvailable: true,
}

export default function OwnerMenuPage() {
  const { showToast } = useToast()
  const [restaurant, setRestaurant] = useState(null)
  const [items, setItems] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY_ITEM)
  const [saving, setSaving] = useState(false)

  const load = async () => {
    setLoading(true)
    try {
      const rRes = await getMyRestaurant()
      const r = rRes.data?.data || rRes.data
      setRestaurant(r)
      const [mRes, cRes] = await Promise.all([
        getMenuItems(r.id),
        axiosClient.get('/categories'),
      ])
      setItems(mRes.data?.data || mRes.data || [])
      const cats = cRes.data?.data || cRes.data
      setCategories(Array.isArray(cats) ? cats : cats?.content || [])
    } catch {}
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  const openCreate = () => { setForm({ ...EMPTY_ITEM, restaurantId: restaurant?.id }); setEditing(null); setModalOpen(true) }
  const openEdit = (item) => { setForm({ ...item }); setEditing(item.id); setModalOpen(true) }

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      if (editing) {
        await updateMenuItem(editing, { ...form, restaurantId: restaurant?.id })
        showToast('Item updated ✅', 'success')
      } else {
        await createMenuItem({ ...form, restaurantId: restaurant?.id })
        showToast('Item created ✅', 'success')
      }
      setModalOpen(false)
      load()
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to save item', 'error')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this menu item?')) return
    try {
      await deleteMenuItem(id)
      showToast('Item deleted', 'info')
      load()
    } catch { showToast('Failed to delete', 'error') }
  }

  const handleToggle = async (id) => {
    try {
      await toggleItemAvailability(id)
      load()
    } catch { showToast('Failed to toggle', 'error') }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Menu Management</h1>
        <button
          onClick={openCreate}
          className="bg-orange-500 text-white px-5 py-2.5 rounded-xl font-semibold hover:bg-orange-600 transition"
        >
          + Add Item
        </button>
      </div>

      {items.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <p className="text-5xl mb-3">🍽️</p>
          <p>No menu items yet. Add your first item!</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {items.map((item) => (
            <div key={item.id} className={`bg-white rounded-2xl shadow p-5 flex gap-4 border-2 ${item.isAvailable ? 'border-transparent' : 'border-gray-200 opacity-70'}`}>
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <span className={`w-3 h-3 rounded-full ${item.foodType === 'VEG' || item.foodType === 'VEGAN' ? 'bg-green-500' : 'bg-red-500'}`} />
                  <p className="font-semibold text-gray-900">{item.name}</p>
                </div>
                <p className="text-gray-500 text-sm">{item.description}</p>
                <p className="text-orange-600 font-bold mt-2">{formatCurrency(item.price)}</p>
              </div>
              <div className="flex flex-col gap-2 items-end">
                <button
                  onClick={() => handleToggle(item.id)}
                  className={`text-xs px-3 py-1 rounded-full font-medium ${item.isAvailable ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}
                >
                  {item.isAvailable ? 'Available' : 'Unavailable'}
                </button>
                <button onClick={() => openEdit(item)} className="text-xs text-blue-600 hover:underline">Edit</button>
                <button onClick={() => handleDelete(item.id)} className="text-xs text-red-500 hover:underline">Delete</button>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Edit Menu Item' : 'Add Menu Item'}
        footer={
          <>
            <button onClick={() => setModalOpen(false)} className="px-5 py-2 rounded-xl border text-gray-600 hover:bg-gray-50">Cancel</button>
            <button onClick={handleSave} disabled={saving} className="bg-orange-500 text-white px-5 py-2 rounded-xl font-semibold hover:bg-orange-600 disabled:opacity-60">
              {saving ? 'Saving…' : 'Save Item'}
            </button>
          </>
        }
      >
        <form onSubmit={handleSave} className="space-y-4">
          {[
            { name: 'name', label: 'Name', placeholder: 'Chicken Biryani' },
            { name: 'description', label: 'Description', placeholder: 'Aromatic rice dish…' },
            { name: 'price', label: 'Price (₹)', placeholder: '250', type: 'number' },
            { name: 'preparationTime', label: 'Prep Time (min)', placeholder: '15', type: 'number' },
            { name: 'imageUrl', label: 'Image URL (optional)', placeholder: 'https://…' },
          ].map(({ name, label, placeholder, type = 'text' }) => (
            <div key={name}>
              <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
              <input
                type={type}
                value={form[name] || ''}
                onChange={(e) => setForm({ ...form, [name]: e.target.value })}
                placeholder={placeholder}
                className="w-full border border-gray-300 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
              />
            </div>
          ))}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select
              value={form.categoryId || ''}
              onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
              className="w-full border border-gray-300 rounded-xl px-3 py-2.5 text-sm"
            >
              <option value="">Select category</option>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Food Type</label>
            <select
              value={form.foodType}
              onChange={(e) => setForm({ ...form, foodType: e.target.value })}
              className="w-full border border-gray-300 rounded-xl px-3 py-2.5 text-sm"
            >
              <option value="VEG">VEG</option>
              <option value="NON_VEG">NON_VEG</option>
              <option value="VEGAN">VEGAN</option>
            </select>
          </div>
          <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
            <input
              type="checkbox"
              checked={form.isAvailable}
              onChange={(e) => setForm({ ...form, isAvailable: e.target.checked })}
              className="accent-orange-500"
            />
            Available for ordering
          </label>
        </form>
      </Modal>
    </div>
  )
}
