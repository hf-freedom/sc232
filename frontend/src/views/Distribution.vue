<template>
  <div>
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="发放记录" name="distribution">
          <el-alert
            title="系统自动按有效期优先发放，临期物资（3个月内到期）优先出库"
            type="success"
            :closable="false"
            style="margin-bottom: 20px"
          />
          <el-table :data="distributionRecords" border stripe>
            <el-table-column prop="applicantUnit" label="申请单位" width="120" />
            <el-table-column prop="materialType" label="物资类型" width="120" />
            <el-table-column prop="materialName" label="物资名称" width="120" />
            <el-table-column prop="quantity" label="发放数量" width="100" />
            <el-table-column prop="batchNumber" label="批次号" width="150" />
            <el-table-column prop="expiryDate" label="有效期" width="120">
              <template #default="{ row }">
                <el-tag :type="row.isExpiringSoon ? 'danger' : 'success'">
                  {{ row.expiryDate || '无' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发放策略" width="130">
              <template #default="{ row }">
                <el-tag v-if="row.isExpiringSoon" type="danger" effect="dark">
                  临期优先
                </el-tag>
                <el-tag v-else type="info">
                  正常发放
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="operator" label="操作人" width="100" />
            <el-table-column prop="distributedAt" label="发放时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.distributedAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="超额拦截记录" name="quotaBlocks">
          <el-alert
            title="系统自动拦截超过额度限制的申请"
            type="warning"
            :closable="false"
            style="margin-bottom: 20px"
          />
          <el-table :data="quotaBlockRecords" border stripe :empty-text="emptyText">
            <el-table-column prop="applicantUnit" label="申请单位" width="120" />
            <el-table-column prop="materialType" label="物资类型" width="120" />
            <el-table-column prop="requestedQuantity" label="申请数量" width="100">
              <template #default="{ row }">
                <el-tag type="danger">{{ row.requestedQuantity }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="usedQuota" label="已用额度" width="100" />
            <el-table-column prop="totalQuota" label="总可领额度" width="120" />
            <el-table-column prop="remainingQuota" label="当时剩余额度" width="120">
              <template #default="{ row }">
                <el-tag type="danger">{{ row.remainingQuota }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="拦截原因" min-width="150" />
            <el-table-column prop="blockedAt" label="拦截时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.blockedAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const activeTab = ref('distribution')
const distributionRecords = ref([])
const quotaBlockRecords = ref([])

const emptyText = computed(() => {
  if (activeTab.value === 'quotaBlocks' && quotaBlockRecords.value.length === 0) {
    return '暂无超额拦截记录，所有申请均在额度范围内'
  }
  return '暂无数据'
})

const formatDate = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleString()
}

const loadData = async () => {
  try {
    const [distRes, blockRes] = await Promise.all([
      api.getDistribution(),
      api.getQuotaBlocks()
    ])
    distributionRecords.value = distRes.data
    quotaBlockRecords.value = blockRes.data
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

onMounted(() => {
  loadData()
})
</script>
