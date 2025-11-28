<template>
  <a-layout-header class="header">
    <div class="logo">
      <router-link to="/welcome" style="color: white; font-size: 18px">
        12306
      </router-link>
    </div>
    <div style="float: right; color: white;">
      您好：{{member.mobile}} &nbsp;&nbsp;
      <a @click="logout" style="color: white; cursor: pointer;">
        退出登录
      </a>
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <user-outlined /> &nbsp; 乘车人管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/ticket">
        <router-link to="/ticket">
          <user-outlined /> &nbsp; 余票查询
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch, computed} from 'vue';
import { Modal, notification } from 'ant-design-vue';
import store from "@/store";
import router from '@/router'

export default defineComponent({
  name: "the-header-view",
  setup() {
    // 使用computed获取最新的用户信息
    const member = computed(() => store.getters.currentUser);
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});

    // 登出功能
    const logout = () => {
      Modal.confirm({
        title: '确认退出',
        content: '您确定要退出登录吗？',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          // 使用Vuex action清除用户信息
          store.dispatch('logout');
          notification.success({
            message: '退出成功',
            description: '您已成功退出登录'
          });
          // 跳转到登录页
          router.push('/login');
        }
      });
    };

    return {
      member,
      selectedKeys,
      logout
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.logo {
  float: left;
  height: 31px;
  width: 150px;
  color: white;
  font-size: 20px;
}
</style>
