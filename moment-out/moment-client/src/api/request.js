import axios from 'axios';
import { message } from 'antd';

const API_BASE_URL = process.env.REACT_APP_API_BASE || '/';
const ADMIN_TOKEN_KEY = 'adminToken';
const USER_TOKEN_KEY = 'userToken';

const service = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
});

let isAuthRedirecting = false;

const resolveLoginPath = (requestUrl) => {
  if (!requestUrl) {
    return window.location.pathname.startsWith('/user') ? '/user/login' : '/admin/login';
  }
  try {
    const url = new URL(requestUrl, window.location.origin);
    return url.pathname.startsWith('/user') ? '/user/login' : '/admin/login';
  } catch (err) {
    return requestUrl.startsWith('/user') ? '/user/login' : '/admin/login';
  }
};

const showErrorMessage = (msg) => {
  if (isAuthRedirecting) {
    return;
  }
  message.error(msg);
};

const handleAuthExpired = (requestUrl) => {
  if (isAuthRedirecting) {
    return;
  }
  localStorage.removeItem(ADMIN_TOKEN_KEY);
  localStorage.removeItem(USER_TOKEN_KEY);
  const target = resolveLoginPath(requestUrl || window.location.pathname);
  isAuthRedirecting = true;
  message.destroy();
  message.error('登录已过期，请重新登录');
  window.location.replace(target);
};

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
    if (errMsg.includes('未登录')) {
      handleAuthExpired(response.config?.url);
    } else {
      showErrorMessage(errMsg);
    }
    return Promise.reject(new Error(errMsg));
  },
  error => {
    if (error.response?.status === 401) {
      handleAuthExpired(error.response.config?.url);
    } else if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      showErrorMessage('请求超时，请稍后重试');
    } else if (!axios.isCancel(error)) {
      showErrorMessage(error.message || '网络异常');
    }
    return Promise.reject(error);
  }
);

export default service;
