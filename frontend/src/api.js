import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8001/api'
})

export default {
  getInventory: () => api.get('/inventory'),
  addInventory: (data) => api.post('/inventory', data),
  getRequests: () => api.get('/requests'),
  createRequest: (data) => api.post('/requests', data),
  lockRequest: (id) => api.post(`/requests/${id}/lock`),
  approveRequest: (id, approver, remark, approved) => 
    api.post(`/requests/${id}/approve`, { approver, remark, approved }),
  getRecovery: () => api.get('/recovery'),
  addRecovery: (data) => api.post('/recovery', data),
  getDistribution: () => api.get('/distribution'),
  getStatistics: () => api.get('/statistics'),
  getQuotas: () => api.get('/quotas'),
  getQuotaUsage: () => api.get('/quotas/usage'),
  getQuotaBlocks: () => api.get('/quota-blocks'),
  getAvailableBatches: (type) => api.get(`/inventory/batches/${type}`)
}
