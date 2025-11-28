import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
 import Antd, {notification} from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';
import './utils/tool';
import './assets/js/enums';

const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app');

// 全局使用图标
const icons = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
    console.log('请求参数：', config);
    // 从store中获取token
    const token = store.getters.token;
    if (token) {
        config.headers.token = token;
        console.log("请求headers增加token:", token);
    }
    return config;
}, error => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    console.log('返回结果：', response);
    return response;
}, error => {
    console.log('返回错误：', error);
    
    if (!error.response) {
        // 网络错误或服务器无响应
        notification.error({ 
            message: '网络错误',
            description: '请检查网络连接或稍后重试' 
        });
        return Promise.reject(error);
    }
    
    const response = error.response;
    const status = response.status;
    const data = response.data;
    
    switch (status) {
        case 401:
            // 未授权，token无效或过期
            console.log("未登录或登录超时，跳到登录页");
            store.dispatch('logout'); // 使用action清除用户信息
            notification.error({ 
                message: '登录已过期',
                description: "请重新登录" 
            });
            // 避免在登录页面重复跳转
            if (router.currentRoute.value.path !== '/login') {
                router.push('/login');
            }
            break;
        case 403:
            // 禁止访问
            notification.error({ 
                message: '访问被拒绝',
                description: data?.message || '您没有权限访问此资源' 
            });
            break;
        case 404:
            // 资源不存在
            notification.error({ 
                message: '资源不存在',
                description: data?.message || '请求的资源不存在' 
            });
            break;
        case 500:
            // 服务器内部错误
            notification.error({ 
                message: '服务器错误',
                description: data?.message || '服务器内部错误，请稍后重试' 
            });
            break;
        default:
            // 其他错误
            notification.error({ 
                message: '请求失败',
                description: data?.message || `请求失败 (${status})` 
            });
    }
    
    return Promise.reject(error);
});
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);

