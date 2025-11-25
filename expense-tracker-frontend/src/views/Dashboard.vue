<template>
  <div class="dashboard">
    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
    </div>

    <template v-else>
      <!-- Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Total Balance Card -->
        <div class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">Total Balance</p>
              <p class="text-2xl font-bold text-gray-900">
                ${{ formatMoney(summary.balance) }}
              </p>
              <p class="text-sm text-gray-500 mt-1">
                <span :class="balanceChangeClass">{{ balanceChangePercent }}%</span>
                vs last month
              </p>
            </div>
            <div class="rounded-full p-3 bg-primary-100">
              <BanknotesIcon class="w-8 h-8 text-primary-600" />
            </div>
          </div>
        </div>

        <!-- Income Card -->
        <div class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">Income</p>
              <p class="text-2xl font-bold text-success-700">
                +${{ formatMoney(summary.totalIncome) }}
              </p>
              <p class="text-sm text-gray-500 mt-1">This month</p>
            </div>
            <div class="rounded-full p-3 bg-success-50">
              <ArrowTrendingUpIcon class="w-8 h-8 text-success-500" />
            </div>
          </div>
        </div>

        <!-- Expenses Card -->
        <div class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">Expenses</p>
              <p class="text-2xl font-bold text-danger-700">
                -${{ formatMoney(summary.totalExpenses) }}
              </p>
              <p class="text-sm text-gray-500 mt-1">This month</p>
            </div>
            <div class="rounded-full p-3 bg-danger-50">
              <ArrowTrendingDownIcon class="w-8 h-8 text-danger-500" />
            </div>
          </div>
        </div>

        <!-- Savings Rate Card -->
        <div class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 mb-1">Savings Rate</p>
              <p class="text-2xl font-bold text-gray-900">{{ savingsRate }}%</p>
              <p class="text-sm text-gray-500 mt-1">Of income</p>
            </div>
            <div class="rounded-full p-3 bg-purple-100">
              <ChartPieIcon class="w-8 h-8 text-purple-600" />
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <!-- Spending Trend Chart -->
        <div class="card">
          <div class="card-body">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Spending Trend</h2>
            <SpendingTrendChart :data="spendingTrendData" />
          </div>
        </div>

        <!-- Category Breakdown Chart -->
        <div class="card">
          <div class="card-body">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Category Breakdown</h2>
            <CategoryBreakdownChart :data="categoryBreakdownData" />
          </div>
        </div>
      </div>

      <!-- Recent Transactions and Budget Alerts -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Recent Transactions -->
        <div class="card">
          <div class="card-body">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-semibold text-gray-900">Recent Transactions</h2>
              <router-link to="/transactions" class="text-primary-600 hover:text-primary-700 text-sm font-medium">
                View all →
              </router-link>
            </div>
            
            <div v-if="recentTransactions.length === 0" class="text-center py-8 text-gray-500">
              <p>No transactions yet</p>
              <router-link to="/transactions" class="text-primary-600 hover:text-primary-700 text-sm font-medium mt-2 inline-block">
                Add your first transaction →
              </router-link>
            </div>
            <div v-else class="space-y-3">
              <TransactionItem
                v-for="transaction in recentTransactions"
                :key="transaction.id"
                :transaction="transaction"
                compact
              />
            </div>
          </div>
        </div>

        <!-- Budget Alerts -->
        <div class="card">
          <div class="card-body">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-semibold text-gray-900">Budget Status</h2>
              <router-link to="/budgets" class="text-primary-600 hover:text-primary-700 text-sm font-medium">
                Manage →
              </router-link>
            </div>
            
            <div v-if="budgetAlerts.length === 0" class="text-center py-8 text-gray-500">
              <p>No budgets set</p>
              <router-link to="/budgets" class="text-primary-600 hover:text-primary-700 text-sm font-medium mt-2 inline-block">
                Set up budgets →
              </router-link>
            </div>
            <div v-else class="space-y-3">
              <BudgetAlert
                v-for="budget in budgetAlerts"
                :key="budget.id"
                :budget="budget"
              />
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onActivated } from 'vue'
import {
  BanknotesIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  ChartPieIcon
} from '@heroicons/vue/24/outline'
import SpendingTrendChart from '@/components/charts/SpendingTrendChart.vue'
import CategoryBreakdownChart from '@/components/charts/CategoryBreakdownChart.vue'
import TransactionItem from '@/components/TransactionItem.vue'
import BudgetAlert from '@/components/BudgetAlert.vue'
import { useTransactionsStore } from '@/stores/transactions'
import { useUserStore } from '@/stores/user'
import api from '@/services/api'

