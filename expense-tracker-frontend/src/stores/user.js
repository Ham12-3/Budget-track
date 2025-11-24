import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // State
  const currentUser = ref({
    id: 1,
    name: 'John Doe',
    email: 'john.doe@example.com',
    username: 'johndoe'
  })
  
  const isAuthenticated = ref(true)
  
  // Actions
  function setUser(user) {
    currentUser.value = user
    isAuthenticated.value = true
  }
  
  function logout() {
    currentUser.value = null
    isAuthenticated.value = false
  }
  
  return {
    currentUser,
    isAuthenticated,
    setUser,
    logout
  }
})