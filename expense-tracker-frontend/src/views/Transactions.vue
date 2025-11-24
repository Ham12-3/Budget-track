<template>
  <div class="transactions">
    <!-- Filters Section -->
    <div class="card mb-6">
      <div class="card-body">
        <div class="flex flex-wrap items-center gap-4">
          <!-- Date Range Filter -->
          <div>
            <label class="form-label">Date Range</label>
            <div class="flex gap-2">
              <input
                type="date"
                v-model="filters.startDate"
                class="form-input"
                @change="applyFilters"
              />
              <span class="self-center text-gray-500">to</span>
              <input
                type="date"
                v-model="filters.endDate"
                class="form-input"
                @change="applyFilters"
              />
            </div>
          </div>
          
          <!-- Category Filter -->
          <div>
            <label class="form-label">Category</label>
            <select
              v-model="filters.categoryId"
              class="form-select min-w-[150px]"
              @change="applyFilters"
            >
              <option :value="null">All Categories</option>
              <option
                v-for="category in categories"
                :key="category.id"
                :value="category.id"
              >
                {{ category.icon }} {{ category.name }}
              </option>
            </select>
          </div>
          
          <!-- Type Filter -->
          <div>
            <label class="form-label">Type</label>
            <select
              v-model="filters.type"
              class="form-select"
              @change="applyFilters"
            >
              <option :value="null">All Types</option>
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
            </select>
          </div>
          
          <!-- Search -->
          <div class="flex-1">
            <label class="form-label">Search</label>
            <input
              type="text"
              v-model="searchQuery"
              placeholder="Search transactions..."
              class="form-input"
              @input="debounceSearch"
            />
          </div>
          
          <!-- Clear Filters -->
          <div class="self-end">
            <button
              @click="clearFilters"
              class="btn btn-secondary"
            >
              Clear Filters
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Transactions List -->
    <div class="card">
      <div class="card-body">
        <!-- Header -->
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">
            Transactions ({{ totalTransactions }})
          </h2>
          <button
            @click="showAddModal = true"
            class="btn btn-primary flex items-center"
          >
            <PlusIcon class="w-5 h-5 mr-2" />
            Add Transaction
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="text-center py-8">
          <div class="inline-flex items-center">
            <svg class="animate-spin h-5 w-5 text-primary-600 mr-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Loading transactions...
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="transactions.length === 0" class="text-center py-8">
          <p class="text-gray-500">No transactions found. Start by adding your first transaction!</p>
        </div>

        <!-- Transactions Table -->
        <div v-else class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Description
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Amount
                </th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="transaction in transactions" :key="transaction.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatDate(transaction.transactionDate) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ transaction.description || '-' }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  <span class="inline-flex items-center">
                    <span class="mr-2">{{ transaction.category.icon }}</span>
                    {{ transaction.category.name }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                    :class="transaction.type === 'INCOME' ? 'bg-success-100 text-success-800' : 'bg-danger-100 text-danger-800'"
                  >
                    {{ transaction.type }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-right font-medium"
                    :class="transaction.type === 'INCOME' ? 'text-success-600' : 'text-danger-600'">
                  {{ transaction.type === 'INCOME' ? '+' : '-' }}${{ formatMoney(transaction.amount) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                  <button
                    @click="editTransaction(transaction)"
                    class="text-primary-600 hover:text-primary-900 mr-3"
                  >
                    <PencilIcon class="w-5 h-5" />
                  </button>
                  <button
                    @click="confirmDelete(transaction)"
                    class="text-danger-600 hover:text-danger-900"
                  >
                    <TrashIcon class="w-5 h-5" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="mt-4 flex items-center justify-between">
          <div class="text-sm text-gray-700">
            Showing {{ startItem }} to {{ endItem }} of {{ totalTransactions }} results
          </div>
          <div class="flex gap-2">
            <button
              @click="previousPage"
              :disabled="currentPage === 0"
              class="btn btn-secondary"
              :class="{ 'opacity-50 cursor-not-allowed': currentPage === 0 }"
            >
              Previous
            </button>
            <button
              v-for="page in visiblePages"
              :key="page"
              @click="goToPage(page - 1)"
              class="btn"
              :class="page - 1 === currentPage ? 'btn-primary' : 'btn-secondary'"
            >
              {{ page }}
            </button>
            <button
              @click="nextPage"
              :disabled="currentPage === totalPages - 1"
              class="btn btn-secondary"
              :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages - 1 }"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Transaction Modal -->
    <TransactionModal
      v-if="showAddModal || showEditModal"
      :transaction="selectedTransaction"
      :mode="showEditModal ? 'edit' : 'add'"
      @close="closeModals"
      @saved="onTransactionSaved"
    />

    <!-- Delete Confirmation Modal -->
    <ConfirmDialog
      v-if="showDeleteModal"
      title="Delete Transaction"
      message="Are you sure you want to delete this transaction? This action cannot be undone."
      confirmText="Delete"
      confirmClass="btn-danger"
      @confirm="deleteTransaction"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/vue/24/outline'
import { format } from 'date-fns'
import TransactionModal from '@/components/TransactionModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useTransactionsStore } from '@/stores/transactions'
import { useUserStore } from '@/stores/user'
import api from '@/services/api'

const transactionsStore = useTransactionsStore()
const userStore = useUserStore()

// Data
const transactions = ref([])
const categories = ref([])
const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(0)
const totalPages = ref(0)
const totalTransactions = ref(0)
const pageSize = 20

// Modals
const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const selectedTransaction = ref(null)

// Filters
const filters = ref({
  startDate: null,
  endDate: null,
  categoryId: null,
  type: null
})

// Computed
const startItem = computed(() => currentPage.value * pageSize + 1)
const endItem = computed(() => Math.min((currentPage.value + 1) * pageSize, totalTransactions.value))

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2) + 1)
  let end = Math.min(totalPages.value, start + maxVisible - 1)
  
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

// Methods
const formatDate = (date) => {
  return format(new Date(date), 'MMM dd, yyyy')
}

const formatMoney = (amount) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount)
}

