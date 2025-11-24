<template>
  <div 
    v-if="show" 
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
    @click.self="$emit('cancel')"
  >
    <div class="bg-white rounded-lg max-w-sm w-full p-6">
      <!-- Icon -->
      <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-danger-100 mb-4">
        <ExclamationTriangleIcon class="h-6 w-6 text-danger-600" />
      </div>
      
      <!-- Title -->
      <h3 class="text-lg font-semibold text-gray-900 text-center mb-2">
        {{ title }}
      </h3>
      
      <!-- Message -->
      <p class="text-sm text-gray-500 text-center mb-6">
        {{ message }}
      </p>
      
      <!-- Actions -->
      <div class="flex gap-3">
        <button
          @click="$emit('cancel')"
          class="btn btn-secondary flex-1"
        >
          {{ cancelText }}
        </button>
        <button
          @click="$emit('confirm')"
          class="btn flex-1"
          :class="confirmClass || 'btn-primary'"
        >
          {{ confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ExclamationTriangleIcon } from '@heroicons/vue/24/outline'

defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    required: true
  },
  message: {
    type: String,
    required: true
  },
  confirmText: {
    type: String,
    default: 'Confirm'
  },
  cancelText: {
    type: String,
    default: 'Cancel'
  },
  confirmClass: {
    type: String,
    default: null
  }
})

defineEmits(['confirm', 'cancel'])
</script>