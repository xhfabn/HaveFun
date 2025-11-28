import React from 'react';
import { Form, Input, Button, Card, message, Typography, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import userApi from '../../api/user';

const Login = () => {
  const navigate = useNavigate();

  const onFinish = async values => {
    try {
      const res = await userApi.login(values);
      localStorage.setItem('userToken', res.token);
      localStorage.setItem('userInfo', JSON.stringify(res));
      message.success('登录成功');
      navigate('/user/menu');
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#f0f2f5' }}>
      <Card title="用户登录" style={{ width: 400 }}>
        <Typography.Paragraph type="secondary">
          使用注册的账号密码登录，稍后可在用户端进行点餐与下单。
        </Typography.Paragraph>
        <Form layout="vertical" onFinish={onFinish}>
          <Form.Item name="username" label="用户名" rules={[{ required: true, message: '请输入用户名' }]}
            >
            <Input placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]}
            >
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              登录
            </Button>
          </Form.Item>
        </Form>
        <Space style={{ width: '100%', justifyContent: 'space-between' }}>
          <Button type="link" onClick={() => navigate('/user/register')}>
            注册新用户
          </Button>
          <Button type="link" onClick={() => navigate('/')}>返回主入口</Button>
        </Space>
      </Card>
    </div>
  );
};

export default Login;
