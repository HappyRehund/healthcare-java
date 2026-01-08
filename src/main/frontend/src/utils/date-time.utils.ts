export const formatTime = (timeString: string) => {
  return timeString.substring(0,5) // extract HH:mm from HH:mm:ss
}

export const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  });
}