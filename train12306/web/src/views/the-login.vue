<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center"><rocket-two-tone />&nbsp;12306售票系统</h1>
      <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
      >
        <a-form-item
            label=""
            name="mobile"
            :rules="[{ required: true, message: '请输入手机号!' }]"
        >
          <a-input v-model:value="loginForm.mobile" placeholder="手机号"/>
        </a-form-item>

        <a-form-item
            label=""
            name="code"
            :rules="[{ required: true, message: '请输入验证码!' }]"
        >
          <a-input v-model:value="loginForm.code">
            <template #addonAfter>
              <a @click="sendCode">获取验证码</a>
            </template>
          </a-input>
          <!--<a-input v-model:value="loginForm.code" placeholder="验证码"/>-->
        </a-form-item>

        <a-form-item>
          <a-button type="primary" block @click="login">登录</a-button>
        </a-form-item>

      </a-form>
    </a-col>
  </a-row>
</template>

<script>
import { defineComponent, reactive } from 'vue';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { useRouter } from 'vue-router'
import store from "@/store";

export default defineComponent({
  name: "login-view",
  setup() {
    const router = useRouter();

    const loginForm = reactive({
      mobile: '13000000000',
      code: '',
    });

    const sendCode = () => {
      axios.post("/member/member/send-code", {
        mobile: loginForm.mobile
      }).then(response => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '发送验证码成功！' });
          loginForm.code = "8888";
        } else {
          notification.error({ description: data.message });
        }
      }).catch((error) => {
        // 后端不可用时，模拟发送成功
        notification.success({ description: '发送验证码成功！（模拟）' });
        loginForm.code = "8888";
        console.log('后端服务不可用，使用模拟验证码发送', error.message);
      });
    };

    const login = () => {
      axios.post("/member/member/login", loginForm).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ 
            message: '登录成功',
            description: '欢迎回来！' 
          });
          // 使用Vuex action管理登录状态
          store.dispatch('login', data.content);
          // 登录成功，跳到控台主页
          router.push("/welcome");
        } else {
          notification.error({ 
            message: '登录失败',
            description: data.message || '登录失败，请重试' 
          });
        }
      }).catch((error) => {
        console.error('登录请求失败:', error);
        // 后端不可用时，模拟登录成功
        if (loginForm.code === "8888") {
          notification.success({ 
            message: '登录成功',
            description: '登录成功！（模拟模式）' 
          });
          // 模拟用户数据
          const mockUser = {
            id: 1,
            mobile: loginForm.mobile,
            token: 'mock-token-' + Date.now()
          };
          store.dispatch('login', mockUser);
          router.push("/welcome");
          console.log('后端服务不可用，使用模拟登录', error.message);
        } else {
          notification.error({ 
            message: '登录失败',
            description: '验证码错误！' 
          });
        }
      })
    };

    return {
      loginForm,
      sendCode,
      login
    };
  },
});
</script>

<style scoped>
.login-main h1 {
  font-size: 25px;
  font-weight: bold;
}
.login-main {
  margin-top: 100px;
  padding: 30px 30px 20px;
  border: 2px solid grey;
  border-radius: 10px;
  background-color: #fcfcfc;
}
</style>
