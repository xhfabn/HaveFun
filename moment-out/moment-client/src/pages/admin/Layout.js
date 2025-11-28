import React from 'react';
import { Layout, Menu, Button, Typography, Space } from 'antd';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  UserOutlined,
  CoffeeOutlined,
  LogoutOutlined,
  HomeOutlined,
  AppstoreOutlined,
  OrderedListOutlined,
  ShoppingOutlined,
  SettingOutlined
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const AdminLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
    navigate('/admin/login');
  };

  const menuItems = [
    {
      key: '/admin/dashboard',
      icon: <HomeOutlined />,
      label: 'Dashboard'
    },
    {
      key: '/admin/employee',
      icon: <UserOutlined />,
      label: 'Employee Management'
    },
    {
      key: '/admin/category',
      icon: <AppstoreOutlined />,
      label: 'Categories'
    },
    {
      key: '/admin/dish',
      icon: <CoffeeOutlined />,
      label: 'Dish Management'
    },
    {
      key: '/admin/setmeal',
      icon: <ShoppingOutlined />,
      label: 'Setmeal Management'
    },
    {
      key: '/admin/orders',
      icon: <OrderedListOutlined />,
      label: 'Orders'
    },
    {
      key: '/admin/settings',
      icon: <SettingOutlined />,
      label: 'Settings'
    }
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible>
        <div
    style={{
      height: 48,
      margin: 16,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'left',
      color: '#fff',
      fontWeight: 600,
      letterSpacing: 1
    }}
  >
    MomentOut管理后台
  </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 16px', background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography.Text>
            欢迎，{JSON.parse(localStorage.getItem('adminUser') || '{}').name || '管理员'}
          </Typography.Text>
          <Space>
            <Button onClick={() => navigate('/')}>返回入口</Button>
            <Button icon={<LogoutOutlined />} onClick={handleLogout}>
              Logout
            </Button>
          </Space>
        </Header>
        <Content style={{ margin: '16px' }}>
          <div style={{ padding: 24, minHeight: 360, background: '#fff' }}>
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default AdminLayout;
