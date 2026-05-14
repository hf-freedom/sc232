<template>
  <div>
    <el-button type="primary" @click="showAddDialog = true" style="margin-bottom: 20px">
      新增物资
    </el-button>

    <el-card>
      <el-alert
        title="库存物资按有效期自动排序，临期物资（3个月内到期）优先发放"
        type="info"
        :closable="false"
        style="margin-bottom: 20px"
      />
      <el-table :data="sortedInventory" border stripe>
        <el-table-column prop="type" label="物资类型" width="120" />
        <el-table-column prop="name" label="物资名称" width="120" />
        <el-table-column prop="batchNumber" label="批次号" width="150" />
        <el-table-column prop="quantity" label="总数量" width="100" />
        <el-table-column prop="lockedQuantity" label="锁定数量" width="100" />
        <el-table-column label="可用数量" width="100">
          <template #default="{ row }">
            <span :style="{ color: (row.quantity - row.lockedQuantity) < 50 ? '#f56c6c' : '#606266' }">
              {{ row.quantity - row.lockedQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="warehouseLocation" label="仓库位置" width="120" />
        <el-table-column prop="expiryDate" label="有效期" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.expiryDate" :type="isExpiringSoon(row.expiryDate) ? 'danger' : 'success'">
              {{ row.expiryDate }}
            </el-tag>
            <span v-else style="color: #909399">无</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="isExpiringSoon(row.expiryDate)" type="danger" effect="dark">
              临期
            </el-tag>
            <el-tag v-else type="success">
              正常
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增物资">
      <el-form :model="form" label-width="100px">
        <el-form-item label="物资类型">
          <el-input v-model="form.type" placeholder="如: 口罩、防护服、消毒液" />
        </el-form-item>
        <el-form-item label="物资名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="form.batchNumber" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="仓库位置">
          <el-input v-model="form.warehouseLocation" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-date-picker v-model="form.expiryDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addInventory">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const inventory = ref([])
const showAddDialog = ref(false)
const form = ref({
  type: '',
  name: '',
  batchNumber: '',
  quantity: 100,
  warehouseLocation: '',
  expiryDate: ''
})

const sortedInventory = computed(() => {
  return [...inventory.value].sort((a, b) => {
    if (!a.expiryDate && !b.expiryDate) return 0
    if (!a.expiryDate) return 1
    if (!b.expiryDate) return -1
    return new Date(a.expiryDate) - new Date(b.expiryDate)
  })
})

const isExpiringSoon = (date) => {
  if (!date) return false
  const expiry = new Date(date)
  const now = new Date()
  const diff = expiry - now
  return diff < 90 * 24 * 60 * 60 * 1000
}

const loadInventory = async () => {
  try {
    const res = await api.getInventory()
    inventory.value = res.data
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const addInventory = async () => {
  try {
    await api.addInventory(form.value)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    form.value = {
      type: '',
      name: '',
      batchNumber: '',
      quantity: 100,
      warehouseLocation: '',
      expiryDate: ''
    }
    loadInventory()
  } catch (e) {
    ElMessage.error(e.response?.data || '添加失败')
  }
}

onMounted(() => {
  loadInventory()
})
</script>
