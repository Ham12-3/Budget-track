<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-lg max-w-md w-full p-6">
      <!-- Modal Header -->
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ mode === 'edit' ? 'Edit Transaction' : 'Add New Transaction' }}
        </h2>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-500"
        >
          <XMarkIcon class="w-6 h-6" />
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit">
        <!-- Transaction Type Toggle -->
        <div class="mb-4">
          <label class="form-label">Type</label>
          <div class="flex gap-2">
            <button
              type="button"
              @click="form.type = 'EXPENSE'"
              class="flex-1 py-2 px-4 rounded-lg font-medium transition-colors"
              :class="form.type === 'EXPENSE' ? 'bg-danger-500 text-white' : 'bg-gray-200 text-gray-700'"
            >
              Expense
            </button>
            <button
              type="button"
              @click="form.type = 'INCOME'"
              class="flex-1 py-2 px-4 rounded-lg font-medium transition-colors"
              :class="form.type === 'INCOME' ? 'bg-success-500 text-white' : 'bg-gray-200 text-gray-700'"
            >
              Income
            </button>
          </div>
        </div>

        <!-- Amount -->
        <div class="mb-4">
          <label class="form-label">Amount</label>
          <div class="relative">
            <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">$</span>
            <input
              v-model.number="form.amount"
              type="number"
              step="0.01"
              min="0"
              required
              class="form-input pl-8"
              placeholder="0.00"
            />
          </div>
        </div>

        <!-- Category -->
        <div class="mb-4">
          <label class="form-label">Category</label>
          <select
            v-model="form.categoryId"
            required
            class="form-select"
          >
            <option :value="null" disabled>Select a category</option>
            <option
              v-for="category in filteredCategories"
              :key="category.id"
              :value="category.id"
            >
              {{ category.icon }} {{ category.name }}
            </option>
          </select>
        </div>

        <!-- Date -->
        <div class="mb-4">
          <label class="form-label">Date</label>
          <input
            v-model="form.transactionDate"
            type="date"
            required
            class="form-input"
            :max="today"
          />
        </div>

        <!-- Description -->
        <div class="mb-6">
          <label class="form-label">Description (Optional)</label>
          <textarea
            v-model="form.description"
            rows="3"
            class="form-input"
            placeholder="Add notes about this transaction..."
          ></textarea>
        </div>

        <!-- Actions -->
        <div class="flex gap-3">
          <button
            type="button"
            @click="$emit('close')"
            class="btn btn-secondary flex-1"
          >
            Cancel
          </button>
          <button
            type="submit"
            :disabled="loading"
            class="btn btn-primary flex-1 flex items-center justify-center"
          >
            <span v-if="loading" class="flex items-center">
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Saving...
            </span>
            <span v-else>{{ mode === 'edit' ? 'Update' : 'Add' }} Transaction</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'
import { useTransactionsStore } from '@/stores/transactions'
import { useUserStore } from '@/stores/user'
import { useToast } from 'vue-toastification'
import api from '@/services/api'

const props = defineProps({
  transaction: {
    type: Object,
    default: null
  },
  mode: {
    type: String,
    default: 'add'
  }
})

const emit = defineEmits(['close', 'saved'])

const transactionsStore = useTransactionsStore()
const userStore = useUserStore()
const toast = useToast()

// Form data
const form = ref({
  type: 'EXPENSE',
  amount: null,
  categoryId: null,
  transactionDate: new Date().toISOString().split('T')[0],
  description: ''
})

const categories = ref([])
const loading = ref(false)

// Computed
const today = computed(() => new Date().toISOString().split('T')[0])

const filteredCategories = computed(() => {
  // Filter categories based on transaction type
  if (form.value.type === 'INCOME') {
    return categories.value.filter(c => c.name === 'Income' || c.name === 'Salary')
  }
  return categories.value.filter(c => c.name !== 'Income' && c.name !== 'Salary')
})

// Methods
const loadCategories = async () => {
  try {
    const response = await api.get('/categories')
    categories.value = response.data
  } catch (error) {
    console.error('Failed to load categories:', error)
    toast.error('Failed to load categories')
  }
}

const handleSubmit = async () => {
  if (!form.value.amount || !form.value.categoryId) {
    toast.warning('Please fill in all required fields')
    return
  }

  loading.value = true
  const userId = userStore.currentUser.id

  try {
    if (props.mode === 'edit') {
      await transactionsStore.updateTransaction(userId, props.transaction.id, form.value)
    } else {
      await transactionsStore.createTransaction(userId, form.value)
    }
    
    emit('saved')
    emit('close')
  } catch (error) {
    console.error('Failed to save transaction:', error)
  } finally {
    loading.value = false
  }
}

// Initialize form with transaction data if editing
watch(() => props.transaction, (newTransaction) => {
  if (newTransaction && props.mode === 'edit') {
    form.value = {
      type: newTransaction.type,
      amount: newTransaction.amount,
      categoryId: newTransaction.category.id,
      transactionDate: newTransaction.transactionDate,
      description: newTransaction.description || ''
    }
  }
}, { immediate: true })

onMounted(() => {
  loadCategories()
})
</script>