import React, { useEffect, useState } from 'react';
import { Card, Tabs, List, Tag, Button, Space, message, Drawer, Descriptions } from 'antd';
import { useNavigate } from 'react-router-dom';
import userApi from '../../api/user';

const STATUS_OPTIONS = [
  { key: 'all', label: '全部', status: undefined },
  { key: '1', label: '待付款', status: 1 },
  { key: '2', label: '待接单', status: 2 },
  { key: '3', label: '已接单', status: 3 },
  { key: '4', label: '派送中', status: 4 },
  { key: '5', label: '已完成', status: 5 },
  { key: '6', label: '已取消', status: 6 }
];

const statusMeta = value => {
  const map = {
    1: { text: '待付款', color: 'default' },
    2: { text: '待接单', color: 'gold' },
    3: { text: '已接单', color: 'blue' },
    4: { text: '派送中', color: 'cyan' },
    5: { text: '已完成', color: 'green' },
    6: { text: '已取消', color: 'red' }
  };
  return map[value] || { text: '未知', color: 'default' };
};

const UserOrders = () => {
  const navigate = useNavigate();
  const [activeKey, setActiveKey] = useState('all');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 5, total: 0 });
  const [detail, setDetail] = useState(null);
  const [detailVisible, setDetailVisible] = useState(false);

  const fetchData = async (page = pagination.current, pageSize = pagination.pageSize, key = activeKey) => {
    setLoading(true);
    try {
      const tab = STATUS_OPTIONS.find(item => item.key === key);
      const res = await userApi.fetchOrders({ page, pageSize, status: tab?.status });
      setData(res?.records || []);
      setPagination(prev => ({ ...prev, current: page, pageSize, total: res?.total || 0 }));
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleTabChange = key => {
    setActiveKey(key);
    fetchData(1, pagination.pageSize, key);
  };

  const handleCancel = async id => {
    try {
      await userApi.cancelOrder(id);
      message.success('订单已取消');
      fetchData();
    } catch (error) {
      console.error(error);
    }
  };

  const handleRepeat = async id => {
    try {
      await userApi.repeatOrder(id);
      message.success('已加入当前购物车');
    } catch (error) {
      console.error(error);
    }
  };

  const handleRemind = async id => {
    try {
      await userApi.remindOrder(id);
      message.success('已催单');
    } catch (error) {
      console.error(error);
    }
  };

  const openDetail = async id => {
    try {
      const res = await userApi.fetchOrderDetail(id);
      setDetail(res);
      setDetailVisible(true);
    } catch (error) {
      console.error(error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('userToken');
    localStorage.removeItem('userInfo');
    navigate('/user/login');
  };

  return (
    <Card
      title="我的订单"
      bodyStyle={{ padding: 0 }}
      extra={(
        <Space>
          <Button onClick={() => navigate('/user/menu')}>返回点餐</Button>
          <Button onClick={() => navigate('/')}>返回入口</Button>
          <Button danger onClick={handleLogout}>退出登录</Button>
        </Space>
      )}
    >
      <Tabs activeKey={activeKey} onChange={handleTabChange} items={STATUS_OPTIONS.map(item => ({ key: item.key, label: item.label }))} />
      <List
        loading={loading}
        dataSource={data}
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          onChange: (page, pageSize) => fetchData(page, pageSize)
        }}
        renderItem={item => {
          const meta = statusMeta(item.status);
          return (
            <List.Item
              actions={[
                item.status <= 2 && (
                  <Button type="link" danger onClick={() => handleCancel(item.id)}>
                    取消
                  </Button>
                ),
                item.status <= 4 && item.status !== 6 && (
                  <Button type="link" onClick={() => handleRemind(item.id)}>
                    催单
                  </Button>
                ),
                <Button type="link" onClick={() => handleRepeat(item.id)}>
                  再来一单
                </Button>,
                <Button type="link" onClick={() => openDetail(item.id)}>
                  详情
                </Button>
              ].filter(Boolean)}
            >
              <List.Item.Meta
                title={
                  <Space>
                    <span>订单号：{item.number}</span>
                    <Tag color={meta.color}>{meta.text}</Tag>
                  </Space>
                }
                description={`金额：¥${item.amount} ｜ 下单时间：${item.orderTime}`}
              />
            </List.Item>
          );
        }}
      />

      <Drawer title="订单详情" width={480} open={detailVisible} onClose={() => setDetailVisible(false)} destroyOnClose>
        {detail && (
          <>
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="订单号">{detail.number}</Descriptions.Item>
              <Descriptions.Item label="状态">{statusMeta(detail.status).text}</Descriptions.Item>
              <Descriptions.Item label="金额">¥{detail.amount}</Descriptions.Item>
              <Descriptions.Item label="下单时间">{detail.orderTime}</Descriptions.Item>
              <Descriptions.Item label="收货人">{detail.consignee}</Descriptions.Item>
              <Descriptions.Item label="联系电话">{detail.phone}</Descriptions.Item>
              <Descriptions.Item label="地址">{detail.address}</Descriptions.Item>
              <Descriptions.Item label="备注">{detail.remark || '-'}</Descriptions.Item>
            </Descriptions>
            <List
              header={<div>菜品</div>}
              dataSource={detail.orderDetailList || []}
              renderItem={d => (
                <List.Item>
                  <Space>
                    <span>{d.name}</span>
                    <span>×{d.number}</span>
                    <span>¥{d.amount}</span>
                  </Space>
                </List.Item>
              )}
            />
          </>
        )}
      </Drawer>
    </Card>
  );
};

export default UserOrders;
