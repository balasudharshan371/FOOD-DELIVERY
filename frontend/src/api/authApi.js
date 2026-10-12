import axiosClient from './axiosClient';

export const authApi = {
  login: (credentials) => axiosClient.post('/auth/login', credentials),
  register: (userData) => axiosClient.post('/auth/register', userData),
  getCurrentUser: () => axiosClient.get('/auth/me'),
  getProfile: () => axiosClient.get('/users/profile'),
  addAddress: (address) => axiosClient.post('/users/addresses', address),
  deleteAddress: (id) => axiosClient.delete(`/users/addresses/${id}`),
};
