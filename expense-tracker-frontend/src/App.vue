<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- Navigation Sidebar -->
    <aside class="fixed top-0 left-0 z-40 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0">
      <div class="h-full px-3 py-4 overflow-y-auto bg-white border-r border-gray-200">
        <!-- Logo -->
        <div class="flex items-center mb-8 px-2">
          <span class="text-2xl font-bold text-primary-600">💰 ExpenseTracker</span>
        </div>
        
        <!-- Navigation Menu -->
        <nav>
          <ul class="space-y-2">
            <li>
              <router-link
                to="/"
                class="nav-link"
                :class="{ 'nav-link-active': $route.name === 'Dashboard' }"
              >
                <HomeIcon class="w-5 h-5" />
                <span class="ml-3">Dashboard</span>
              </router-link>
            </li>
            <li>
              <router-link
                to="/transactions"
                class="nav-link"
                :class="{ 'nav-link-active': $route.name === 'Transactions' }"
              >
                <CreditCardIcon class="w-5 h-5" />
                <span class="ml-3">Transactions</span>
              </router-link>
            </li>
            <li>
              <router-link
                to="/budgets"
                class="nav-link"
                :class="{ 'nav-link-active': $route.name === 'Budgets' }"
              >
                <CalculatorIcon class="w-5 h-5" />
                <span class="ml-3">Budgets</span>
              </router-link>
            </li>
            <li>
              <router-link
                to="/categories"
                class="nav-link"
                :class="{ 'nav-link-active': $route.name === 'Categories' }"
              >
                <TagIcon class="w-5 h-5" />
                <span class="ml-3">Categories</span>
              </router-link>
            </li>
            <li>
              <router-link
                to="/reports"
                class="nav-link"
                :class="{ 'nav-link-active': $route.name === 'Reports' }"
              >
                <ChartBarIcon class="w-5 h-5" />
                <span class="ml-3">Reports</span>
              </router-link>
            </li>
            <li>
              <router-link
                to="/settings"
                class="nav-link"
                :class="{ 'nav-link-active': $route.name === 'Settings' }"
              >
                <CogIcon class="w-5 h-5" />
                <span class="ml-3">Settings</span>
              </router-link>
            </li>
          </ul>
        </nav>
        
        <!-- User Profile -->
        <div class="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200 bg-white">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-10 h-10 rounded-full bg-primary-600 flex items-center justify-center text-white font-semibold">
                {{ userInitials }}
              </div>
            </div>
            <div class="ml-3">
              <p class="text-sm font-medium text-gray-900">{{ currentUser.name }}</p>
              <p class="text-xs text-gray-500">{{ currentUser.email }}</p>
            </div>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="sm:ml-64">
      <!-- Top Bar -->
      <header class="bg-white shadow-sm border-b border-gray-200">
        <div class="px-4 sm:px-6 lg:px-8 py-4">
          <div class="flex items-center justify-between">
            <h1 class="text-2xl font-semibold text-gray-900">{{ $route.meta.title }}</h1>
            
            <!-- Quick Actions -->
            <div class="flex items-center space-x-4">
              <button
                @click="showAddTransaction = true"
                class="btn btn-primary flex items-center"
              >
                <PlusIcon class="w-5 h-5 mr-2" />
                Add Transaction
              </button>
              
              <!-- Notifications -->
              <button class="relative p-2 text-gray-600 hover:text-gray-900 focus:outline-none">
                <BellIcon class="w-6 h-6" />
                <span v-if="notifications > 0" class="absolute top-0 right-0 block h-2 w-2 rounded-full bg-danger-500"></span>
              </button>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="p-4 sm:p-6 lg:p-8">
        <router-view />
      </main>
    </div>
    
    <!-- Add Transaction Modal -->
    <TransactionModal v-if="showAddTransaction" @close="showAddTransaction = false" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  HomeIcon,
  CreditCardIcon,
  CalculatorIcon,
  TagIcon,
  ChartBarIcon,
  CogIcon,
  BellIcon,
  PlusIcon
} from '@heroicons/vue/24/outline'
import TransactionModal from '@/components/TransactionModal.vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const showAddTransaction = ref(false)
const notifications = ref(2) // Mock notification count

const currentUser = computed(() => userStore.currentUser)
const userInitials = computed(() => {
  if (!currentUser.value.name) return 'U'
  return currentUser.value.name.split(' ').map(n => n[0]).join('').toUpperCase()
})
</script>

<style scoped>
.nav-link {
  @apply flex items-center px-3 py-2 text-gray-700 rounded-lg hover:bg-gray-100 transition-colors duration-150;
}

.nav-link-active {
  @apply bg-primary-50 text-primary-700 hover:bg-primary-100;
}
</style>