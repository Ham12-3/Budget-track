<template>
  <div class="transaction-item flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
    <!-- Left side - Category and Description -->
    <div class="flex items-center space-x-3">
      <div
        class="w-10 h-10 rounded-full flex items-center justify-center text-xl"
        :style="{ backgroundColor: transaction.category.color + '20' }"
      >
        {{ transaction.category.icon }}
      </div>
      <div>
        <p class="font-medium text-gray-900">
          {{ transaction.description || transaction.category.name }}
        </p>
        <p class="text-sm text-gray-500">
          {{ formatDate(transaction.transactionDate) }}
          <span v-if="!compact"> • {{ transaction.category.name }}</span>
        </p>
      </div>
    </div>
    
    <!-- Right side - Amount -->
    <div class="text-right">
      <p
        class="font-semibold"
        :class="transaction.type === 'INCOME' ? 'text-success-600' : 'text-danger-600'"
      >
        {{ transaction.type === 'INCOME' ? '+' : '-' }}${{ formatMoney(transaction.amount) }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { format } from 'date-fns'

defineProps({
  transaction: {
    type: Object,
    required: true
  },
  compact: {
    type: Boolean,
    default: false
  }
})

const formatDate = (date) => {
  return format(new Date(date), 'MMM dd, yyyy')
}

const formatMoney = (amount) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount)
}
</script>