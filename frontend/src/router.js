import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from './views/Dashboard.vue'
import Inventory from './views/Inventory.vue'
import Requests from './views/Requests.vue'
import Distribution from './views/Distribution.vue'
import Recovery from './views/Recovery.vue'

const routes = [
  { path: '/', component: Dashboard },
  { path: '/inventory', component: Inventory },
  { path: '/requests', component: Requests },
  { path: '/distribution', component: Distribution },
  { path: '/recovery', component: Recovery }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
