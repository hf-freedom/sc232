<template>
  <div>
    <el-button type="primary" @click="showAddDialog = true" style="margin-bottom: 20px">
      新增回收
    </el-button>

    <el-card>
      <el-table :data="records" border stripe>
        <el-table-column prop="applicantUnit" label="申请单位" width="120" />
        <el-table-column prop="materialType" label="物资类型" width="120" />
        <el-table-column prop="materialName" label="物资名称" width="120" />
        <el-table-column prop="quantity" label="回收数量" width="100" />
        <el-table-column prop="batchNumber" label="批次号" width="150" />
        <el-table-column prop="reason" label="回收原因" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="recoveredAt" label="回收时间">
          <template #default="{ row }">
            {{ formatDate(row.recoveredAt) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增回收登记">
      <el-form :model="form" label-width="100px">
        <el-form-item label="申请单位">
          <el-select v-model="form.applicantUnit">
            <el-option label="A社区" value="A社区" />
            <el-option label="B社区" value="B社区" />
            <el-option label="C社区" value="C社区" />
            <el-option label="D社区" value="D社区" />
            <el-option label="E社区" value="E社区" />
          </el-select>
        </el-form-item>
        <el-form-item label="物资类型">
          <el-input v-model="form.materialType" />
        </el-form-item>
        <el-form-item label="物资名称">
          <el-input v-model="form.materialName" />
        </el-form-item>
        <el-form-item label="回收数量">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="form.batchNumber" />
        </el-form-item>
        <el-form-item label="回收原因">
          <el-input v-model="form.reason" type="textarea" />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="form.operator" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addRecovery">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const records = ref([])
const showAddDialog = ref(false)
const form = ref({
  applicantUnit: '',
  materialType: '',
  materialName: '',
  quantity: 10,
  batchNumber: '',
  reason: '',
  operator: ''
})

const formatDate = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleString()
}

const loadData = async () => {
  try {
    const res = await api.getRecovery()
    records.value = res.data
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const addRecovery = async () => {
  try {
    await api.addRecovery(form.value)
    ElMessage.success('回收登记成功')
    showAddDialog.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.response?.data || '登记失败')
  }
}

onMounted(() => {
  loadData()
})
</script>
