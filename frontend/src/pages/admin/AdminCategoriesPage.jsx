import React, { useEffect, useState } from 'react'
import { getAllCategories, createCategory, updateCategory, deleteCategory } from '../../api/dashboardApi'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import { useToast } from '../../context/ToastContext'

const EMPTY_CATEGORY = {
  name: '',
  description: '',
  imageUrl: '',
  isActive: true,
}

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY_CATEGORY)
  const [editingId, setEditingId] = useState(null)
  const [saving, setSaving] = useState(false)
  const { showToast } = useToast()

  const loadCategories = () => {
    setLoading(true)
    getAllCategories()
      .then((res) => {
        const d = res.data?.data || res.data
        setCategories(Array.isArray(d) ? d : d?.content || [])
      })
      .catch(() => setCategories([]))
      .finally(() => setLoading(false))
  }

  useEffect(loadCategories, [])

  const handleOpenCreate = () => {
    setForm(EMPTY_CATEGORY)
    setEditingId(null)
    setModalOpen(true)
  }

  const handleOpenEdit = (c) => {
    setForm({ ...c })
    setEditingId(c.id)
    setModalOpen(true)
  }

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      if (editingId) {
        await updateCategory(editingId, form)
        showToast('Category updated! ✅', 'success')
      } else {
        await createCategory(form)
        showToast('Category created! ✅', 'success')
      }
      setModalOpen(false)
      loadCategories()
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to save category', 'error')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this category?')) return
    try {
      await deleteCategory(id)
      showToast('Category deleted', 'info')
      loadCategories()
    } catch (err) {
      showToast('Failed to delete category', 'error')
    }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Manage Categories</h1>
        <button
          onClick={handleOpenCreate}
          className="bg-orange-500 text-white px-5 py-2.5 rounded-xl font-semibold hover:bg-orange-600 transition"
        >
          + Add Category
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5">
        {categories.map((c) => (
          <div key={c.id} className="bg-white rounded-2xl shadow p-5 flex flex-col justify-between border border-gray-100">
            <div>
              <div className="flex items-center justify-between mb-2">
                <h3 className="font-bold text-gray-900 text-lg">{c.name}</h3>
                <span className={`px-2 py-0.5 text-xs rounded-full font-medium ${
                  c.isActive ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                }`}>
                  {c.isActive ? 'Active' : 'Inactive'}
                </span>
              </div>
              <p className="text-gray-500 text-sm mb-4">{c.description || 'No description'}</p>
            </div>
            <div className="flex justify-end gap-3 pt-3 border-t">
              <button onClick={() => handleOpenEdit(c)} className="text-sm text-blue-600 hover:underline">Edit</button>
              <button onClick={() => handleDelete(c.id)} className="text-sm text-red-500 hover:underline">Delete</button>
            </div>
          </div>
        ))}
      </div>

      {categories.length === 0 && (
        <div className="text-center py-16 text-gray-400">No categories found</div>
      )}

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingId ? 'Edit Category' : 'Create Category'}
        footer={
          <>
            <button onClick={() => setModalOpen(false)} className="px-5 py-2 rounded-xl border text-gray-600 hover:bg-gray-50">
              Cancel
            </button>
            <button
              onClick={handleSave}
              disabled={saving}
              className="bg-orange-500 text-white px-5 py-2 rounded-xl font-semibold hover:bg-orange-600 disabled:opacity-60"
            >
              {saving ? 'Saving…' : 'Save Category'}
            </button>
          </>
        }
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              placeholder="Biryani, Desserts, Fast Food..."
              required
              className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Category details..."
              rows={3}
              className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
            <input
              value={form.imageUrl}
              onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
              placeholder="https://images.unsplash.com/..."
              className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
            />
          </div>
          <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
            <input
              type="checkbox"
              checked={form.isActive}
              onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
              className="accent-orange-500"
            />
            Active category
          </label>
        </form>
      </Modal>
    </div>
  )
}
