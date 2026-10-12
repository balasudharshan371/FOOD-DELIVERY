import React, { lazy, Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import ProtectedRoute from './ProtectedRoute'
import RoleRoute from './RoleRoute'
import Loader from '../components/common/Loader'
import { ROLES } from '../utils/constants'

// Public pages
const HomePage = lazy(() => import('../pages/HomePage'))
const RestaurantsPage = lazy(() => import('../pages/RestaurantsPage'))
const RestaurantDetailPage = lazy(() => import('../pages/RestaurantDetailPage'))
const LoginPage = lazy(() => import('../pages/LoginPage'))
const RegisterPage = lazy(() => import('../pages/RegisterPage'))
const NotFoundPage = lazy(() => import('../pages/NotFoundPage'))
const AccessDeniedPage = lazy(() => import('../pages/AccessDeniedPage'))

// Customer pages
const ProfilePage = lazy(() => import('../pages/customer/ProfilePage'))
const CartPage = lazy(() => import('../pages/customer/CartPage'))
const CheckoutPage = lazy(() => import('../pages/customer/CheckoutPage'))
const OrderConfirmPage = lazy(() => import('../pages/customer/OrderConfirmPage'))
const OrderHistoryPage = lazy(() => import('../pages/customer/OrderHistoryPage'))
const OrderDetailPage = lazy(() => import('../pages/customer/OrderDetailPage'))
const FavoritesPage = lazy(() => import('../pages/customer/FavoritesPage'))

// Owner pages
const OwnerDashboardPage = lazy(() => import('../pages/owner/OwnerDashboardPage'))
const OwnerMenuPage = lazy(() => import('../pages/owner/OwnerMenuPage'))
const OwnerOrdersPage = lazy(() => import('../pages/owner/OwnerOrdersPage'))
const OwnerRestaurantPage = lazy(() => import('../pages/owner/OwnerRestaurantPage'))

// Admin pages
const AdminDashboardPage = lazy(() => import('../pages/admin/AdminDashboardPage'))
const AdminUsersPage = lazy(() => import('../pages/admin/AdminUsersPage'))
const AdminRestaurantsPage = lazy(() => import('../pages/admin/AdminRestaurantsPage'))
const AdminCouponsPage = lazy(() => import('../pages/admin/AdminCouponsPage'))
const AdminOrdersPage = lazy(() => import('../pages/admin/AdminOrdersPage'))
const AdminCategoriesPage = lazy(() => import('../pages/admin/AdminCategoriesPage'))

const SuspenseWrap = ({ children }) => (
  <Suspense fallback={<Loader fullScreen message="Loading page…" />}>{children}</Suspense>
)

export default function AppRoutes() {
  return (
    <SuspenseWrap>
      <Routes>
        {/* Public */}
        <Route path="/" element={<HomePage />} />
        <Route path="/restaurants" element={<RestaurantsPage />} />
        <Route path="/restaurants/:id" element={<RestaurantDetailPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/access-denied" element={<AccessDeniedPage />} />

        {/* Customer */}
        <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
        <Route path="/cart" element={<ProtectedRoute><CartPage /></ProtectedRoute>} />
        <Route path="/checkout" element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
        <Route path="/order-confirm/:id" element={<ProtectedRoute><OrderConfirmPage /></ProtectedRoute>} />
        <Route path="/orders" element={<ProtectedRoute><OrderHistoryPage /></ProtectedRoute>} />
        <Route path="/orders/:id" element={<ProtectedRoute><OrderDetailPage /></ProtectedRoute>} />
        <Route path="/favorites" element={<ProtectedRoute><FavoritesPage /></ProtectedRoute>} />

        {/* Owner */}
        <Route path="/owner" element={<RoleRoute roles={[ROLES.OWNER]}><OwnerDashboardPage /></RoleRoute>} />
        <Route path="/owner/menu" element={<RoleRoute roles={[ROLES.OWNER]}><OwnerMenuPage /></RoleRoute>} />
        <Route path="/owner/orders" element={<RoleRoute roles={[ROLES.OWNER]}><OwnerOrdersPage /></RoleRoute>} />
        <Route path="/owner/restaurant" element={<RoleRoute roles={[ROLES.OWNER]}><OwnerRestaurantPage /></RoleRoute>} />

        {/* Admin */}
        <Route path="/admin" element={<RoleRoute roles={[ROLES.ADMIN]}><AdminDashboardPage /></RoleRoute>} />
        <Route path="/admin/users" element={<RoleRoute roles={[ROLES.ADMIN]}><AdminUsersPage /></RoleRoute>} />
        <Route path="/admin/restaurants" element={<RoleRoute roles={[ROLES.ADMIN]}><AdminRestaurantsPage /></RoleRoute>} />
        <Route path="/admin/coupons" element={<RoleRoute roles={[ROLES.ADMIN]}><AdminCouponsPage /></RoleRoute>} />
        <Route path="/admin/orders" element={<RoleRoute roles={[ROLES.ADMIN]}><AdminOrdersPage /></RoleRoute>} />
        <Route path="/admin/categories" element={<RoleRoute roles={[ROLES.ADMIN]}><AdminCategoriesPage /></RoleRoute>} />

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </SuspenseWrap>
  )
}