const loadTransactions = async () => {
  loading.value = true
  const userId = userStore.currentUser.id
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize,
      sortBy: 'transactionDate',
      sortDirection: 'DESC',
      ...filters.value
    }
    
    const response = await api.get(`/users/${userId}/transactions`, { params })
    
    transactions.value = response.data.content
    currentPage.value = response.data.number
    totalPages.value = response.data.totalPages
    totalTransactions.value = response.data.totalElements
  } catch (error) {
    console.error('Failed to load transactions:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await api.get('/categories')
    categories.value = response.data
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

const applyFilters = () => {
  currentPage.value = 0
  loadTransactions()
}

const clearFilters = () => {
  filters.value = {
    startDate: null,
    endDate: null,
    categoryId: null,
    type: null
  }
  searchQuery.value = ''
  currentPage.value = 0
  loadTransactions()
}

const debounceSearch = () => {
  // Implement debounce logic here
  setTimeout(() => {
    applyFilters()
  }, 500)
}

const goToPage = (page) => {
  currentPage.value = page
  loadTransactions()
}

const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    loadTransactions()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    loadTransactions()
  }
}

const editTransaction = (transaction) => {
  selectedTransaction.value = transaction
  showEditModal.value = true
}

const confirmDelete = (transaction) => {
  selectedTransaction.value = transaction
  showDeleteModal.value = true
}

const deleteTransaction = async () => {
  const userId = userStore.currentUser.id
  
  try {
    await transactionsStore.deleteTransaction(userId, selectedTransaction.value.id)
    showDeleteModal.value = false
    loadTransactions()
  } catch (error) {
    console.error('Failed to delete transaction:', error)
  }
}

const closeModals = () => {
  showAddModal.value = false
  showEditModal.value = false
  selectedTransaction.value = null
}

const onTransactionSaved = () => {
  closeModals()
  loadTransactions()
}

onMounted(() => {
  loadCategories()
  loadTransactions()
})
</script>