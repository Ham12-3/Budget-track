<template>
  <div class="dashboard">
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
          
          <div class="space-y-3">
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
          
          <div class="space-y-3">
            <BudgetAlert
              v-for="budget in budgetAlerts"
              :key="budget.id"
              :budget="budget"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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
const summary = ref({
  balance: 5250.00,
  totalIncome: 8500.00,
  totalExpenses: 3250.00
})

const recentTransactions = ref([
  {
    id: 1,
    amount: 120.50,
    description: 'Grocery Shopping',
    category: { name: 'Food & Dining', icon: '🍔', color: '#FF6384' },
    type: 'EXPENSE',
    transactionDate: new Date().toISOString()
  },
  {
    id: 2,
    amount: 3500.00,
    description: 'Monthly Salary',
    category: { name: 'Income', icon: '💵', color: '#32CD32' },
    type: 'INCOME',
    transactionDate: new Date().toISOString()
  },
  {
    id: 3,
    amount: 45.99,
    description: 'Netflix Subscription',
    category: { name: 'Entertainment', icon: '🎬', color: '#4BC0C0' },
    type: 'EXPENSE',
    transactionDate: new Date().toISOString()
  }
])

const budgetAlerts = ref([
  {
    id: 1,
    category: 'Food & Dining',
    budgetAmount: 500,
    spent: 420,
    percentage: 84
  },
  {
    id: 2,
    category: 'Transportation',
    budgetAmount: 300,
    spent: 180,
    percentage: 60
  },
  {
    id: 3,
    category: 'Entertainment',
    budgetAmount: 200,
    spent: 195,
    percentage: 97.5
  }
])

const spendingTrendData = ref({
  labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
  datasets: [
    {
      label: 'Income',
      data: [8500, 9200, 8800, 9500, 8500, 8500],
      borderColor: '#22c55e',
      backgroundColor: 'rgba(34, 197, 94, 0.1)'
    },
    {
      label: 'Expenses',
      data: [3200, 2800, 3500, 3100, 2900, 3250],
      borderColor: '#ef4444',
      backgroundColor: 'rgba(239, 68, 68, 0.1)'
    }
  ]
})

const categoryBreakdownData = ref({
  labels: ['Food', 'Transport', 'Shopping', 'Entertainment', 'Bills', 'Other'],
  datasets: [{
    data: [850, 450, 620, 195, 980, 155],
    backgroundColor: [
      '#FF6384',
      '#36A2EB',
      '#FFCE56',
      '#4BC0C0',
      '#9966FF',
      '#C9CBCF'
    ]
  }]
})

// Computed
const balanceChangePercent = computed(() => {
  // Mock calculation
  return 12.5
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
  }).format(amount)
}

// Load dashboard data
const loadDashboardData = async () => {
  const userId = userStore.currentUser.id
  const now = new Date()
  const month = now.getMonth() + 1
  const year = now.getFullYear()
  
  try {
    // Fetch monthly summary
    const response = await api.get(`/users/${userId}/transactions/summary/monthly`, {
      params: { month, year }
    })
    
    summary.value = {
      balance: response.data.balance || 0,
      totalIncome: response.data.totalIncome || 0,
      totalExpenses: response.data.totalExpenses || 0
    }
    
    // You can also fetch recent transactions and budget alerts here
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>