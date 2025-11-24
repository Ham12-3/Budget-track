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
        <p class="text-2xl font-bold text-success-600">+$12,500</p>
        <p class="text-xs text-gray-500 mt-1">↑ 15% from last period</p>
      </div>
      
      <div class="stat-card">
        <p class="text-sm text-gray-600 mb-1">Total Expenses</p>
        <p class="text-2xl font-bold text-danger-600">-$8,350</p>
        <p class="text-xs text-gray-500 mt-1">↓ 5% from last period</p>
      </div>
      
      <div class="stat-card">
        <p class="text-sm text-gray-600 mb-1">Net Savings</p>
        <p class="text-2xl font-bold text-primary-600">$4,150</p>
        <p class="text-xs text-gray-500 mt-1">33.2% savings rate</p>
      </div>
      
      <div class="stat-card">
        <p class="text-sm text-gray-600 mb-1">Avg Daily Spend</p>
        <p class="text-2xl font-bold text-gray-900">$278</p>
        <p class="text-xs text-gray-500 mt-1">30 days average</p>
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
import { ref, onMounted } from 'vue'
import { ArrowDownTrayIcon } from '@heroicons/vue/24/outline'
import SpendingTrendChart from '@/components/charts/SpendingTrendChart.vue'

// Data
const reportType = ref('monthly')
const selectedPeriod = ref('current')

const topCategories = ref([
  { name: 'Food & Dining', icon: '🍔', amount: '2,150', percentage: 65 },
  { name: 'Transportation', icon: '🚗', amount: '1,800', percentage: 54 },
  { name: 'Shopping', icon: '🛍️', amount: '1,500', percentage: 45 },
  { name: 'Bills & Utilities', icon: '💡', amount: '1,200', percentage: 36 },
  { name: 'Entertainment', icon: '🎬', amount: '800', percentage: 24 }
])

const monthlyData = ref([
  { name: 'January', income: '8,500', expenses: '6,200', net: 2300, savingsRate: 27 },
  { name: 'February', income: '8,500', expenses: '5,800', net: 2700, savingsRate: 32 },
  { name: 'March', income: '9,200', expenses: '6,500', net: 2700, savingsRate: 29 },
  { name: 'April', income: '8,500', expenses: '6,100', net: 2400, savingsRate: 28 },
  { name: 'May', income: '8,500', expenses: '5,900', net: 2600, savingsRate: 31 },
  { name: 'June', income: '8,500', expenses: '6,350', net: 2150, savingsRate: 25 }
])

const chartData = ref({
  labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
  datasets: [
    {
      label: 'Income',
      data: [8500, 8500, 9200, 8500, 8500, 8500],
      borderColor: '#22c55e',
      backgroundColor: 'rgba(34, 197, 94, 0.1)'
    },
    {
      label: 'Expenses',
      data: [6200, 5800, 6500, 6100, 5900, 6350],
      borderColor: '#ef4444',
      backgroundColor: 'rgba(239, 68, 68, 0.1)'
    }
  ]
})

// Methods
const generateReport = () => {
  // Generate report based on selected type and period
  console.log('Generating report:', reportType.value, selectedPeriod.value)
}

const exportReport = () => {
  // Export report functionality
  console.log('Exporting report...')
}

onMounted(() => {
  generateReport()
})
</script>