<template>
  <div class="welcome">
    <div class="welcome-content">
      <h1><rocket-two-tone style="color: #1890ff" /> 欢迎使用12306售票系统</h1>
      <p class="welcome-desc">这是一个基于Vue 3和Ant Design Vue的火车票预订系统</p>
      <div class="feature-cards">
        <a-card title="乘车人管理" class="feature-card">
          <template #extra>
            <user-outlined />
          </template>
          <p>管理您的乘车人信息，方便快速购票</p>
          <a-button type="primary" @click="$router.push('/passenger')">立即管理</a-button>
        </a-card>
        <a-card title="余票查询" class="feature-card">
          <template #extra>
            <search-outlined />
          </template>
          <p>查询车次余票信息，选择合适的出行方案</p>
          <a-button type="primary" @click="$router.push('/ticket')">立即查询</a-button>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'welcome-view',
  setup() {
    return {};
  },
});
</script>

<style scoped>
.welcome {
  padding: 40px;
  text-align: center;
}

.welcome-content {
  max-width: 800px;
  margin: 0 auto;
}

.welcome h1 {
  font-size: 32px;
  margin-bottom: 16px;
  color: #1890ff;
}

.welcome-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.feature-cards {
  display: flex;
  gap: 24px;
  justify-content: center;
  flex-wrap: wrap;
}

.feature-card {
  width: 300px;
  text-align: left;
}

.feature-card .ant-card-body {
  padding: 24px;
}

.feature-card p {
  margin-bottom: 16px;
  color: #666;
}
</style>