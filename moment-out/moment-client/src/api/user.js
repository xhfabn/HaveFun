import request from './request';

const userApi = {
  // Auth
  login: data => request({ url: '/user/user/login', method: 'post', data }),
  register: data => request({ url: '/user/user/register', method: 'post', data }),

  // Category & Dish browsing
  fetchCategories: params => request({ url: '/user/category/list', method: 'get', params }),
  fetchDishList: categoryId =>
    request({ url: '/user/dish/list', method: 'get', params: { categoryId } }),
  fetchSetmealList: categoryId =>
    request({ url: '/user/setmeal/list', method: 'get', params: { categoryId } }),

  // Shopping cart
  fetchCart: () => request({ url: '/user/shoppingCart/list', method: 'get' }),
  addCartItem: data => request({ url: '/user/shoppingCart/add', method: 'post', data }),
  subCartItem: data => request({ url: '/user/shoppingCart/sub', method: 'post', data }),
  clearCart: () => request({ url: '/user/shoppingCart/clean', method: 'delete' }),

  // Address book
  listAddresses: () => request({ url: '/user/addressBook/list', method: 'get' }),
  saveAddress: data => request({ url: '/user/addressBook', method: 'post', data }),
  updateAddress: data => request({ url: '/user/addressBook', method: 'put', data }),
  deleteAddress: id => request({ url: '/user/addressBook', method: 'delete', params: { id } }),
  setDefaultAddress: id =>
    request({ url: '/user/addressBook/default', method: 'put', data: { id } }),
  getDefaultAddress: () => request({ url: '/user/addressBook/default', method: 'get' }),

  // Orders
  submitOrder: data => request({ url: '/user/order/submit', method: 'post', data }),
  payOrder: data => request({ url: '/user/order/payment', method: 'put', data }),
  fetchOrders: params => request({ url: '/user/order/historyOrders', method: 'get', params }),
  fetchOrderDetail: id => request({ url: `/user/order/orderDetail/${id}`, method: 'get' }),
  cancelOrder: id => request({ url: `/user/order/cancel/${id}`, method: 'put' }),
  repeatOrder: id => request({ url: `/user/order/repetition/${id}`, method: 'post' }),
  remindOrder: id => request({ url: `/user/order/reminder/${id}`, method: 'get' })
};

export const login = userApi.login;
export default userApi;
