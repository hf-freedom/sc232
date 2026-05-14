<template>
  <div>
    <el-button type="primary" @click="showAddDialog = true" style="margin-bottom: 20px">
      提交需求申请
    </el-button>

    <el-card>
      <el-table :data="requests" border stripe>
        <el-table-column prop="applicantUnit" label="申请单位" width="120" />
        <el-table-column prop="personCount" label="人数" width="80" />
        <el-table-column prop="purpose" label="用途" width="150" />
        <el-table-column prop="urgencyLevel" label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyType(row.urgencyLevel)">
              {{ getUrgencyText(row.urgencyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请物资" width="180">
          <template #default="{ row }">
            <div v-for="(qty, type) in row.materials" :key="type">
              {{ type }}: {{ qty }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批信息" min-width="200">
          <template #default="{ row }">
            <div v-if="row.approver">
              <div>审批人: {{ row.approver }}</div>
              <div v-if="row.approvalRemark">备注: {{ row.approvalRemark }}</div>
              <div v-if="row.status === 'REJECTED'" style="color: #f56c6c">
                ✅ 库存已释放
              </div>
              <div v-if="row.status === 'APPROVED'" style="color: #67c23a">
                ✅ 物资已发放
              </div>
            </div>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" size="small" type="warning" @click="lockRequest(row.id)">
              锁定库存
            </el-button>
            <el-button v-if="row.status === 'LOCKED'" size="small" type="success" @click="approveDialog(row)">
              审批
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="提交需求申请">
      <el-form :model="form" label-width="100px">
        <el-form-item label="申请单位">
          <el-select v-model="form.applicantUnit" placeholder="选择单位">
            <el-option label="A社区" value="A社区" />
            <el-option label="B社区" value="B社区" />
            <el-option label="C社区" value="C社区" />
            <el-option label="D社区" value="D社区" />
            <el-option label="E社区" value="E社区" />
          </el-select>
        </el-form-item>
        <el-form-item label="人数">
          <el-input-number v-model="form.personCount" :min="1" />
        </el-form-item>
        <el-form-item label="用途">
          <el-input v-model="form.purpose" />
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-select v-model="form.urgencyLevel">
            <el-option label="特急" value="CRITICAL" />
            <el-option label="紧急" value="HIGH" />
            <el-option label="一般" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="物资1">
          <el-input v-model="material1.type" placeholder="类型" style="width: 150px; margin-right: 10px" />
          <el-input-number v-model="material1.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="物资2">
          <el-input v-model="material2.type" placeholder="类型" style="width: 150px; margin-right: 10px" />
          <el-input-number v-model="material2.quantity" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="createRequest">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showApproveDialog" title="审批">
      <el-form label-width="100px">
        <el-form-item label="审批人">
          <el-input v-model="approveForm.approver" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="approveForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApproveDialog = false">取消</el-button>
        <el-button type="danger" @click="approve(false)">拒绝</el-button>
        <el-button type="success" @click="approve(true)">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const requests = ref([])
const showAddDialog = ref(false)
const showApproveDialog = ref(false)
const currentRequestId = ref('')

const form = ref({
  applicantUnit: '',
  personCount: 10,
  purpose: '',
  urgencyLevel: 'MEDIUM'
})

const material1 = ref({ type: '', quantity: 10 })
const material2 = ref({ type: '', quantity: 0 })

const approveForm = ref({
  approver: '',
  remark: ''
})

const getUrgencyType = (level) => {
  const map = { CRITICAL: 'danger', HIGH: 'warning', MEDIUM: '', LOW: 'info' }
  return map[level] || ''
}

const getUrgencyText = (level) => {
  const map = { CRITICAL: '特急', HIGH: '紧急', MEDIUM: '一般', LOW: '低' }
  return map[level] || level
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', LOCKED: 'info', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { PENDING: '待处理', LOCKED: '已锁定', APPROVED: '已通过', REJECTED: '已拒绝' }
  return map[status] || status
}

const loadRequests = async () => {
  try {
    const res = await api.getRequests()
    requests.value = res.data
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const createRequest = async () => {
  try {
    const materials = {}
    if (material1.value.type && material1.value.quantity > 0) {
      materials[material1.value.type] = material1.value.quantity
    }
    if (material2.value.type && material2.value.quantity > 0) {
      materials[material2.value.type] = material2.value.quantity
    }
    
    if (Object.keys(materials).length === 0) {
      ElMessage.warning('请至少填写一种物资')
      return
    }
    
    await api.createRequest({ ...form.value, materials })
    ElMessage.success('申请提交成功')
    showAddDialog.value = false
    loadRequests()
  } catch (e) {
    ElMessage.error(e.response?.data || '提交失败')
  }
}

const lockRequest = async (id) => {
  try {
    await api.lockRequest(id)
    ElMessage.success('库存锁定成功')
    loadRequests()
  } catch (e) {
    ElMessage.error(e.response?.data || '锁定失败')
  }
}

const approveDialog = (row) => {
  currentRequestId.value = row.id
  showApproveDialog.value = true
}

const approve = async (approved) => {
  try {
    const res = await api.approveRequest(currentRequestId.value, approveForm.value.approver, approveForm.value.remark, approved)
    ElMessage.success(res.data.message)
    showApproveDialog.value = false
    loadRequests()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '审批失败')
  }
}

onMounted(() => {
  loadRequests()
})
</script>
