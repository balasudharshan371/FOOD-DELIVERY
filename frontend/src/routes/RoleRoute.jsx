import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function RoleRoute({ children, roles = [] }) {
  const { user, loading } = useAuth()

  if (loading) return null

  if (!user) return <Navigate to="/login" replace />

  const hasRole = roles.some((r) => user.roles?.includes(r))
  if (!hasRole) return <Navigate to="/access-denied" replace />

  return children
}
