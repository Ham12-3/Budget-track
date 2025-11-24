<template>
  <div class="categories">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Categories</h1>
      <p class="text-gray-600 mt-1">Manage expense and income categories</p>
    </div>

    <!-- Category Tabs -->
    <div class="flex gap-4 mb-6">
      <button
        @click="activeTab = 'system'"
        class="px-4 py-2 font-medium rounded-lg transition-colors"
        :class="activeTab === 'system' ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-700'"
      >
        System Categories
      </button>
      <button
        @click="activeTab = 'custom'"
        class="px-4 py-2 font-medium rounded-lg transition-colors"
        :class="activeTab === 'custom' ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-700'"
      >
        Custom Categories
      </button>
    </div>

    <!-- Categories Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      <div
        v-for="category in displayedCategories"
        :key="category.id"
        class="card hover:shadow-lg transition-shadow"
      >
        <div class="card-body">
          <div class="flex items-center justify-between mb-3">
            <div
              class="w-12 h-12 rounded-lg flex items-center justify-center text-2xl"
              :style="{ backgroundColor: category.color + '20' }"
            >
              {{ category.icon }}
            </div>
            <div v-if="!category.isSystem" class="flex gap-1">
              <button
                @click="editCategory(category)"
                class="p-1 text-gray-600 hover:text-primary-600"
              >
                <PencilIcon class="w-4 h-4" />
              </button>
              <button
                @click="confirmDelete(category)"
                class="p-1 text-gray-600 hover:text-danger-600"
              >
                <TrashIcon class="w-4 h-4" />
              </button>
            </div>
          </div>
          
          <h3 class="font-semibold text-gray-900">{{ category.name }}</h3>
          <p class="text-sm text-gray-500 mt-1">{{ category.description }}</p>
          
          <div class="mt-3 pt-3 border-t">
            <span class="text-xs px-2 py-1 rounded-full" 
                  :class="category.isSystem ? 'bg-blue-100 text-blue-700' : 'bg-green-100 text-green-700'">
              {{ category.isSystem ? 'System' : 'Custom' }}
            </span>
          </div>
        </div>
      </div>

      <!-- Add Category Card (for custom tab) -->
      <div
        v-if="activeTab === 'custom'"
        @click="showAddModal = true"
        class="card border-2 border-dashed border-gray-300 hover:border-primary-400 cursor-pointer hover:shadow-lg transition-all"
      >
        <div class="card-body flex flex-col items-center justify-center text-center">
          <PlusCircleIcon class="w-12 h-12 text-gray-400 mb-3" />
          <h3 class="font-semibold text-gray-700">Add Category</h3>
          <p class="text-sm text-gray-500 mt-1">Create custom category</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  PlusCircleIcon,
  PencilIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'
import api from '@/services/api'

// Data
const categories = ref([])
const activeTab = ref('system')
const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const selectedCategory = ref(null)

// Computed
const displayedCategories = computed(() => {
  return categories.value.filter(c => 
    activeTab.value === 'system' ? c.isSystem : !c.isSystem
  )
})

// Methods
const loadCategories = async () => {
  try {
    const response = await api.get('/categories')
    categories.value = response.data
  } catch (error) {
    // Use mock data for demonstration
    categories.value = [
      { id: 1, name: 'Food & Dining', description: 'Restaurants and groceries', icon: '🍔', color: '#FF6384', isSystem: true },
      { id: 2, name: 'Transportation', description: 'Gas, public transit, parking', icon: '🚗', color: '#36A2EB', isSystem: true },
      { id: 3, name: 'Shopping', description: 'Clothing and household items', icon: '🛍️', color: '#FFCE56', isSystem: true },
      { id: 4, name: 'Entertainment', description: 'Movies, games, subscriptions', icon: '🎬', color: '#4BC0C0', isSystem: true },
      { id: 5, name: 'Custom Category', description: 'User created category', icon: '📌', color: '#9966FF', isSystem: false }
    ]
  }
}

const editCategory = (category) => {
  selectedCategory.value = category
  showEditModal.value = true
}

const confirmDelete = (category) => {
  selectedCategory.value = category
  showDeleteModal.value = true
}

onMounted(() => {
  loadCategories()
})
</script>