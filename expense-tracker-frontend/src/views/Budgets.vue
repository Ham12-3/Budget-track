<template>
  <div class="budgets">
    <!-- Header Section -->
    <div class="mb-6 flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Budget Management</h1>
        <p class="text-gray-600 mt-1">Set monthly spending limits for each category</p>
      </div>
      <button
        @click="showAddModal = true"
        class="btn btn-primary flex items-center"
      >
        <PlusIcon class="w-5 h-5 mr-2" />
        Add Budget
      </button>
    </div>

    <!-- Month Selector -->
    <div class="card mb-6">
      <div class="card-body flex items-center justify-between">
        <button
          @click="previousMonth"
          class="p-2 hover:bg-gray-100 rounded-lg"
        >
          <ChevronLeftIcon class="w-5 h-5" />
        </button>
        
        <div class="text-center">
          <h2 class="text-xl font-semibold text-gray-900">
            {{ currentMonthName }} {{ currentYear }}
          </h2>
          <p class="text-sm text-gray-500 mt-1">
            Total Budget: ${{ formatMoney(totalBudget) }}
          </p>
        </div>
        
        <button
          @click="nextMonth"
          class="p-2 hover:bg-gray-100 rounded-lg"
        >
          <ChevronRightIcon class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!-- Budget Cards Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="budget in budgets"
        :key="budget.id"
        class="card hover:shadow-lg transition-shadow"
      >
        <div class="card-body">
          <!-- Category Header -->
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center">
              <span class="text-2xl mr-3">{{ budget.category.icon }}</span>
              <div>
                <h3 class="font-semibold text-gray-900">{{ budget.category.name }}</h3>
                <p class="text-sm text-gray-500">${{ formatMoney(budget.amount) }} budget</p>
              </div>
            </div>
            <div class="flex gap-1">
              <button
                @click="editBudget(budget)"
                class="p-2 text-gray-600 hover:text-primary-600"
              >
                <PencilIcon class="w-4 h-4" />
              </button>
              <button
                @click="confirmDelete(budget)"
                class="p-2 text-gray-600 hover:text-danger-600"
              >
                <TrashIcon class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- Progress Section -->
          <div class="mb-4">
            <div class="flex justify-between text-sm mb-2">
              <span class="text-gray-600">Spent</span>
              <span class="font-semibold" :class="getSpentClass(budget)">
                ${{ formatMoney(budget.spent) }} / ${{ formatMoney(budget.amount) }}
              </span>
            </div>
            
            <div class="w-full bg-gray-200 rounded-full h-3">
              <div
                class="h-3 rounded-full transition-all duration-300"
                :class="getProgressClass(budget)"
                :style="{ width: Math.min(budget.percentage, 100) + '%' }"
              ></div>
            </div>
            
            <p class="text-sm mt-2" :class="getPercentageTextClass(budget)">
              {{ budget.percentage.toFixed(1) }}% of budget used
            </p>
          </div>

          <!-- Remaining Amount -->
          <div class="pt-3 border-t">
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-600">Remaining</span>
              <span class="font-semibold" :class="budget.remaining >= 0 ? 'text-success-600' : 'text-danger-600'">
                {{ budget.remaining >= 0 ? '' : '-' }}${{ formatMoney(Math.abs(budget.remaining)) }}
              </span>
            </div>
          </div>

          <!-- Alert Threshold -->
          <div v-if="budget.alertThreshold" class="mt-3 pt-3 border-t">
            <p class="text-xs text-gray-500">
              Alert at {{ budget.alertThreshold }}% spending
            </p>
          </div>
        </div>
      </div>

      <!-- Add Budget Card -->
      <div
        @click="showAddModal = true"
        class="card border-2 border-dashed border-gray-300 hover:border-primary-400 cursor-pointer hover:shadow-lg transition-all"
      >
        <div class="card-body flex flex-col items-center justify-center text-center py-12">
          <PlusCircleIcon class="w-12 h-12 text-gray-400 mb-3" />
          <h3 class="font-semibold text-gray-700">Add New Budget</h3>
          <p class="text-sm text-gray-500 mt-1">Set spending limit for a category</p>
        </div>
      </div>
    </div>

    <!-- Budget Modal -->
    <BudgetModal
      v-if="showAddModal || showEditModal"
      :budget="selectedBudget"
      :mode="showEditModal ? 'edit' : 'add'"
      :month="currentMonth"
      :year="currentYear"
      @close="closeModals"
      @saved="onBudgetSaved"
    />

    <!-- Delete Confirmation -->
    <ConfirmDialog
      v-if="showDeleteModal"
      title="Delete Budget"
      message="Are you sure you want to delete this budget? This action cannot be undone."
      confirmText="Delete"
      confirmClass="btn-danger"
      @confirm="deleteBudget"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  PlusIcon,
  PlusCircleIcon,
  PencilIcon,
  TrashIcon,
  ChevronLeftIcon,
  ChevronRightIcon
} from '@heroicons/vue/24/outline'
import BudgetModal from '@/components/BudgetModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useUserStore } from '@/stores/user'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

