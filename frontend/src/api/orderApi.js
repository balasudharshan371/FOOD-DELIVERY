import axiosClient from './axiosClient'

export const createOrder = (data) => axiosClient.post('/orders', data)
export const getMyOrders = (params) => axiosClient.get('/orders/my', { params })
export const getOrderById = (id) => axiosClient.get(`/orders/${id}`)
export const cancelOrder = (id, reason) => axiosClient.patch(`/orders/${id}/cancel`, { reason })

export const getOwnerOrders = (params) => axiosClient.get('/owner/orders', { params })
export const updateOrderStatus = (id, status, note) =>
  axiosClient.patch(`/owner/orders/${id}/status`, { status, note })

export const getAllAdminOrders = (params) => axiosClient.get('/admin/orders', { params })

export const addReview = (data) => axiosClient.post('/reviews', data)
export const getRestaurantReviews = (restaurantId) =>
  axiosClient.get(`/reviews/restaurant/${restaurantId}`)

// Legacy compat
export const orderApi = {
  createOrder,
  getMyOrders,
  getOrderById,
  cancelOrder,
  getOwnerOrders,
  updateOrderStatus,
  getAllAdminOrders,
  addReview,
  getRestaurantReviews,
}
