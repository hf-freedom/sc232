<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background-color: #2f4050">
      <div style="color: white; padding: 20px; text-align: center; font-size: 18px; font-weight: bold">
        应急物资系统
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#2f4050"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/">
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/inventory">
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/requests">
          <span>需求申请</span>
        </el-menu-item>
        <el-menu-item index="/distribution">
          <span>发放记录</span>
        </el-menu-item>
        <el-menu-item index="/recovery">
          <span>回收登记</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background-color: #fff; border-bottom: 1px solid #eee; display: flex; align-items: center; padding: 0 20px">
        <h2 style="margin: 0">{{ pageTitle }}</h2>
      </el-header>
      <el-main style="background-color: #f3f3f4">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pageTitle = computed(() => {
  const titles = {
    '/': '数据统计',
    '/inventory': '库存管理',
    '/requests': '需求申请与审批',
    '/distribution': '发放记录',
    '/recovery': '回收登记'
  }
  return titles[route.path] || '应急物资发放系统'
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>
