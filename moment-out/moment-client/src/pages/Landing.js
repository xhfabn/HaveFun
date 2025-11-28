import React from 'react';
import { Card, Button, Typography, Space } from 'antd';
import { useNavigate } from 'react-router-dom';

const Landing = () => {
  const navigate = useNavigate();

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #f0f5ff, #fff1f0)'
      }}
    >
      <Card
        style={{ width: 420, textAlign: 'center', boxShadow: '0 12px 40px rgba(0, 0, 0, 0.08)' }}
        title={<Typography.Title level={4} style={{ marginBottom: 0 }}>请选择入口</Typography.Title>}
      >
        <Typography.Paragraph type="secondary" style={{ marginBottom: 24 }}>
          选择管理端或用户端进入对应的操作界面
        </Typography.Paragraph>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <Button type="primary" size="large" block onClick={() => navigate('/admin/login')}>
            进入管理后台
          </Button>
          <Button size="large" block onClick={() => navigate('/user/login')}>
            进入用户端
          </Button>
        </Space>
      </Card>
    </div>
  );
};

export default Landing;
