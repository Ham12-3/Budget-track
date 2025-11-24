import { createRouter, createWebHistory } from 'vue-router'

// Import view components
import Dashboard from '@/views/Dashboard.vue'
import Transactions from '@/views/Transactions.vue'
import Budgets from '@/views/Budgets.vue'
import Categories from '@/views/Categories.vue'
import Reports from '@/views/Reports.vue'
import Settings from '@/views/Settings.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { title: 'Dashboard' }
  },
  {
    path: '/transactions',
    name: 'Transactions',
    component: Transactions,
    meta: { title: 'Transactions' }
  },
  {
    path: '/budgets',
    name: 'Budgets',
    component: Budgets,
    meta: { title: 'Budgets' }
  },
  {
    path: '/categories',
    name: 'Categories',
    component: Categories,
    meta: { title: 'Categories' }
  },
  {
    path: '/reports',
    name: 'Reports',
    component: Reports,
    meta: { title: 'Reports' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: { title: 'Settings' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Update page title on route change
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title} - Expense Tracker`
  next()
})

export default router