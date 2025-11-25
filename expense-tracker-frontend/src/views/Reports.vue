<template>
  <div class="reports">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Financial Reports</h1>
      <p class="text-gray-600 mt-1">Detailed insights and analytics</p>
    </div>

    <!-- Report Type Selector -->
    <div class="card mb-6">
      <div class="card-body">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="form-label">Report Type</label>
            <select v-model="reportType" class="form-select" @change="generateReport">
              <option value="monthly">Monthly Summary</option>
              <option value="yearly">Yearly Overview</option>
              <option value="category">Category Analysis</option>
              <option value="trend">Spending Trends</option>
            </select>
          </div>
          
          <div>
            <label class="form-label">Time Period</label>
            <select v-model="selectedPeriod" class="form-select" @change="generateReport">
              <option value="current">Current Month</option>
              <option value="last3">Last 3 Months</option>
              <option value="last6">Last 6 Months</option>
              <option value="year">This Year</option>
              <option value="custom">Custom Range</option>
            </select>
          </div>
          
          <div class="self-end">
            <button @click="exportReport" class="btn btn-primary w-full">
              <ArrowDownTrayIcon class="w-5 h-5 mr-2" />
              Export Report
            </button>
          </div>
        </div>
      </div>
    </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <div class="stat-card">
        <p class="text-sm text-gray-600 mb-1">Total Income</p>
        <p class="text-2xl font-bold text-success-600">+${{ formatMoney(summary.totalIncome) }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ selectedPeriodLabel }}</p>
      </div>
      
      <div class="stat-card">
        <p class="text-sm text-gray-600 mb-1">Total Expenses</p>
        <p class="text-2xl font-bold text-danger-600">-${{ formatMoney(summary.totalExpenses) }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ selectedPeriodLabel }}</p>
      </div>
      
      <div class="stat-card">
        <p class="text-sm text-gray-600 mb-1">Net Savings</p>
        <p class="text-2xl font-bold text-primary-600">${{ formatMoney(summary.netSavings) }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ summary.savingsRate }}% savings rate</p>
      </div>
      
      <div class="stat-card">
        <p class="text-sm text-gray-600 mb-1">Avg Daily Spend</p>
        <p class="text-2xl font-bold text-gray-900">${{ formatMoney(summary.avgDailySpend) }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ summary.daysCount }} days average</p>
      </div>
    </div>
    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
      <!-- Income vs Expenses Chart -->
      <div class="card">
        <div class="card-body">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Income vs Expenses</h2>
          <div class="h-64 flex items-center justify-center text-gray-500">
            <SpendingTrendChart v-if="chartData" :data="chartData" />
          </div>
        </div>
      </div>

      <!-- Top Categories -->
      <div class="card">
        <div class="card-body">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Top Spending Categories</h2>
          <div class="space-y-3">
            <div v-for="cat in topCategories" :key="cat.name" class="flex items-center justify-between">
              <div class="flex items-center flex-1">
                <span class="text-xl mr-3">{{ cat.icon }}</span>
                <div class="flex-1">
                  <div class="flex justify-between mb-1">
                    <span class="text-sm font-medium">{{ cat.name }}</span>
                    <span class="text-sm text-gray-600">${{ cat.amount }}</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div
                      class="bg-primary-600 h-2 rounded-full"
                      :style="{ width: cat.percentage + '%' }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Detailed Table -->
    <div class="card">
      <div class="card-body">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Monthly Breakdown</h2>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Month</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Income</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Expenses</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Net</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Savings Rate</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="month in monthlyData" :key="month.name">
                <td class="px-6 py-4 text-sm text-gray-900">{{ month.name }}</td>
                <td class="px-6 py-4 text-sm text-right text-success-600">+${{ month.income }}</td>
                <td class="px-6 py-4 text-sm text-right text-danger-600">-${{ month.expenses }}</td>
                <td class="px-6 py-4 text-sm text-right font-medium"
                    :class="month.net >= 0 ? 'text-success-600' : 'text-danger-600'">
                  {{ month.net >= 0 ? '+' : '' }}${{ month.net }}
                </td>
                <td class="px-6 py-4 text-sm text-right">{{ month.savingsRate }}%</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowDownTrayIcon } from '@heroicons/vue/24/outline'
import SpendingTrendChart from '@/components/charts/SpendingTrendChart.vue'
import { useUserStore } from '@/stores/user'
import { useToast } from 'vue-toastification'
import api from '@/services/api'

const userStore = useUserStore()
const toast = useToast()

// Data
const loading = ref(false)
const reportType = ref('monthly')
const selectedPeriod = ref('current')

const summary = ref({
  totalIncome: 0,
  totalExpenses: 0,
  netSavings: 0,
  savingsRate: 0,
  avgDailySpend: 0,
  daysCount: 30
})

const topCategories = ref([])
const monthlyData = ref([])
const chartData = ref({
  labels: [],
  datasets: []
})

// Computed
const selectedPeriodLabel = computed(() => {
  const labels = {
    current: 'This month',
    last3: 'Last 3 months',
    last6: 'Last 6 months',
    year: 'This year'
  }
  return labels[selectedPeriod.value]
})

// Methods
const formatMoney = (amount) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount || 0)
}

