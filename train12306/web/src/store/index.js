import { createStore } from 'vuex'

// 从localStorage获取用户信息
const getStoredMember = () => {
  try {
    const stored = localStorage.getItem('member')
    return stored ? JSON.parse(stored) : {}
  } catch (error) {
    console.error('解析localStorage中的用户信息失败:', error)
    return {}
  }
}

export default createStore({
  state: {
    member: getStoredMember()
  },
  getters: {
    // 检查用户是否已登录
    isLoggedIn: (state) => {
      return !!(state.member && state.member.token)
    },
    // 获取当前用户信息
    currentUser: (state) => {
      return state.member
    },
    // 获取token
    token: (state) => {
      return state.member ? state.member.token : null
    }
  },
  mutations: {
    // 设置用户信息并持久化到localStorage
    setMember(state, member) {
      state.member = member
      if (member && Object.keys(member).length > 0) {
        localStorage.setItem('member', JSON.stringify(member))
      } else {
        localStorage.removeItem('member')
      }
    },
    // 清除用户信息
    clearMember(state) {
      state.member = {}
      localStorage.removeItem('member')
    },
    // 更新token
    updateToken(state, token) {
      if (state.member) {
        state.member.token = token
        localStorage.setItem('member', JSON.stringify(state.member))
      }
    }
  },
  actions: {
    // 登录action
    login({ commit }, memberData) {
      commit('setMember', memberData)
    },
    // 登出action
    logout({ commit }) {
      commit('clearMember')
    },
    // 刷新token
    refreshToken({ commit }, token) {
      commit('updateToken', token)
    }
  },
  modules: {
  }
})
