import axiosClient from './axiosClient'

export const validateCoupon = (data) => axiosClient.post('/coupons/validate', data)
export const getAllAdminCoupons = (params) => axiosClient.get('/admin/coupons', { params })
export const createCoupon = (data) => axiosClient.post('/admin/coupons', data)
export const updateCoupon = (id, data) => axiosClient.put(`/admin/coupons/${id}`, data)
export const deleteCoupon = (id) => axiosClient.delete(`/admin/coupons/${id}`)
export const toggleCouponStatus = (id) => axiosClient.patch(`/admin/coupons/${id}/status`)

export const couponApi = { validateCoupon, getAllAdminCoupons, createCoupon, updateCoupon, deleteCoupon }
