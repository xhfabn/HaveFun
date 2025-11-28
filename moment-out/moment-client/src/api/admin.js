import request from './request';

const wrapDeleteParams = ids => ({ ids: Array.isArray(ids) ? ids.join(',') : ids });

const adminApi = {
  // Auth
  login: data => request({ url: '/admin/employee/login', method: 'post', data }),

  // Employee
  fetchEmployees: params => request({ url: '/admin/employee/page', method: 'get', params }),
  createEmployee: data => request({ url: '/admin/employee', method: 'post', data }),
  updateEmployee: data => request({ url: '/admin/employee', method: 'put', data }),
  getEmployeeDetail: id => request({ url: `/admin/employee/${id}`, method: 'get' }),
  toggleEmployeeStatus: (status, id) =>
    request({ url: `/admin/employee/status/${status}`, method: 'post', params: { id } }),

  // Category
  fetchCategoryPage: params => request({ url: '/admin/category/page', method: 'get', params }),
  listCategoriesByType: type => request({ url: '/admin/category/list', method: 'get', params: { type } }),
  createCategory: data => request({ url: '/admin/category', method: 'post', data }),
  updateCategory: data => request({ url: '/admin/category', method: 'put', data }),
  deleteCategory: id => request({ url: '/admin/category', method: 'delete', params: { id } }),
  toggleCategoryStatus: (status, id) =>
    request({ url: `/admin/category/status/${status}`, method: 'post', params: { id } }),

  // Dish
  fetchDishPage: params => request({ url: '/admin/dish/page', method: 'get', params }),
  createDish: data => request({ url: '/admin/dish', method: 'post', data }),
  updateDish: data => request({ url: '/admin/dish', method: 'put', data }),
  getDishDetail: id => request({ url: `/admin/dish/${id}`, method: 'get' }),
  deleteDish: ids => request({ url: '/admin/dish', method: 'delete', params: wrapDeleteParams(ids) }),
  toggleDishStatus: (status, id) =>
    request({ url: `/admin/dish/status/${status}`, method: 'post', params: { id } }),
  listDishesByCategory: cateId => request({ url: '/admin/dish/list', method: 'get', params: { cateId } }),
  uploadImage: file => {
    const formData = new FormData();
    formData.append('file', file);
    return request({
      url: '/admin/common/upload',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },

  // Setmeal
  fetchSetmealPage: params => request({ url: '/admin/setmeal/page', method: 'get', params }),
  createSetmeal: data => request({ url: '/admin/setmeal', method: 'post', data }),
  updateSetmeal: data => request({ url: '/admin/setmeal', method: 'put', data }),
  getSetmealDetail: id => request({ url: `/admin/setmeal/${id}`, method: 'get' }),
  deleteSetmeal: ids => request({ url: '/admin/setmeal', method: 'delete', params: wrapDeleteParams(ids) }),
  toggleSetmealStatus: (status, id) =>
    request({ url: `/admin/setmeal/status/${status}`, method: 'post', params: { id } }),

  // Orders
  fetchOrderStats: () => request({ url: '/admin/order/statistics', method: 'get' }),
  searchOrders: params => request({ url: '/admin/order/conditionSearch', method: 'get', params }),
  getOrderDetail: id => request({ url: `/admin/order/details/${id}`, method: 'get' }),
  confirmOrder: data => request({ url: '/admin/order/confirm', method: 'put', data }),
  rejectOrder: data => request({ url: '/admin/order/rejection', method: 'put', data }),
  cancelOrder: data => request({ url: '/admin/order/cancel', method: 'put', data }),
  deliverOrder: id => request({ url: `/admin/order/delivery/${id}`, method: 'put' }),
  completeOrder: id => request({ url: `/admin/order/complete/${id}`, method: 'put' }),

  // Reports
  fetchTurnoverReport: params => request({ url: '/admin/report/turnoverStatistics', method: 'get', params }),
  fetchUserReport: params => request({ url: '/admin/report/userStatistics', method: 'get', params }),
  fetchOrderReport: params => request({ url: '/admin/report/ordersStatistics', method: 'get', params }),
  fetchSalesTopReport: params => request({ url: '/admin/report/top10', method: 'get', params }),

  // Shop & workspace
  getShopStatus: () => request({ url: '/admin/shop/status', method: 'get' }),
  updateShopStatus: status => request({ url: `/admin/shop/${status}`, method: 'put' }),
  fetchBusinessSnapshot: () => request({ url: '/admin/workspace/businessData', method: 'get' }),
  fetchOrderOverview: () => request({ url: '/admin/workspace/overviewOrders', method: 'get' }),
  fetchDishOverview: () => request({ url: '/admin/workspace/overviewDishes', method: 'get' }),
  fetchSetmealOverview: () => request({ url: '/admin/workspace/overviewSetmeals', method: 'get' })
};

export const login = adminApi.login;
export default adminApi;
