<template>
  <div class="budget-alert p-3 rounded-lg border" :class="alertClass">
    <div class="flex items-center justify-between mb-2">
      <span class="font-medium text-gray-900">{{ budget.category }}</span>
      <span class="text-sm" :class="percentageClass">
        {{ budget.percentage.toFixed(1) }}% used
      </span>
    </div>
    
    <!-- Progress bar -->
    <div class="w-full bg-gray-200 rounded-full h-2 mb-2">
      <div
        class="h-2 rounded-full transition-all duration-300"
        :class="progressBarClass"
        :style="{ width: Math.min(budget.percentage, 100) + '%' }"
      ></div>
    </div>
    
    <!-- Amount details -->
    <div class="flex justify-between text-sm text-gray-600">
      <span>${{ formatMoney(budget.spent) }} spent</span>
      <span>${{ formatMoney(budget.budgetAmount) }} budget</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  budget: {
    type: Object,
    required: true
  }
})

const alertClass = computed(() => {
  if (props.budget.percentage >= 100) {
    return 'border-danger-200 bg-danger-50'
  } else if (props.budget.percentage >= 80) {
    return 'border-yellow-200 bg-yellow-50'
  }
  return 'border-gray-200'
})

const progressBarClass = computed(() => {
  if (props.budget.percentage >= 100) {
    return 'bg-danger-500'
  } else if (props.budget.percentage >= 80) {
    return 'bg-yellow-500'
  }
  return 'bg-success-500'
})

const percentageClass = computed(() => {
  if (props.budget.percentage >= 100) {
    return 'text-danger-600 font-semibold'
  } else if (props.budget.percentage >= 80) {
    return 'text-yellow-600'
  }
  return 'text-gray-600'
})

const formatMoney = (amount) => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount)
}
</script>