const userStore = useUserStore()
const toast = useToast()

// Data
const budgets = ref([])
const currentMonth = ref(new Date().getMonth() + 1)
const currentYear = ref(new Date().getFullYear())

// Modals
const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const selectedBudget = ref(null)

// Computed
const currentMonthName = computed(() => {
  const months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ]
  return months[currentMonth.value - 1]
})

const totalBudget = computed(() => {
  return budgets.value.reduce((sum, b) => sum + parseFloat(b.amount), 0)
})

// Methods
const formatMoney = (amount) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount)
}

const getSpentClass = (budget) => {
  if (budget.percentage >= 100) return 'text-danger-600'
  if (budget.percentage >= 80) return 'text-yellow-600'
  return 'text-gray-900'
}

const getProgressClass = (budget) => {
  if (budget.percentage >= 100) return 'bg-danger-500'
  if (budget.percentage >= 80) return 'bg-yellow-500'
  return 'bg-success-500'
}

const getPercentageTextClass = (budget) => {
  if (budget.percentage >= 100) return 'text-danger-600 font-semibold'
  if (budget.percentage >= 80) return 'text-yellow-600'
  return 'text-gray-600'
}

const loadBudgets = async () => {
  const userId = userStore.currentUser.id
  
  try {
    const response = await api.get(`/users/${userId}/budgets/monthly`, {
      params: {
        month: currentMonth.value,
        year: currentYear.value
      }
    })
    
    // Transform the response to include calculated fields
    budgets.value = response.data.map(item => ({
      id: item.budget.id,
      category: item.budget.category,
      amount: item.budget.amount,
      spent: item.spent || 0,
      remaining: item.remaining || item.budget.amount,
      percentage: item.percentage || 0,
      alertThreshold: item.budget.alertThreshold,
      notes: item.budget.notes
    }))
  } catch (error) {
    console.error('Failed to load budgets:', error)
    // Use mock data for demonstration
    budgets.value = [
      {
        id: 1,
        category: { id: 1, name: 'Food & Dining', icon: '🍔' },
        amount: 500,
        spent: 420,
        remaining: 80,
        percentage: 84,
        alertThreshold: 80
      },
      {
        id: 2,
        category: { id: 2, name: 'Transportation', icon: '🚗' },
        amount: 300,
        spent: 180,
        remaining: 120,
        percentage: 60,
        alertThreshold: 80
      }
    ]
  }
}

const previousMonth = () => {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
  loadBudgets()
}

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
  loadBudgets()
}

const editBudget = (budget) => {
  selectedBudget.value = budget
  showEditModal.value = true
}

const confirmDelete = (budget) => {
  selectedBudget.value = budget
  showDeleteModal.value = true
}

const deleteBudget = async () => {
  const userId = userStore.currentUser.id
  
  try {
    await api.delete(`/users/${userId}/budgets/${selectedBudget.value.id}`)
    toast.success('Budget deleted successfully')
    showDeleteModal.value = false
    loadBudgets()
  } catch (error) {
    toast.error('Failed to delete budget')
  }
}

const closeModals = () => {
  showAddModal.value = false
  showEditModal.value = false
  selectedBudget.value = null
}

const onBudgetSaved = () => {
  closeModals()
  loadBudgets()
}

onMounted(() => {
  loadBudgets()
})
</script>