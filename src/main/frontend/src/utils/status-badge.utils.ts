export const getAppointmentStatusBadgeClass = (status: string) => {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800'
    case 'SCHEDULED':
      return 'bg-blue-100 text-blue-800'
    case 'COMPLETED':
      return 'bg-green-100 text-green-800'
    case 'CANCELLED':
      return 'bg-red-100 text-red-800'
    case 'NO_SHOW':
      return 'bg-orange-100 text-orange-800'
    default :
      return 'bg-gray-100 text-gray-800'
  }
}

export const getPaymentStatusBadgeClass = (status: string) => {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800'
    case 'COMPLETED':
      return 'bg-green-100 text-green-800'
    case 'FAILED':
      return 'bg-red-100 text-red-800'
    case 'CANCELLED':
      return 'bg-orange-100 text-orange-800'
    default :
      return 'bg-gray-100 text-gray-800'
  }
}