const transactionsStore = useTransactionsStore()
const userStore = useUserStore()

// Data
const loading = ref(false)
const summary = ref({
  balance: 0,
  totalIncome: 0,
  totalExpenses: 0
})

const recentTransactions = ref([])
const budgetAlerts = ref([])
const spendingTrendData = ref({
  labels: [],
  datasets: []
})
const categoryBreakdownData = ref({
  labels: [],
  datasets: []
})

// Computed
const balanceChangePercent = computed(() => {
  return summary.value.balanceChange || 0
})

const balanceChangeClass = computed(() => {
  return balanceChangePercent.value >= 0 ? 'text-success-600' : 'text-danger-600'
})

const savingsRate = computed(() => {
  if (summary.value.totalIncome === 0) return 0
  const savings = summary.value.totalIncome - summary.value.totalExpenses
  return Math.round((savings / summary.value.totalIncome) * 100)
})

// Methods
const formatMoney = (amount) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount || 0)
}

// Load dashboard data
const loadDashboardData = async () => {
  loading.value = true
  const userId = userStore.currentUser.id
  const now = new Date()
  const month = now.getMonth() + 1
  const year = now.getFullYear()
  
  try {
    // Fetch monthly summary
    const summaryResponse = await api.get(`/users/${userId}/transactions/summary/monthly`, {
      params: { month, year }
    })
    
    summary.value = {
      balance: summaryResponse.data.balance || 0,
      totalIncome: summaryResponse.data.totalIncome || 0,
      totalExpenses: summaryResponse.data.totalExpenses || 0,
      balanceChange: summaryResponse.data.balanceChange || 0
    }
    
    // Fetch recent transactions (last 5)
    const transactionsResponse = await api.get(`/users/${userId}/transactions`, {
      params: { page: 0, size: 5, sortBy: 'transactionDate', sortDirection: 'DESC' }
    })
    recentTransactions.value = transactionsResponse.data.content || []
    
    // Fetch budget alerts
    const budgetsResponse = await api.get(`/users/${userId}/budgets/alerts`)
    budgetAlerts.value = budgetsResponse.data || []
    
    // Fetch spending trend data (last 6 months)
    const trendResponse = await api.get(`/users/${userId}/transactions/summary/yearly`, {
      params: { year }
    })
    
    // Process trend data for chart
    if (trendResponse.data && trendResponse.data.monthlyData) {
      const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
      const last6Months = []
      const incomeData = []
      const expenseData = []
      
      for (let i = 5; i >= 0; i--) {
        const date = new Date()
        date.setMonth(date.getMonth() - i)
        const monthIndex = date.getMonth()
        last6Months.push(months[monthIndex])
        
        const monthData = trendResponse.data.monthlyData.find(m => m.month === monthIndex + 1)
        incomeData.push(monthData?.income || 0)
        expenseData.push(monthData?.expenses || 0)
      }
      
      spendingTrendData.value = {
        labels: last6Months,
        datasets: [
          {
            label: 'Income',
            data: incomeData,
            borderColor: '#22c55e',
            backgroundColor: 'rgba(34, 197, 94, 0.1)'
          },
          {
            label: 'Expenses',
            data: expenseData,
            borderColor: '#ef4444',
            backgroundColor: 'rgba(239, 68, 68, 0.1)'
          }
        ]
      }
    }
    
    // Fetch category breakdown
    const categoryResponse = await api.get(`/users/${userId}/transactions`, {
      params: { 
        startDate: `${year}-${month.toString().padStart(2, '0')}-01`,
        endDate: new Date(year, month, 0).toISOString().split('T')[0],
        type: 'EXPENSE'
      }
    })
    
    // Group by category
    const categoryMap = {}
    categoryResponse.data.content?.forEach(transaction => {
      const catName = transaction.category?.name || 'Other'
      const catColor = transaction.category?.color || '#C9CBCF'
      if (!categoryMap[catName]) {
        categoryMap[catName] = { amount: 0, color: catColor }
      }
      categoryMap[catName].amount += parseFloat(transaction.amount)
    })
    
    categoryBreakdownData.value = {
      labels: Object.keys(categoryMap),
      datasets: [{
        data: Object.values(categoryMap).map(c => c.amount),
        backgroundColor: Object.values(categoryMap).map(c => c.color)
      }]
    }
    
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  } finally {
    loading.value = false
  }
}

// Lifecycle hooks
onMounted(() => {
  loadDashboardData()
})

// Reload data when component is activated (navigated back to)
onActivated(() => {
  loadDashboardData()
})
</script>