import axiosClient from './axiosClient'

export const getAdminDashboard = () => axiosClient.get('/admin/dashboard')
export const getOwnerDashboard = () => axiosClient.get('/owner/dashboard')
export const getAllUsers = (params) => axiosClient.get('/admin/users', { params })
export const updateUserStatus = (userId, status) =>
  axiosClient.patch(`/admin/users/${userId}/status`, { status })
export const getAllCategories = (params) => axiosClient.get('/categories', { params })
export const createCategory = (data) => axiosClient.post('/categories', data)
export const updateCategory = (id, data) => axiosClient.put(`/categories/${id}`, data)
export const deleteCategory = (id) => axiosClient.delete(`/categories/${id}`)

export const dashboardApi = {
  getAdminDashboard, getOwnerDashboard, getAllUsers, updateUserStatus,
}
