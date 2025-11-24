<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-lg max-w-md w-full p-6">
      <!-- Modal Header -->
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ mode === 'edit' ? 'Edit Budget' : 'Create Budget' }}
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
        <!-- Category -->
        <div class="mb-4">
          <label class="form-label">Category</label>
          <select
            v-model="form.categoryId"
            required
            class="form-select"
            :disabled="mode === 'edit'"
          >
            <option :value="null" disabled>Select a category</option>
            <option
              v-for="category in availableCategories"
              :key="category.id"
              :value="category.id"
            >
              {{ category.icon }} {{ category.name }}
            </option>
          </select>
          <p v-if="mode === 'edit'" class="text-xs text-gray-500 mt-1">
            Category cannot be changed when editing
          </p>
        </div>

        <!-- Budget Amount -->
        <div class="mb-4">
          <label class="form-label">Budget Amount</label>
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

        <!-- Month & Year -->
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="form-label">Month</label>
            <select
              v-model="form.month"
              class="form-select"
              :disabled="mode === 'edit'"
            >
              <option v-for="(name, index) in monthNames" :key="index" :value="index + 1">
                {{ name }}
              </option>
            </select>
          </div>
          
          <div>
            <label class="form-label">Year</label>
            <select
              v-model="form.year"
              class="form-select"
              :disabled="mode === 'edit'"
            >
              <option v-for="year in yearOptions" :key="year" :value="year">
                {{ year }}
              </option>
            </select>
          </div>
        </div>

        <!-- Alert Threshold -->
        <div class="mb-4">
          <label class="form-label">Alert Threshold (%)</label>
          <div class="flex items-center gap-3">
            <input
              v-model.number="form.alertThreshold"
              type="range"
              min="50"
              max="100"
              step="5"
              class="flex-1"
            />
            <span class="text-sm font-medium text-gray-700 w-12">{{ form.alertThreshold }}%</span>
          </div>
          <p class="text-xs text-gray-500 mt-1">
            You'll be notified when spending reaches this percentage
          </p>
        </div>

        <!-- Notes -->
        <div class="mb-6">
          <label class="form-label">Notes (Optional)</label>
          <textarea
            v-model="form.notes"
            rows="3"
            class="form-input"
            placeholder="Add notes about this budget..."
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
            class="btn btn-primary flex-1"
          >
            {{ loading ? 'Saving...' : (mode === 'edit' ? 'Update' : 'Create') }} Budget
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'
import { useUserStore } from '@/stores/user'
import { useToast } from 'vue-toastification'
import api from '@/services/api'

const props = defineProps({
  budget: {
    type: Object,
    default: null
  },
  mode: {
    type: String,
    default: 'add'
  },
  month: {
    type: Number,
    default: new Date().getMonth() + 1
  },
  year: {
    type: Number,
    default: new Date().getFullYear()
  }
})

const emit = defineEmits(['close', 'saved'])

const userStore = useUserStore()
const toast = useToast()

// Form data
const form = ref({
  categoryId: null,
  amount: null,
  month: props.month,
  year: props.year,
  alertThreshold: 80,
  notes: ''
})

const categories = ref([])
const loading = ref(false)

// Computed
const monthNames = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
]

const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  return Array.from({ length: 5 }, (_, i) => currentYear - 2 + i)
})

const availableCategories = computed(() => {
  // Filter out Income category for budgets
  return categories.value.filter(c => c.name !== 'Income' && c.name !== 'Savings')
})

// Methods
const loadCategories = async () => {
  try {
    const response = await api.get('/categories')
    categories.value = response.data
  } catch (error) {
    console.error('Failed to load categories:', error)
    // Use mock categories
    categories.value = [
      { id: 1, name: 'Food & Dining', icon: '🍔' },
      { id: 2, name: 'Transportation', icon: '🚗' },
      { id: 3, name: 'Shopping', icon: '🛍️' },
      { id: 4, name: 'Entertainment', icon: '🎬' },
      { id: 5, name: 'Bills & Utilities', icon: '💡' }
    ]
  }
}

const handleSubmit = async () => {
  if (!form.value.categoryId || !form.value.amount) {
    toast.warning('Please fill in all required fields')
    return
  }

  loading.value = true
  const userId = userStore.currentUser.id

  try {
    if (props.mode === 'edit') {
      await api.put(`/users/${userId}/budgets/${props.budget.id}`, form.value)
      toast.success('Budget updated successfully')
    } else {
      await api.post(`/users/${userId}/budgets`, form.value)
      toast.success('Budget created successfully')
    }
    
    emit('saved')
    emit('close')
  } catch (error) {
    console.error('Failed to save budget:', error)
    toast.error('Failed to save budget')
  } finally {
    loading.value = false
  }
}

// Initialize form with budget data if editing
watch(() => props.budget, (newBudget) => {
  if (newBudget && props.mode === 'edit') {
    form.value = {
      categoryId: newBudget.category.id,
      amount: newBudget.amount,
      month: props.month,
      year: props.year,
      alertThreshold: newBudget.alertThreshold || 80,
      notes: newBudget.notes || ''
    }
  }
}, { immediate: true })

onMounted(() => {
  loadCategories()
})
</script>