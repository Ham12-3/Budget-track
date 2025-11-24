import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

const toast = useToast()

export const useTransactionsStore = defineStore('transactions', () => {
  // State
  const transactions = ref([])
  const loading = ref(false)
  const error = ref(null)
  const pagination = ref({
    page: 0,
    size: 20,
    total: 0,
    totalPages: 0
  })
  
  // Filters
  const filters = ref({
    categoryId: null,
    startDate: null,
    endDate: null,
    type: null
  })
  
  // Computed
  const totalExpenses = computed(() => {
    return transactions.value
      .filter(t => t.type === 'EXPENSE')
      .reduce((sum, t) => sum + parseFloat(t.amount), 0)
  })
  
  const totalIncome = computed(() => {
    return transactions.value
      .filter(t => t.type === 'INCOME')
      .reduce((sum, t) => sum + parseFloat(t.amount), 0)
  })
  
  // Actions
  async function fetchTransactions(userId, page = 0) {
    loading.value = true
    error.value = null
    
    try {
      const params = {
        page,
        size: pagination.value.size,
        sortBy: 'transactionDate',
        sortDirection: 'DESC',
        ...filters.value
      }
      
      const response = await api.get(`/users/${userId}/transactions`, { params })
      
      transactions.value = response.data.content
      pagination.value = {
        page: response.data.number,
        size: response.data.size,
        total: response.data.totalElements,
        totalPages: response.data.totalPages
      }
    } catch (err) {
      error.value = err.message
      toast.error('Failed to load transactions')
    } finally {
      loading.value = false
    }
  }
  
  async function createTransaction(userId, transactionData) {
    try {
      const response = await api.post(`/users/${userId}/transactions`, transactionData)
      transactions.value.unshift(response.data)
      toast.success('Transaction added successfully')
      return response.data
    } catch (err) {
      toast.error('Failed to create transaction')
      throw err
    }
  }
  
  async function updateTransaction(userId, transactionId, transactionData) {
    try {
      const response = await api.put(
        `/users/${userId}/transactions/${transactionId}`,
        transactionData
      )
      
      const index = transactions.value.findIndex(t => t.id === transactionId)
      if (index !== -1) {
        transactions.value[index] = response.data
      }
      
      toast.success('Transaction updated successfully')
      return response.data
    } catch (err) {
      toast.error('Failed to update transaction')
      throw err
    }
  }
  
  async function deleteTransaction(userId, transactionId) {
    try {
      await api.delete(`/users/${userId}/transactions/${transactionId}`)
      
      const index = transactions.value.findIndex(t => t.id === transactionId)
      if (index !== -1) {
        transactions.value.splice(index, 1)
      }
      
      toast.success('Transaction deleted successfully')
    } catch (err) {
      toast.error('Failed to delete transaction')
      throw err
    }
  }
  
  function setFilters(newFilters) {
    filters.value = { ...filters.value, ...newFilters }
  }
  
  function clearFilters() {
    filters.value = {
      categoryId: null,
      startDate: null,
      endDate: null,
      type: null
    }
  }
  
  return {
    transactions,
    loading,
    error,
    pagination,
    filters,
    totalExpenses,
    totalIncome,
    fetchTransactions,
    createTransaction,
    updateTransaction,
    deleteTransaction,
    setFilters,
    clearFilters
  }
})