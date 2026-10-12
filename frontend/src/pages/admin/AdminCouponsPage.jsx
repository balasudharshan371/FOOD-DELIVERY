import React, { useEffect, useState } from 'react'
import { getAllAdminCoupons, createCoupon, updateCoupon, deleteCoupon } from '../../api/couponApi'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import { useToast } from '../../context/ToastContext'
import { formatCurrency, formatDate } from '../../utils/formatters'

const EMPTY_COUPON = {
  code: '',
  discountType: 'PERCENTAGE',
  discountValue: 10,
  minimumOrderValue: 200,
  maximumDiscount: 100,
  usageLimit: 100,
  perUserUsageLimit: 1,
  startDate: new Date().toISOString().split('T')[0],
  expiryDate: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
  isActive: true,
}

export default function AdminCouponsPage() {
  const [coupons, setCoupons] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY_COUPON)
  const [editingId, setEditingId] = useState(null)
  const [saving, setSaving] = useState(false)
  const { showToast } = useToast()

  const loadCoupons = () => {
    setLoading(true)
    getAllAdminCoupons()
      .then((res) => {
        const d = res.data?.data || res.data
        setCoupons(Array.isArray(d) ? d : d?.content || [])
      })
      .catch(() => setCoupons([]))
      .finally(() => setLoading(false))
  }

  useEffect(loadCoupons, [])

  const handleOpenCreate = () => {
    setForm(EMPTY_COUPON)
    setEditingId(null)
    setModalOpen(true)
  }

  const handleOpenEdit = (c) => {
    setForm({
      ...c,
      startDate: c.startDate ? c.startDate.split('T')[0] : '',
      expiryDate: c.expiryDate ? c.expiryDate.split('T')[0] : '',
    })
    setEditingId(c.id)
    setModalOpen(true)
  }

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      if (editingId) {
        await updateCoupon(editingId, form)
        showToast('Coupon updated successfully! ✅', 'success')
      } else {
        await createCoupon(form)
        showToast('Coupon created successfully! ✅', 'success')
      }
      setModalOpen(false)
      loadCoupons()
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to save coupon', 'error')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this coupon?')) return
    try {
      await deleteCoupon(id)
      showToast('Coupon deleted', 'info')
      loadCoupons()
    } catch (err) {
      showToast('Failed to delete coupon', 'error')
    }
  }

  if (loading) return <Loader />

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Manage Coupons</h1>
        <button
          onClick={handleOpenCreate}
          className="bg-orange-500 text-white px-5 py-2.5 rounded-xl font-semibold hover:bg-orange-600 transition"
        >
          + Add Coupon
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Code', 'Type', 'Value', 'Min Order', 'Valid Till', 'Status', 'Actions'].map((h) => (
                <th key={h} className="px-5 py-3 text-left font-semibold text-gray-700">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {coupons.map((c) => (
              <tr key={c.id} className="hover:bg-gray-50">
                <td className="px-5 py-3 font-mono font-bold text-orange-600">{c.code}</td>
                <td className="px-5 py-3 text-gray-600">{c.discountType}</td>
                <td className="px-5 py-3 font-medium">
                  {c.discountType === 'PERCENTAGE' ? `${c.discountValue}%` : formatCurrency(c.discountValue)}
                </td>
                <td className="px-5 py-3 text-gray-600">{formatCurrency(c.minimumOrderValue)}</td>
                <td className="px-5 py-3 text-gray-500">{formatDate(c.expiryDate)}</td>
                <td className="px-5 py-3">
                  <span className={`px-2 py-1 text-xs rounded-full font-medium ${
                    c.isActive ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                  }`}>
                    {c.isActive ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="px-5 py-3">
                  <div className="flex gap-3">
                    <button onClick={() => handleOpenEdit(c)} className="text-xs text-blue-600 hover:underline">Edit</button>
                    <button onClick={() => handleDelete(c.id)} className="text-xs text-red-500 hover:underline">Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {coupons.length === 0 && (
          <div className="text-center py-16 text-gray-400">No coupons found</div>
        )}
      </div>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingId ? 'Edit Coupon' : 'Create Coupon'}
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
              {saving ? 'Saving…' : 'Save Coupon'}
            </button>
          </>
        }
      >
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Coupon Code</label>
            <input
              value={form.code}
              onChange={(e) => setForm({ ...form, code: e.target.value.toUpperCase() })}
              placeholder="FOODIE50"
              required
              className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
            />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Discount Type</label>
              <select
                value={form.discountType}
                onChange={(e) => setForm({ ...form, discountType: e.target.value })}
                className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm"
              >
                <option value="PERCENTAGE">PERCENTAGE (%)</option>
                <option value="FLAT">FLAT (₹)</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Value</label>
              <input
                type="number"
                value={form.discountValue}
                onChange={(e) => setForm({ ...form, discountValue: Number(e.target.value) })}
                required
                className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm"
              />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Min Order (₹)</label>
              <input
                type="number"
                value={form.minimumOrderValue}
                onChange={(e) => setForm({ ...form, minimumOrderValue: Number(e.target.value) })}
                className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Discount (₹)</label>
              <input
                type="number"
                value={form.maximumDiscount}
                onChange={(e) => setForm({ ...form, maximumDiscount: Number(e.target.value) })}
                className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm"
              />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Expiry Date</label>
              <input
                type="date"
                value={form.expiryDate}
                onChange={(e) => setForm({ ...form, expiryDate: e.target.value })}
                className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Usage Limit</label>
              <input
                type="number"
                value={form.usageLimit}
                onChange={(e) => setForm({ ...form, usageLimit: Number(e.target.value) })}
                className="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm"
              />
            </div>
          </div>
          <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
            <input
              type="checkbox"
              checked={form.isActive}
              onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
              className="accent-orange-500"
            />
            Active coupon
          </label>
        </form>
      </Modal>
    </div>
  )
}
