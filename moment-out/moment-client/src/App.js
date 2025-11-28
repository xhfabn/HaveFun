import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import AdminLogin from './pages/admin/Login';
import AdminLayout from './pages/admin/Layout';
import EmployeeList from './pages/admin/EmployeeList';
import DishList from './pages/admin/DishList';
import CategoryList from './pages/admin/CategoryList';
import SetmealList from './pages/admin/SetmealList';
import OrderList from './pages/admin/OrderList';
import Dashboard from './pages/admin/Dashboard';
import Settings from './pages/admin/Settings';
import Landing from './pages/Landing';
import UserLogin from './pages/user/Login';
import UserRegister from './pages/user/Register';
import UserMenu from './pages/user/Menu';
import UserOrders from './pages/user/Orders';
import UserAddresses from './pages/user/Addresses';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Router>
      <Routes>
        {/* Admin Routes */}
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route
          path="/admin"
          element={(
            <ProtectedRoute tokenKey="adminToken" redirectTo="/admin/login">
              <AdminLayout />
            </ProtectedRoute>
          )}
        >
          <Route index element={<Navigate to="/admin/dashboard" />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="employee" element={<EmployeeList />} />
          <Route path="category" element={<CategoryList />} />
          <Route path="dish" element={<DishList />} />
          <Route path="setmeal" element={<SetmealList />} />
          <Route path="orders" element={<OrderList />} />
          <Route path="settings" element={<Settings />} />
        </Route>

        {/* User Routes */}
        <Route path="/user/login" element={<UserLogin />} />
        <Route path="/user/register" element={<UserRegister />} />
        <Route
          path="/user/menu"
          element={(
            <ProtectedRoute tokenKey="userToken" redirectTo="/user/login">
              <UserMenu />
            </ProtectedRoute>
          )}
        />
        <Route
          path="/user/orders"
          element={(
            <ProtectedRoute tokenKey="userToken" redirectTo="/user/login">
              <UserOrders />
            </ProtectedRoute>
          )}
        />
        <Route
          path="/user/addresses"
          element={(
            <ProtectedRoute tokenKey="userToken" redirectTo="/user/login">
              <UserAddresses />
            </ProtectedRoute>
          )}
        />
        
        <Route path="/" element={<Landing />} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </Router>
  );
}

export default App;
