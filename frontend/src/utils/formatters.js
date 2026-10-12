export const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return '₹0.00'
  return `₹${Number(amount).toFixed(2)}`
}

export const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
  })
}

export const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

export const capitalize = (str) => {
  if (!str) return ''
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
}

export const truncate = (str, len = 80) => {
  if (!str) return ''
  return str.length > len ? str.slice(0, len) + '…' : str
}

export const orderStatusColor = (status) => {
  const map = {
    PENDING: 'warning',
    CONFIRMED: 'info',
    PREPARING: 'primary',
    OUT_FOR_DELIVERY: 'primary',
    DELIVERED: 'success',
    CANCELLED: 'danger',
    REJECTED: 'danger',
  }
  return map[status] || 'secondary'
}
