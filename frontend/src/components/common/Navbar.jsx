import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { ShoppingBag, Heart, User, LogOut, UtensilsCrossed, ShieldAlert } from 'lucide-react';

export const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();

  return (
    <header className="sticky top-0 z-40 bg-white border-b border-slate-200 shadow-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        
        {/* Brand Logo */}
        <Link to="/" className="flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-brand-500 to-brand-600 flex items-center justify-center text-white font-black text-xl shadow-md">
            F
          </div>
          <div>
            <span className="font-extrabold text-xl tracking-tight text-slate-900">
              Foodie<span className="text-brand-600">Hub</span>
            </span>
          </div>
        </Link>

        {/* Center Navigation Links */}
        <nav className="hidden md:flex items-center gap-6 text-sm font-semibold text-slate-600">
          <Link to="/" className="hover:text-brand-600 transition">
            Restaurants
          </Link>
          
          {isAuthenticated && user?.role === 'ROLE_CUSTOMER' && (
            <>
              <Link to="/orders" className="hover:text-brand-600 transition">
                My Orders
              </Link>
              <Link to="/favorites" className="hover:text-brand-600 transition flex items-center gap-1">
                <Heart className="w-4 h-4 text-rose-500" />
                Favorites
              </Link>
            </>
          )}

          {isAuthenticated && user?.role === 'ROLE_RESTAURANT_OWNER' && (
            <>
              <Link to="/owner" className="hover:text-brand-600 transition flex items-center gap-1 font-bold text-amber-700">
                <UtensilsCrossed className="w-4 h-4" />
                Owner Portal
              </Link>
              <Link to="/owner/orders" className="hover:text-brand-600 transition">
                Incoming Orders
              </Link>
              <Link to="/owner/menu" className="hover:text-brand-600 transition">
                Menu Management
              </Link>
            </>
          )}

          {isAuthenticated && user?.role === 'ROLE_ADMIN' && (
            <>
              <Link to="/admin" className="hover:text-brand-600 transition flex items-center gap-1 font-bold text-purple-700">
                <ShieldAlert className="w-4 h-4" />
                Admin Panel
              </Link>
              <Link to="/admin/restaurants" className="hover:text-brand-600 transition">
                Restaurants
              </Link>
              <Link to="/admin/coupons" className="hover:text-brand-600 transition">
                Coupons
              </Link>
              <Link to="/admin/users" className="hover:text-brand-600 transition">
                Users
              </Link>
            </>
          )}
        </nav>

        {/* Right Action Controls */}
        <div className="flex items-center gap-3">
          {/* Cart Button (For Customers / Guests) */}
          {(!isAuthenticated || user?.role === 'ROLE_CUSTOMER') && (
            <Link
              to="/cart"
              className="relative bg-slate-100 hover:bg-slate-200 text-slate-800 p-2.5 rounded-xl transition flex items-center gap-1.5"
            >
              <ShoppingBag className="w-5 h-5 text-brand-600" />
              <span className="text-xs font-bold hidden sm:inline">Cart</span>
              {cart.totalItemCount > 0 && (
                <span className="bg-brand-600 text-white font-extrabold text-[11px] w-5 h-5 rounded-full flex items-center justify-center">
                  {cart.totalItemCount}
                </span>
              )}
            </Link>
          )}

          {isAuthenticated ? (
            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-slate-700 hidden lg:inline">
                {user.fullName}
              </span>
              <span className="px-2 py-0.5 rounded text-[10px] font-bold uppercase bg-slate-200 text-slate-700 hidden sm:inline">
                {user.role.replace('ROLE_', '')}
              </span>
              <button
                onClick={logout}
                title="Logout"
                className="p-2 rounded-xl text-slate-500 hover:text-rose-600 hover:bg-rose-50 transition"
              >
                <LogOut className="w-5 h-5" />
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Link
                to="/login"
                className="text-xs font-bold text-slate-700 hover:text-brand-600 px-3 py-2 transition"
              >
                Sign In
              </Link>
              <Link
                to="/register"
                className="bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold px-4 py-2 rounded-xl transition shadow-sm"
              >
                Sign Up
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default Navbar;
