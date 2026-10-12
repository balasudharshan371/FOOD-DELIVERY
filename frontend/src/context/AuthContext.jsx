import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';
import { useToast } from './ToastContext';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();

  useEffect(() => {
    const savedToken = localStorage.getItem('foodiehub_token');
    const savedUser = localStorage.getItem('foodiehub_user');

    if (savedToken && savedUser) {
      setToken(savedToken);
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('foodiehub_user');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      const res = await authApi.login({ email, password });
      if (res.success && res.data) {
        const { accessToken, user: authUser } = res.data;
        localStorage.setItem('foodiehub_token', accessToken);
        localStorage.setItem('foodiehub_user', JSON.stringify(authUser));
        setToken(accessToken);
        setUser(authUser);
        showToast(`Welcome back, ${authUser.fullName}!`, 'success');
        return authUser;
      }
    } catch (err) {
      const msg = err.message || 'Login failed. Please check credentials.';
      showToast(msg, 'error');
      throw err;
    }
  };

  const register = async (formData) => {
    try {
      const res = await authApi.register(formData);
      if (res.success && res.data) {
        const { accessToken, user: authUser } = res.data;
        localStorage.setItem('foodiehub_token', accessToken);
        localStorage.setItem('foodiehub_user', JSON.stringify(authUser));
        setToken(accessToken);
        setUser(authUser);
        showToast('Registration successful! Welcome to FoodieHub.', 'success');
        return authUser;
      }
    } catch (err) {
      const msg = err.message || 'Registration failed.';
      showToast(msg, 'error');
      throw err;
    }
  };

  const logout = () => {
    localStorage.removeItem('foodiehub_token');
    localStorage.removeItem('foodiehub_user');
    setToken(null);
    setUser(null);
    showToast('Logged out successfully', 'info');
  };

  const hasRole = (role) => {
    return user && user.role === role;
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        register,
        logout,
        hasRole,
        isAuthenticated: !!token,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
