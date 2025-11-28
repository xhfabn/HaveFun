import axios from 'axios';
import { message } from 'antd';

const API_BASE_URL = process.env.REACT_APP_API_BASE || '/';
const ADMIN_TOKEN_KEY = 'adminToken';
const USER_TOKEN_KEY = 'userToken';

const service = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
});

service.interceptors.request.use(
  config => {
    const headers = config.headers || {};
    if (!headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }

    if (config.url.startsWith('/admin')) {
      const token = localStorage.getItem(ADMIN_TOKEN_KEY);
      if (token) {
        headers.token = token;
      }
    } else if (config.url.startsWith('/user')) {
      const token = localStorage.getItem(USER_TOKEN_KEY);
      if (token) {
        headers.authentication = token;
      }
    }

    config.headers = headers;
    return config;
  },
  error => Promise.reject(error)
);

service.interceptors.response.use(
  response => {
    const res = response.data;
    if (res?.code === 1) {
      return res.data;
    }
    const errMsg = res?.msg || '请求失败，请稍后重试';
    message.error(errMsg);
    return Promise.reject(new Error(errMsg));
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem(ADMIN_TOKEN_KEY);
      localStorage.removeItem(USER_TOKEN_KEY);
      message.error('登录已过期，请重新登录');
    } else {
      message.error(error.message || '网络异常');
    }
    return Promise.reject(error);
  }
);

export default service;
