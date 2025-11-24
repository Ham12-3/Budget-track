<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto" @click.self="closeModal">
    <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center">
      <!-- Background overlay -->
      <div class="fixed inset-0 transition-opacity bg-gray-500 bg-opacity-75"></div>

      <!-- Modal panel -->
      <div class="relative inline-block w-full max-w-md p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-2xl">
        <!-- Header -->
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-900">
            {{ isEdit ? 'Edit Category' : 'Add New Category' }}
          </h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-500">
            <XMarkIcon class="w-6 h-6" />
          </button>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleSubmit">
          <!-- Category Name -->
          <div class="mb-4">
            <label class="form-label">Category Name *</label>
            <input
              v-model="formData.name"
              type="text"
              required
              class="form-input"
              placeholder="e.g., Pet Expenses"
            />
          </div>

          <!-- Description -->
          <div class="mb-4">
            <label class="form-label">Description</label>
            <textarea
              v-model="formData.description"
              rows="2"
              class="form-input"
              placeholder="Brief description of this category"
            ></textarea>
          </div>

          <!-- Icon -->
          <div class="mb-4">
            <label class="form-label">Icon (Emoji) *</label>
            <div class="grid grid-cols-8 gap-2 mb-2">
              <button
                v-for="emoji in emojiOptions"
                :key="emoji"
                type="button"
                @click="formData.icon = emoji"
                class="w-10 h-10 text-2xl flex items-center justify-center rounded-lg hover:bg-gray-100 transition-colors"
                :class="formData.icon === emoji ? 'bg-primary-100 ring-2 ring-primary-500' : 'bg-gray-50'"
              >
                {{ emoji }}
              </button>
            </div>
            <input
              v-model="formData.icon"
              type="text"
              required
              maxlength="2"
              class="form-input text-center text-2xl"
              placeholder="Or type emoji"
            />
          </div>

          <!-- Color -->
          <div class="mb-6">
            <label class="form-label">Color *</label>
            <div class="grid grid-cols-8 gap-2 mb-2">
              <button
                v-for="color in colorOptions"
                :key="color"
                type="button"
                @click="formData.color = color"
                class="w-10 h-10 rounded-lg transition-all"
                :style="{ backgroundColor: color }"
                :class="formData.color === color ? 'ring-2 ring-offset-2 ring-gray-400 scale-110' : ''"
              ></button>
            </div>
            <input
              v-model="formData.color"
              type="color"
              class="w-full h-10 rounded-lg cursor-pointer"
            />
          </div>

          <!-- Actions -->
          <div class="flex gap-3">
            <button type="button" @click="closeModal" class="btn btn-secondary flex-1">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary flex-1" :disabled="loading">
              {{ loading ? 'Saving...' : (isEdit ? 'Update' : 'Create') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'

const props = defineProps({
  show: Boolean,
  category: Object
})

const emit = defineEmits(['close', 'save'])

// Form data
const formData = ref({
  name: '',
  description: '',
  icon: '📁',
  color: '#3B82F6'
})

const loading = ref(false)

// Emoji options
const emojiOptions = [
  '🍔', '🚗', '🛍️', '🎬', '💡', '🏥', '📚', '✈️',
  '💅', '💰', '💵', '📌', '🎮', '🏠', '🐕', '☕',
  '🎵', '💳', '🎓', '🏋️', '🍕', '🎁', '📱', '🌍'
]

// Color options
const colorOptions = [
  '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
  '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF',
  '#90EE90', '#32CD32', '#808080', '#FF1744',
  '#00BCD4', '#FFC107', '#4CAF50', '#9C27B0'
]

const isEdit = ref(false)

// Watch for category changes
watch(() => props.category, (newCategory) => {
  if (newCategory) {
    isEdit.value = true
    formData.value = {
      name: newCategory.name,
      description: newCategory.description || '',
      icon: newCategory.icon,
      color: newCategory.color
    }
  } else {
    isEdit.value = false
    resetForm()
  }
}, { immediate: true })

// Methods
const resetForm = () => {
  formData.value = {
    name: '',
    description: '',
    icon: '📁',
    color: '#3B82F6'
  }
}

const closeModal = () => {
  resetForm()
  emit('close')
}

const handleSubmit = async () => {
  loading.value = true
  try {
    emit('save', { ...formData.value })
  } finally {
    loading.value = false
  }
}
</script>