<template>
  <div class="categories">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Categories</h1>
      <p class="text-gray-600 mt-1">Manage expense and income categories</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
    </div>

    <template v-else>
      <!-- Category Tabs -->
      <div class="flex gap-4 mb-6">
        <button
          @click="activeTab = 'system'"
          class="px-4 py-2 font-medium rounded-lg transition-colors"
          :class="activeTab === 'system' ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-700'"
        >
          System Categories ({{ systemCount }})
        </button>
        <button
          @click="activeTab = 'custom'"
          class="px-4 py-2 font-medium rounded-lg transition-colors"
          :class="activeTab === 'custom' ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-700'"
        >
          Custom Categories ({{ customCount }})
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
                  class="p-1 text-gray-600 hover:text-primary-600 transition-colors"
                  title="Edit category"
                >
                  <PencilIcon class="w-4 h-4" />
                </button>
                <button
                  @click="confirmDelete(category)"
                  class="p-1 text-gray-600 hover:text-danger-600 transition-colors"
                  title="Delete category"
                >
                  <TrashIcon class="w-4 h-4" />
                </button>
              </div>
            </div>
            
            <h3 class="font-semibold text-gray-900">{{ category.name }}</h3>
            <p class="text-sm text-gray-500 mt-1 line-clamp-2">{{ category.description }}</p>
            
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
          @click="openAddModal"
          class="card border-2 border-dashed border-gray-300 hover:border-primary-400 cursor-pointer hover:shadow-lg transition-all"
        >
          <div class="card-body flex flex-col items-center justify-center text-center min-h-[200px]">
            <PlusCircleIcon class="w-12 h-12 text-gray-400 mb-3" />
            <h3 class="font-semibold text-gray-700">Add Category</h3>
            <p class="text-sm text-gray-500 mt-1">Create custom category</p>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="displayedCategories.length === 0 && activeTab === 'custom'" class="text-center py-12">
        <div class="text-gray-400 text-6xl mb-4">📂</div>
        <h3 class="text-lg font-semibold text-gray-700 mb-2">No Custom Categories Yet</h3>
        <p class="text-gray-500 mb-4">Create your first custom category to get started</p>
        <button @click="openAddModal" class="btn btn-primary">
          <PlusCircleIcon class="w-5 h-5 inline-block mr-2" />
          Add Category
        </button>
      </div>
    </template>

    <!-- Category Modal -->
    <CategoryModal
      :show="showModal"
      :category="selectedCategory"
      @close="closeModal"
      @save="handleSave"
    />

    <!-- Delete Confirmation -->
    <ConfirmDialog
      :show="showDeleteDialog"
      title="Delete Category"
      :message="`Are you sure you want to delete '${selectedCategory?.name}'? This action cannot be undone.`"
      confirm-text="Delete"
      confirm-class="btn-danger"
      @confirm="handleDelete"
      @cancel="showDeleteDialog = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import {
  PlusCircleIcon,
  PencilIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'
import api from '@/services/api'
import CategoryModal from '@/components/CategoryModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

const toast = useToast()

// Data
const categories = ref([])
const activeTab = ref('system')
const showModal = ref(false)
const showDeleteDialog = ref(false)
const selectedCategory = ref(null)
const loading = ref(false)

// Computed
const displayedCategories = computed(() => {
  return categories.value.filter(c => 
    activeTab.value === 'system' ? c.isSystem : !c.isSystem
  )
})

const systemCount = computed(() => 
  categories.value.filter(c => c.isSystem).length
)

const customCount = computed(() => 
  categories.value.filter(c => !c.isSystem).length
)

// Methods
const loadCategories = async () => {
  loading.value = true
  try {
    const response = await api.get('/categories')
    categories.value = response.data
  } catch (error) {
    toast.error('Failed to load categories: ' + error.message)
    console.error('Error loading categories:', error)
  } finally {
    loading.value = false
  }
}

const openAddModal = () => {
  selectedCategory.value = null
  showModal.value = true
}

const editCategory = (category) => {
  selectedCategory.value = category
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedCategory.value = null
}

const handleSave = async (categoryData) => {
  try {
    if (selectedCategory.value) {
      // Update existing category
      await api.put(`/categories/${selectedCategory.value.id}`, categoryData)
      toast.success('Category updated successfully!')
    } else {
      // Create new category
      await api.post('/categories', categoryData)
      toast.success('Category created successfully!')
    }
    
    await loadCategories()
    closeModal()
  } catch (error) {
    toast.error('Failed to save category: ' + error.message)
    console.error('Error saving category:', error)
  }
}

const confirmDelete = (category) => {
  selectedCategory.value = category
  showDeleteDialog.value = true
}

const handleDelete = async () => {
  try {
    await api.delete(`/categories/${selectedCategory.value.id}`)
    toast.success('Category deleted successfully!')
    await loadCategories()
    showDeleteDialog.value = false
    selectedCategory.value = null
  } catch (error) {
    toast.error('Failed to delete category: ' + error.message)
    console.error('Error deleting category:', error)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>