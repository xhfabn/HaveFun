import React, { useEffect, useState } from 'react';
import {
  Card,
  Col,
  Row,
  Statistic,
  Space,
  Switch,
  Typography,
  message,
  Descriptions,
  Alert
} from 'antd';
import { CheckCircleOutlined, ExclamationCircleOutlined, CloudUploadOutlined } from '@ant-design/icons';
import adminApi from '../../api/admin';
import ImageUploader from '../../components/ImageUploader';

const statusText = status => (status === 1 ? '营业中' : '打烊');

const formatPercent = value => `${((value || 0) * 100).toFixed(2)}%`;

const formatCurrency = value => `¥${Number(value || 0).toFixed(2)}`;

const Settings = () => {
  const [shopStatus, setShopStatus] = useState(0);
  const [statusLoading, setStatusLoading] = useState(false);
  const [business, setBusiness] = useState(null);
  const [orderOverview, setOrderOverview] = useState(null);
  const [dishOverview, setDishOverview] = useState(null);
  const [setmealOverview, setSetmealOverview] = useState(null);
  const [testImage, setTestImage] = useState('');

  const loadStatus = async () => {
    setStatusLoading(true);
    try {
      const status = await adminApi.getShopStatus();
      setShopStatus(typeof status === 'number' ? status : 0);
    } catch (error) {
      console.error(error);
      message.error(error.message || '获取营业状态失败');
    } finally {
      setStatusLoading(false);
    }
  };

  const loadWorkspaceSnapshots = async () => {
    try {
      const [businessData, orders, dishes, setmeals] = await Promise.all([
        adminApi.fetchBusinessSnapshot(),
        adminApi.fetchOrderOverview(),
        adminApi.fetchDishOverview(),
        adminApi.fetchSetmealOverview()
      ]);
      setBusiness(businessData || null);
      setOrderOverview(orders || null);
      setDishOverview(dishes || null);
      setSetmealOverview(setmeals || null);
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载工作台数据失败');
    }
  };

  useEffect(() => {
    loadStatus();
    loadWorkspaceSnapshots();
  }, []);

  const toggleShopStatus = async checked => {
    setStatusLoading(true);
    try {
      await adminApi.updateShopStatus(checked ? 1 : 0);
      setShopStatus(checked ? 1 : 0);
      message.success(`已${checked ? '开启' : '暂停'}营业`);
    } catch (error) {
      console.error(error);
      message.error(error.message || '更新营业状态失败');
    } finally {
      setStatusLoading(false);
    }
  };

  const renderOverviewCard = (title, stats = []) => (
    <Card title={title} size="small">
      <Space direction="vertical" style={{ width: '100%' }}>
        {stats.map(item => (
          <div key={item.label} style={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography.Text type="secondary">{item.label}</Typography.Text>
            <Typography.Text strong>{item.value}</Typography.Text>
          </div>
        ))}
      </Space>
    </Card>
  );

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={12}>
          <Card
            title="营业状态"
            extra={(
              <Space>
                <Typography.Text type="secondary">{statusText(shopStatus)}</Typography.Text>
                <Switch
                  checkedChildren="营业"
                  unCheckedChildren="打烊"
                  checked={shopStatus === 1}
                  loading={statusLoading}
                  onChange={toggleShopStatus}
                />
              </Space>
            )}
          >
            <Alert
              type="info"
              showIcon
              message="该开关会实时影响用户端下单，请谨慎操作。"
              style={{ marginBottom: 12 }}
            />
            <Descriptions column={1} size="small" bordered>
              <Descriptions.Item label="当前状态">{statusText(shopStatus)}</Descriptions.Item>
              <Descriptions.Item label="最近同步">
                {new Date().toLocaleString('zh-CN')}
              </Descriptions.Item>
              <Descriptions.Item label="操作提示">
                切换后请确认配送、客服等同步更新。
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="OSS 上传检测" extra={<CloudUploadOutlined />}>
            <Alert
              type="success"
              icon={<CheckCircleOutlined />}
              message="系统已对接阿里云 OSS，上传后返回的 URL 会自动回填。"
              style={{ marginBottom: 12 }}
            />
            <Typography.Paragraph type="secondary">
              可使用下方上传控件进行一次测试，确保 AccessKey、Bucket、域名配置正确。
            </Typography.Paragraph>
            <ImageUploader value={testImage} onChange={setTestImage} />
            {testImage && (
              <Typography.Paragraph copyable code style={{ marginTop: 8 }}>
                {testImage}
              </Typography.Paragraph>
            )}
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日营业额"
              value={business ? formatCurrency(business.turnover) : '¥0.00'}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="有效订单" value={business?.validOrderCount || 0} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="订单完成率" value={business ? formatPercent(business.orderCompletionRate) : '0%'} />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="新增用户" value={business?.newUsers || 0} />
          </Card>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={8}>
          {renderOverviewCard('订单概览', [
            { label: '待接单', value: orderOverview?.waitingOrders ?? '-' },
            { label: '待派送', value: orderOverview?.deliveredOrders ?? '-' },
            { label: '已完成', value: orderOverview?.completedOrders ?? '-' },
            { label: '已取消', value: orderOverview?.cancelledOrders ?? '-' },
            { label: '全部订单', value: orderOverview?.allOrders ?? '-' }
          ])}
        </Col>
        <Col span={8}>
          {renderOverviewCard('菜品概览', [
            { label: '启售菜品', value: dishOverview?.sold ?? '-' },
            { label: '停售菜品', value: dishOverview?.discontinued ?? '-' }
          ])}
        </Col>
        <Col span={8}>
          {renderOverviewCard('套餐概览', [
            { label: '启售套餐', value: setmealOverview?.sold ?? '-' },
            { label: '停售套餐', value: setmealOverview?.discontinued ?? '-' }
          ])}
        </Col>
      </Row>

      <Alert
        type="warning"
        showIcon
        icon={<ExclamationCircleOutlined />}
        message="提示"
        description="如需调整 OSS 配置、配送范围等系统级参数，请联系运维或更新后端配置文件。"
        style={{ marginTop: 16 }}
      />
    </div>
  );
};

export default Settings;
