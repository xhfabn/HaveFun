import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'
import { notification } from 'ant-design-vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import(/* webpackChunkName: "login" */ '../views/the-login.vue'),
    meta: {
      requiresAuth: false // 登录页面不需要认证
    }
  },
  {
    path: '/',
    component: () => import(/* webpackChunkName: "main" */ '../views/the-main.vue'),
    meta: {
      requiresAuth: true // 主页面需要认证
    },
    children: [
      {
        path: '',
        redirect: '/welcome'
      },
      {
        path: '/welcome',
        name: 'Welcome',
        component: () => import(/* webpackChunkName: "welcome" */ '../views/welcome.vue'),
        meta: {
          requiresAuth: true
        }
      },
      {
        path: '/passenger',
        name: 'Passenger',
        component: () => import(/* webpackChunkName: "passenger" */ '../views/passenger.vue'),
        meta: {
          requiresAuth: true
        }
      },
      {
        path: '/ticket',
        name: 'Ticket',
        component: () => import(/* webpackChunkName: "ticket" */ '../views/ticket.vue'),
        meta: {
          requiresAuth: true
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log('路由守卫检查:', to.path)
  
  // 检查路由是否需要认证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  // 获取用户登录状态
  const isLoggedIn = store.getters.isLoggedIn
  
  console.log('需要认证:', requiresAuth, '已登录:', isLoggedIn)
  
  if (requiresAuth && !isLoggedIn) {
    // 需要认证但未登录，跳转到登录页
    console.log('未登录，跳转到登录页')
    notification.warning({
      message: '请先登录',
      description: '您需要登录后才能访问该页面'
    })
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    // 已登录用户访问登录页，跳转到首页
    console.log('已登录，跳转到首页')
    next('/welcome')
  } else {
    // 正常访问
    next()
  }
})

export default router
