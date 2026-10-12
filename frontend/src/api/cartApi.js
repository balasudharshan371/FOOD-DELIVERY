import axiosClient from './axiosClient';

export const cartApi = {
  getCart: () => axiosClient.get('/cart'),
  addToCart: (menuItemId, quantity = 1, confirmReplace = false) =>
    axiosClient.post(`/cart/items?confirmReplace=${confirmReplace}`, { menuItemId, quantity }),
  updateQuantity: (menuItemId, quantity) =>
    axiosClient.patch(`/cart/items/${menuItemId}`, { quantity }),
  removeItem: (menuItemId) => axiosClient.delete(`/cart/items/${menuItemId}`),
  clearCart: () => axiosClient.delete('/cart'),
};
