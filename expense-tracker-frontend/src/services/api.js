import axios from 'axios'

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor for adding auth token
api.interceptors.request.use(
  config => {
    // Add auth token if available
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      // Server responded with error status
      if (error.response.status === 401) {
        // Unauthorized - redirect to login
        localStorage.removeItem('authToken')
        window.location.href = '/login'
      }
      
      // Extract error message from response
      const message = error.response.data?.message || error.response.data?.error || 'An error occurred'
      error.message = message
    } else if (error.request) {
      // Request was made but no response received
      error.message = 'Network error. Please check your connection.'
    } else {
      // Something else happened
      error.message = 'An unexpected error occurred'
    }
    
    return Promise.reject(error)
  }
)

export default api