const getDateRange = () => {
  const now = new Date()
  let startDate, endDate = new Date()
  
  switch (selectedPeriod.value) {
    case 'current':
      startDate = new Date(now.getFullYear(), now.getMonth(), 1)
      break
    case 'last3':
      startDate = new Date(now.getFullYear(), now.getMonth() - 3, 1)
      break
    case 'last6':
      startDate = new Date(now.getFullYear(), now.getMonth() - 6, 1)
      break
    case 'year':
      startDate = new Date(now.getFullYear(), 0, 1)
      break
    default:
      startDate = new Date(now.getFullYear(), now.getMonth(), 1)
  }
  
  return {
    startDate: startDate.toISOString().split('T')[0],
    endDate: endDate.toISOString().split('T')[0]
  }
}

const generateReport = async () => {
  loading.value = true
  const userId = userStore.currentUser.id
  const { startDate, endDate } = getDateRange()
  
  try {
    // Fetch all transactions for the period
    const transactionsResponse = await api.get(`/users/${userId}/transactions`, {
      params: {
        startDate,
        endDate,
        page: 0,
        size: 1000,
        sortBy: 'transactionDate',
        sortDirection: 'ASC'
      }
    })
    
    const transactions = transactionsResponse.data.content || []
    
    // Calculate summary
    const totalIncome = transactions
      .filter(t => t.type === 'INCOME')
      .reduce((sum, t) => sum + parseFloat(t.amount), 0)
    
    const totalExpenses = transactions
      .filter(t => t.type === 'EXPENSE')
      .reduce((sum, t) => sum + parseFloat(t.amount), 0)
    
    const netSavings = totalIncome - totalExpenses
    const savingsRate = totalIncome > 0 ? Math.round((netSavings / totalIncome) * 100) : 0
    
    const start = new Date(startDate)
    const end = new Date(endDate)
    const daysCount = Math.ceil((end - start) / (1000 * 60 * 60 * 24))
    const avgDailySpend = daysCount > 0 ? totalExpenses / daysCount : 0
    
    summary.value = {
      totalIncome,
      totalExpenses,
      netSavings,
      savingsRate,
      avgDailySpend,
      daysCount
    }
    
    // Calculate top categories
    const categoryMap = {}
    transactions
      .filter(t => t.type === 'EXPENSE')
      .forEach(t => {
        const catName = t.category?.name || 'Other'
        if (!categoryMap[catName]) {
          categoryMap[catName] = {
            name: catName,
            icon: t.category?.icon || '📌',
            amount: 0
          }
        }
        categoryMap[catName].amount += parseFloat(t.amount)
      })
    
    const categoriesArray = Object.values(categoryMap)
      .sort((a, b) => b.amount - a.amount)
      .slice(0, 5)
    
    const maxAmount = categoriesArray[0]?.amount || 1
    topCategories.value = categoriesArray.map(cat => ({
      ...cat,
      percentage: Math.round((cat.amount / maxAmount) * 100)
    }))
    
    // Generate monthly breakdown
    const monthlyMap = {}
    transactions.forEach(t => {
      const date = new Date(t.transactionDate)
      const monthKey = `${date.getFullYear()}-${date.getMonth()}`
      const monthName = date.toLocaleString('default', { month: 'long', year: 'numeric' })
      
      if (!monthlyMap[monthKey]) {
        monthlyMap[monthKey] = {
          name: monthName,
          income: 0,
          expenses: 0,
          date: date
        }
      }
      
      if (t.type === 'INCOME') {
        monthlyMap[monthKey].income += parseFloat(t.amount)
      } else {
        monthlyMap[monthKey].expenses += parseFloat(t.amount)
      }
    })
    
    monthlyData.value = Object.values(monthlyMap)
      .sort((a, b) => a.date - b.date)
      .map(m => {
        const net = m.income - m.expenses
        const savingsRate = m.income > 0 ? Math.round((net / m.income) * 100) : 0
        return {
          name: m.name,
          income: m.income,
          expenses: m.expenses,
          net,
          savingsRate
        }
      })
    
    // Generate chart data
    const chartLabels = monthlyData.value.map(m => {
      const parts = m.name.split(' ')
      return parts[0].substring(0, 3)
    })
    
    chartData.value = {
      labels: chartLabels,
      datasets: [
        {
          label: 'Income',
          data: monthlyData.value.map(m => m.income),
          borderColor: '#22c55e',
          backgroundColor: 'rgba(34, 197, 94, 0.1)'
        },
        {
          label: 'Expenses',
          data: monthlyData.value.map(m => m.expenses),
          borderColor: '#ef4444',
          backgroundColor: 'rgba(239, 68, 68, 0.1)'
        }
      ]
    }
    
  } catch (error) {
    console.error('Failed to generate report:', error)
    toast.error('Failed to load report data')
  } finally {
    loading.value = false
  }
}

const exportReport = () => {
  const csvData = [
    ['Month', 'Income', 'Expenses', 'Net Savings', 'Savings Rate'],
    ...monthlyData.value.map(m => [
      m.name,
      m.income.toFixed(2),
      m.expenses.toFixed(2),
      m.net.toFixed(2),
      m.savingsRate + '%'
    ])
  ]
  
  const csvContent = csvData.map(row => row.join(',')).join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `financial-report-${new Date().toISOString().split('T')[0]}.csv`
  link.click()
  window.URL.revokeObjectURL(url)
  
  toast.success('Report exported successfully!')
}

onMounted(() => {
  generateReport()
})
</script>