<template>
  <div>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="4.8">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 28px; color: #409eff; font-weight: bold">{{ statistics.totalRequests || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">总申请数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 28px; color: #e6a23c; font-weight: bold">{{ statistics.urgentRequestCount || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">紧急申请数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 28px; color: #67c23a; font-weight: bold">{{ statistics.totalDistribution || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">发放记录数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 28px; color: #909399; font-weight: bold">{{ statistics.totalRecovery || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">回收记录数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 28px; color: #f56c6c; font-weight: bold">{{ statistics.totalQuotaBlocks || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">超额拦截次数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card title="物资剩余量">
          <el-table :data="remainingData" border>
            <el-table-column prop="type" label="物资类型" />
            <el-table-column prop="quantity" label="剩余数量" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card title="物资发放量">
          <el-table :data="distributedData" border>
            <el-table-column prop="type" label="物资类型" />
            <el-table-column prop="quantity" label="发放数量" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card title="物资回收量">
          <el-table :data="recoveredData" border>
            <el-table-column prop="type" label="物资类型" />
            <el-table-column prop="quantity" label="回收数量" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px">
      <el-card title="单位额度使用情况">
        <el-table :data="quotaUsageData" border stripe>
          <el-table-column prop="unit" label="申请单位" width="120" />
          <el-table-column prop="totalQuota" label="总可领额度" width="120" />
          <el-table-column prop="usedQuota" label="已使用额度" width="120">
            <template #default="{ row }">
              <span :style="{ color: row.usedQuota > row.totalQuota * 0.8 ? '#f56c6c' : '#606266' }">
                {{ row.usedQuota }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="remainingQuota" label="剩余额度" width="120">
            <template #default="{ row }">
              <el-tag :type="row.remainingQuota < 100 ? 'danger' : 'success'">
                {{ row.remainingQuota }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="使用率" width="200">
            <template #default="{ row }">
              <el-progress :percentage="Math.round((row.usedQuota / row.totalQuota) * 100)" :color="getProgressColor(row)" />
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const statistics = ref({})
const remainingData = ref([])
const distributedData = ref([])
const recoveredData = ref([])
const quotaUsageData = ref([])

const getProgressColor = (row) => {
  const percentage = (row.usedQuota / row.totalQuota) * 100
  if (percentage >= 90) return '#f56c6c'
  if (percentage >= 70) return '#e6a23c'
  return '#67c23a'
}

const loadData = async () => {
  try {
    const statsRes = await api.getStatistics()
    statistics.value = statsRes.data
    
    const mapToArray = (obj) => {
      if (!obj) return []
      return Object.entries(obj).map(([type, quantity]) => ({ type, quantity }))
    }
    
    remainingData.value = mapToArray(statsRes.data.remaining)
    distributedData.value = mapToArray(statsRes.data.distributed)
    recoveredData.value = mapToArray(statsRes.data.recovered)
    
    const usage = statsRes.data.unitQuotaUsage || {}
    quotaUsageData.value = Object.entries(usage).map(([unit, data]) => ({
      unit,
      totalQuota: data.totalQuota,
      usedQuota: data.usedQuota,
      remainingQuota: data.remainingQuota
    }))
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadData()
})
</script>
