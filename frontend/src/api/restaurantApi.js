import axiosClient from './axiosClient'

// Public
export const getPublicRestaurants = (params) => axiosClient.get('/restaurants', { params })
export const getRestaurantById = (id) => axiosClient.get(`/restaurants/${id}`)
export const getMenuByRestaurant = (restaurantId) =>
  axiosClient.get(`/menu-items/restaurant/${restaurantId}`)

// Owner
export const getMyRestaurant = () => axiosClient.get('/owner/restaurant/my')
export const updateMyRestaurant = (data) => axiosClient.put('/owner/restaurant/my', data)
export const toggleRestaurantStatus = (id) => axiosClient.patch(`/restaurants/${id}/status`)

// Admin
export const getAllRestaurantsAdmin = (params) => axiosClient.get('/admin/restaurants', { params })
export const createRestaurant = (data) => axiosClient.post('/restaurants', data)
export const updateRestaurant = (id, data) => axiosClient.put(`/restaurants/${id}`, data)
export const deleteRestaurant = (id) => axiosClient.delete(`/restaurants/${id}`)
export const approveRestaurant = (id) => axiosClient.patch(`/admin/restaurants/${id}/approve`)

// Menu items
export const getMenuItems = (restaurantId) =>
  axiosClient.get(`/menu-items/restaurant/${restaurantId}`)
export const createMenuItem = (data) => axiosClient.post('/menu-items', data)
export const updateMenuItem = (id, data) => axiosClient.put(`/menu-items/${id}`, data)
export const deleteMenuItem = (id) => axiosClient.delete(`/menu-items/${id}`)
export const toggleItemAvailability = (id) =>
  axiosClient.patch(`/menu-items/${id}/availability`)

// Favorites
export const getFavorites = () => axiosClient.get('/favorites')
export const toggleFavorite = (restaurantId) =>
  axiosClient.post(`/favorites/${restaurantId}`)
export const removeFavorite = (restaurantId) =>
  axiosClient.delete(`/favorites/${restaurantId}`)

// Legacy compat (object export still referenced by some components)
export const restaurantApi = {
  getRestaurants: getPublicRestaurants,
  getRestaurantById,
  getMyRestaurant,
  createRestaurant,
  updateRestaurant,
  toggleStatus: toggleRestaurantStatus,
  getAllAdmin: getAllRestaurantsAdmin,
  getMenuItems,
  createMenuItem,
  updateMenuItem,
  toggleItemAvailability,
  deleteMenuItem,
}
