import React from 'react';
import { Card, Form, Input, Button, Typography, message, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import userApi from '../../api/user';

const phoneValidator = (_, value) => {
  if (!value) {
    return Promise.reject(new Error('请输入手机号'));
  }
  return /^1\d{10}$/.test(value) ? Promise.resolve() : Promise.reject(new Error('请输入11位合法手机号'));
};

const Register = () => {
  const navigate = useNavigate();

  const onFinish = async values => {
    if (values.password !== values.confirmPassword) {
      message.warning('两次输入的密码不一致');
      return;
    }
    try {
      await userApi.register({
        username: values.username,
        password: values.password,
        phone: values.phone,
        nickname: values.nickname
      });
      message.success('注册成功，请登录');
      navigate('/user/login');
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: '#f0f2f5' }}>
      <Card title="用户注册" style={{ width: 420 }}>
        <Typography.Paragraph type="secondary">
          填写用户名、手机号和密码完成注册。
        </Typography.Paragraph>
        <Form layout="vertical" onFinish={onFinish}>
          <Form.Item name="username" label="用户名" rules={[{ required: true, message: '请输入用户名' }]}
            >
            <Input placeholder="例如：foodie99" />
          </Form.Item>
          <Form.Item name="nickname" label="昵称" rules={[{ required: true, message: '请输入昵称' }]}
            >
            <Input placeholder="例如：美食达人" />
          </Form.Item>
          <Form.Item name="phone" label="手机号" rules={[{ validator: phoneValidator }]}
            >
            <Input placeholder="11位手机号码" maxLength={11} />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]} hasFeedback
            >
            <Input.Password placeholder="至少6位字符" />
          </Form.Item>
          <Form.Item name="confirmPassword" label="确认密码" dependencies={['password']} hasFeedback rules={[{ required: true, message: '请再次输入密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                }
              })]}
            >
            <Input.Password placeholder="再次输入密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              注册
            </Button>
          </Form.Item>
        </Form>
        <Space style={{ width: '100%', justifyContent: 'space-between' }}>
          <Button type="link" onClick={() => navigate('/user/login')}>
            已有账号？去登录
          </Button>
          <Button type="link" onClick={() => navigate('/')}>返回主入口</Button>
        </Space>
      </Card>
    </div>
  );
};

export default Register;
