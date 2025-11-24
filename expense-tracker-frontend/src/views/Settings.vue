<template>
  <div class="settings">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Settings</h1>
      <p class="text-gray-600 mt-1">Manage your account and preferences</p>
    </div>

    <!-- Settings Sections -->
    <div class="space-y-6">
      <!-- Profile Settings -->
      <div class="card">
        <div class="card-body">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Profile Information</h2>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="form-label">Full Name</label>
              <input v-model="profile.fullName" type="text" class="form-input" />
            </div>
            
            <div>
              <label class="form-label">Email</label>
              <input v-model="profile.email" type="email" class="form-input" />
            </div>
            
            <div>
              <label class="form-label">Username</label>
              <input v-model="profile.username" type="text" class="form-input" />
            </div>
            
            <div>
              <label class="form-label">Phone Number</label>
              <input v-model="profile.phone" type="tel" class="form-input" />
            </div>
          </div>
          
          <div class="mt-4">
            <button @click="saveProfile" class="btn btn-primary">Save Changes</button>
          </div>
        </div>
      </div>

      <!-- Preferences -->
      <div class="card">
        <div class="card-body">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Preferences</h2>
          
          <div class="space-y-4">
            <div>
              <label class="form-label">Default Currency</label>
              <select v-model="preferences.currency" class="form-select max-w-xs">
                <option value="USD">USD - US Dollar</option>
                <option value="EUR">EUR - Euro</option>
                <option value="GBP">GBP - British Pound</option>
                <option value="JPY">JPY - Japanese Yen</option>
              </select>
            </div>
            
            <div>
              <label class="form-label">Date Format</label>
              <select v-model="preferences.dateFormat" class="form-select max-w-xs">
                <option value="MM/DD/YYYY">MM/DD/YYYY</option>
                <option value="DD/MM/YYYY">DD/MM/YYYY</option>
                <option value="YYYY-MM-DD">YYYY-MM-DD</option>
              </select>
            </div>
            
            <div>
              <label class="form-label">Week Starts On</label>
              <select v-model="preferences.weekStart" class="form-select max-w-xs">
                <option value="sunday">Sunday</option>
                <option value="monday">Monday</option>
              </select>
            </div>
          </div>
          
          <div class="mt-4">
            <button @click="savePreferences" class="btn btn-primary">Save Preferences</button>
          </div>
        </div>
      </div>

      <!-- Notifications -->
      <div class="card">
        <div class="card-body">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Notification Settings</h2>
          
          <div class="space-y-3">
            <label class="flex items-center">
              <input v-model="notifications.budgetAlerts" type="checkbox" class="rounded text-primary-600" />
              <span class="ml-3 text-gray-700">Budget alerts when spending exceeds threshold</span>
            </label>
            
            <label class="flex items-center">
              <input v-model="notifications.weeklyReport" type="checkbox" class="rounded text-primary-600" />
              <span class="ml-3 text-gray-700">Weekly spending report</span>
            </label>
            
            <label class="flex items-center">
              <input v-model="notifications.monthlyReport" type="checkbox" class="rounded text-primary-600" />
              <span class="ml-3 text-gray-700">Monthly financial summary</span>
            </label>
            
            <label class="flex items-center">
              <input v-model="notifications.transactionReminders" type="checkbox" class="rounded text-primary-600" />
              <span class="ml-3 text-gray-700">Daily transaction entry reminders</span>
            </label>
          </div>
          
          <div class="mt-4">
            <button @click="saveNotifications" class="btn btn-primary">Save Notifications</button>
          </div>
        </div>
      </div>

      <!-- Security -->
      <div class="card">
        <div class="card-body">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Security</h2>
          
          <div class="space-y-4">
            <div>
              <button @click="changePassword" class="btn btn-secondary">
                Change Password
              </button>
            </div>
            
            <div>
              <button @click="enableTwoFactor" class="btn btn-secondary">
                Enable Two-Factor Authentication
              </button>
            </div>
            
            <div>
              <button @click="downloadData" class="btn btn-secondary">
                Download My Data
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Danger Zone -->
      <div class="card border-danger-200">
        <div class="card-body">
          <h2 class="text-lg font-semibold text-danger-600 mb-4">Danger Zone</h2>
          
          <div class="space-y-4">
            <div>
              <p class="text-gray-700 mb-2">Delete all transactions and reset your account</p>
              <button @click="resetAccount" class="btn btn-danger">
                Reset Account
              </button>
            </div>
            
            <div>
              <p class="text-gray-700 mb-2">Permanently delete your account and all data</p>
              <button @click="deleteAccount" class="btn btn-danger">
                Delete Account
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useToast } from 'vue-toastification'

const toast = useToast()

// Data
const profile = ref({
  fullName: 'John Doe',
  email: 'john.doe@example.com',
  username: 'johndoe',
  phone: '+1 234 567 8900'
})

const preferences = ref({
  currency: 'USD',
  dateFormat: 'MM/DD/YYYY',
  weekStart: 'sunday'
})

const notifications = ref({
  budgetAlerts: true,
  weeklyReport: false,
  monthlyReport: true,
  transactionReminders: false
})

// Methods
const saveProfile = () => {
  toast.success('Profile updated successfully')
}

const savePreferences = () => {
  toast.success('Preferences saved successfully')
}

const saveNotifications = () => {
  toast.success('Notification settings updated')
}

const changePassword = () => {
  toast.info('Password change feature coming soon')
}

const enableTwoFactor = () => {
  toast.info('Two-factor authentication coming soon')
}

const downloadData = () => {
  toast.info('Data export feature coming soon')
}

const resetAccount = () => {
  if (confirm('Are you sure you want to reset your account? This will delete all transactions.')) {
    toast.warning('Account reset initiated')
  }
}

const deleteAccount = () => {
  if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
    toast.error('Account deletion initiated')
  }
}
</script>