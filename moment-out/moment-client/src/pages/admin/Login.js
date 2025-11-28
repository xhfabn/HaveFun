import React from 'react';
import { Form, Input, Button, Card, message, Typography, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import adminApi from '../../api/admin';

const Login = () => {
  const navigate = useNavigate();

  const onFinish = async (values) => {
    try {
      const res = await adminApi.login(values);
      localStorage.setItem('adminToken', res.token);
      localStorage.setItem('adminUser', JSON.stringify(res));
      message.success('Login successful');
      navigate('/admin');
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#f0f2f5' }}>
      <Card title="Admin Login" style={{ width: 360 }}>
        <Typography.Paragraph type="secondary" style={{ marginBottom: 16 }}>
          默认账号：admin / 123456
        </Typography.Paragraph>
        <Form onFinish={onFinish} initialValues={{ username: 'admin', password: '123456' }}>
          <Form.Item name="username" rules={[{ required: true, message: 'Please input your username!' }]}>
            <Input placeholder="Username" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: 'Please input your password!' }]}>
            <Input.Password placeholder="Password" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              Login
            </Button>
          </Form.Item>
        </Form>
        <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
          <Button type="link" onClick={() => navigate('/')}>返回主入口</Button>
        </Space>
      </Card>
    </div>
  );
};

export default Login;
