import React, { createContext, useContext, useState, useEffect } from 'react';
import { cartApi } from '../api/cartApi';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';

const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({
    items: [],
    subtotal: 0,
    estimatedTax: 0,
    deliveryFee: 0,
    estimatedTotal: 0,
    totalItemCount: 0,
    restaurantId: null,
    restaurantName: null,
  });
  const [loading, setLoading] = useState(false);
  const { user, isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const isCustomer = () => {
    if (!isAuthenticated || !user) return false;
    if (user.role === 'ROLE_CUSTOMER') return true;
    if (Array.isArray(user.roles) && user.roles.includes('ROLE_CUSTOMER')) return true;
    return true; // Default to true if user is logged in
  };

  const fetchCart = async () => {
    if (!isAuthenticated || !isCustomer()) return;
    try {
      setLoading(true);
      const res = await cartApi.getCart();
      if (res.data) {
        setCart(res.data);
      }
    } catch (err) {
      console.error('Failed to load cart', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAuthenticated && isCustomer()) {
      fetchCart();
    } else {
      setCart({
        items: [],
        subtotal: 0,
        estimatedTax: 0,
        deliveryFee: 0,
        estimatedTotal: 0,
        totalItemCount: 0,
        restaurantId: null,
        restaurantName: null,
      });
    }
  }, [isAuthenticated, user]);

  const addToCart = async (menuItemId, quantity = 1, confirmReplace = false) => {
    try {
      const res = await cartApi.addToCart(menuItemId, quantity, confirmReplace);
      if (res.data) {
        setCart(res.data);
        return res.data;
      }
    } catch (err) {
      const msg = err.message || (typeof err === 'string' ? err : 'Failed to add item');
      throw err;
    }
  };

  const updateQuantity = async (menuItemId, quantity) => {
    try {
      const res = await cartApi.updateQuantity(menuItemId, quantity);
      if (res.success && res.data) {
        setCart(res.data);
      }
    } catch (err) {
      showToast(err.message || 'Failed to update quantity', 'error');
    }
  };

  const removeItem = async (menuItemId) => {
    try {
      const res = await cartApi.removeItem(menuItemId);
      if (res.success && res.data) {
        setCart(res.data);
        showToast('Item removed from cart', 'info');
      }
    } catch (err) {
      showToast(err.message || 'Failed to remove item', 'error');
    }
  };

  const clearCart = async () => {
    try {
      await cartApi.clearCart();
      setCart({
        items: [],
        subtotal: 0,
        estimatedTax: 0,
        deliveryFee: 0,
        estimatedTotal: 0,
        totalItemCount: 0,
        restaurantId: null,
        restaurantName: null,
      });
      showToast('Cart cleared', 'info');
    } catch (err) {
      showToast(err.message || 'Failed to clear cart', 'error');
    }
  };

  return (
    <CartContext.Provider
      value={{
        cart,
        loading,
        fetchCart,
        addToCart,
        addItem: addToCart,
        updateQuantity,
        updateItem: updateQuantity,
        removeItem,
        clearCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) throw new Error('useCart must be used within a CartProvider');
  return context;
};
