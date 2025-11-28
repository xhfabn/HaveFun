import React, { useEffect, useState } from 'react';
import {
  Table,
  Form,
  Input,
  Select,
  Space,
  Button,
  DatePicker,
  Tag,
  Drawer,
  Descriptions,
  Modal,
  message,
  Row,
  Col,
  Card,
  Statistic
} from 'antd';
import dayjs from 'dayjs';
import adminApi from '../../api/admin';

const { RangePicker } = DatePicker;

const ORDER_STATUS = [
  { label: '待付款', value: 1, color: 'default' },
  { label: '待接单', value: 2, color: 'gold' },
  { label: '已接单', value: 3, color: 'blue' },
  { label: '派送中', value: 4, color: 'cyan' },
  { label: '已完成', value: 5, color: 'green' },
  { label: '已取消', value: 6, color: 'red' }
];

const PAY_STATUS = {
  0: '未支付',
  1: '已支付',
  2: '已退款'
};

const DELIVERY_STATUS = {
  0: '预约送达',
  1: '立即送出'
};

const DEFAULT_PAGINATION = { current: 1, pageSize: 10, total: 0 };

const OrderList = () => {
  const [form] = Form.useForm();
  const [reasonForm] = Form.useForm();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState(DEFAULT_PAGINATION);
  const [filters, setFilters] = useState({});
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [currentOrder, setCurrentOrder] = useState(null);
  const [reasonModal, setReasonModal] = useState({ open: false, type: null, record: null });
  const [stats, setStats] = useState({ toBeConfirmed: 0, confirmed: 0, deliveryInProgress: 0 });
  const [statsLoading, setStatsLoading] = useState(false);

  const formatDateTime = value => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-');

  const fetchData = async (page = pagination.current, pageSize = pagination.pageSize, extraFilters = filters) => {
    setLoading(true);
    try {
      const params = {
        page,
        pageSize,
        number: extraFilters.number || undefined,
        phone: extraFilters.phone || undefined,
        status: extraFilters.status,
        beginTime: extraFilters.dates?.length
          ? dayjs(extraFilters.dates[0]).format('YYYY-MM-DD HH:mm:ss')
          : undefined,
        endTime: extraFilters.dates?.length
          ? dayjs(extraFilters.dates[1]).format('YYYY-MM-DD HH:mm:ss')
          : undefined
      };
      const res = await adminApi.searchOrders(params);
      setData(res?.records || []);
      setPagination(prev => ({ ...prev, current: page, pageSize, total: res?.total || 0 }));
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载订单失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async () => {
    setStatsLoading(true);
    try {
      const res = await adminApi.fetchOrderStats();
      setStats({
        toBeConfirmed: res?.toBeConfirmed || 0,
        confirmed: res?.confirmed || 0,
        deliveryInProgress: res?.deliveryInProgress || 0
      });
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载订单统计失败');
    } finally {
      setStatsLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    fetchStats();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const statusMeta = value => ORDER_STATUS.find(item => item.value === value) || { label: '未知', color: 'default' };

  const openDetail = async record => {
    try {
      const detail = await adminApi.getOrderDetail(record.id);
      setCurrentOrder(detail);
      setDrawerVisible(true);
    } catch (error) {
      console.error(error);
      message.error(error.message || '查询订单详情失败');
    }
  };

  const closeDrawer = () => {
    setDrawerVisible(false);
    setCurrentOrder(null);
  };

  const triggerReasonModal = (type, record) => {
    setReasonModal({ open: true, type, record });
    reasonForm.resetFields();
  };

  const handleReasonSubmit = async values => {
    const { type, record } = reasonModal;
    try {
      if (type === 'reject') {
        await adminApi.rejectOrder({ id: record.id, rejectionReason: values.reason });
      } else if (type === 'cancel') {
        await adminApi.cancelOrder({ id: record.id, cancelReason: values.reason });
      }
      message.success('操作成功');
      setReasonModal({ open: false, type: null, record: null });
      fetchData();
      fetchStats();
    } catch (error) {
      console.error(error);
      message.error(error.message || '操作失败');
    }
  };

  const runAction = async (action, record) => {
    try {
      if (action === 'confirm') {
        await adminApi.confirmOrder({ id: record.id, status: 3 });
      } else if (action === 'deliver') {
        await adminApi.deliverOrder(record.id);
      } else if (action === 'complete') {
        await adminApi.completeOrder(record.id);
      }
      message.success('操作成功');
      fetchData();
      fetchStats();
    } catch (error) {
      console.error(error);
      message.error(error.message || '操作失败');
    }
  };

  const handleAction = (action, record) => {
    const actionTextMap = { confirm: '接单', deliver: '派送', complete: '完成' };
    Modal.confirm({
      title: `确认${actionTextMap[action]}该订单？`,
      content: `订单号：${record.number}`,
      okText: '确认',
      cancelText: '取消',
      onOk: () => runAction(action, record)
    });
  };

  const canStatus = (record, allowedStatuses) => allowedStatuses.includes(record.status);

  const columns = [
    { title: '订单号', dataIndex: 'number' },
    { title: '用户', dataIndex: 'consignee' },
    { title: '电话', dataIndex: 'phone' },
    { title: '金额', dataIndex: 'amount', render: value => `¥${value}` },
    {
      title: '状态',
      dataIndex: 'status',
      render: value => {
        const meta = statusMeta(value);
        return <Tag color={meta.color}>{meta.label}</Tag>;
      }
    },
    {
      title: '下单时间',
      dataIndex: 'orderTime',
      render: value => formatDateTime(value)
    },
    {
      title: '操作',
      render: (_, record) => {
        const actionButtons = [
          (
            <Button
              key="confirm"
              type="link"
              disabled={!canStatus(record, [2])}
              onClick={() => canStatus(record, [2]) && handleAction('confirm', record)}
            >
              接单
            </Button>
          ),
          (
            <Button
              key="reject"
              type="link"
              danger
              disabled={!canStatus(record, [2])}
              onClick={() => canStatus(record, [2]) && triggerReasonModal('reject', record)}
            >
              拒单
            </Button>
          ),
          (
            <Button
              key="deliver"
              type="link"
              disabled={!canStatus(record, [3])}
              onClick={() => canStatus(record, [3]) && handleAction('deliver', record)}
            >
              派送
            </Button>
          ),
          (
            <Button
              key="complete"
              type="link"
              disabled={!canStatus(record, [4])}
              onClick={() => canStatus(record, [4]) && handleAction('complete', record)}
            >
              完成
            </Button>
          ),
          (
            <Button
              key="cancel"
              type="link"
              danger
              disabled={!canStatus(record, [1, 2, 3, 4])}
              onClick={() => canStatus(record, [1, 2, 3, 4]) && triggerReasonModal('cancel', record)}
            >
              取消
            </Button>
          ),
          (
            <Button key="detail" type="link" onClick={() => openDetail(record)}>
              详情
            </Button>
          )
        ];
        return <Space>{actionButtons}</Space>;
      }
    }
  ];

  return (
    <div>
      <Form
        layout="inline"
        form={form}
        style={{ marginBottom: 16 }}
        onFinish={values => {
          setFilters(values);
          fetchData(1, pagination.pageSize, values);
        }}
      >
        <Form.Item name="number">
          <Input placeholder="订单号" allowClear />
        </Form.Item>
        <Form.Item name="phone">
          <Input placeholder="手机号" allowClear />
        </Form.Item>
        <Form.Item name="status">
          <Select allowClear placeholder="状态" style={{ width: 160 }} options={ORDER_STATUS} />
        </Form.Item>
        <Form.Item name="dates">
          <RangePicker showTime allowClear />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit">
              查询
            </Button>
            <Button
              onClick={() => {
                form.resetFields();
                setFilters({});
                fetchData(1, pagination.pageSize, {});
              }}
            >
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>

      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={8}>
          <Card loading={statsLoading}>
            <Statistic title="待接单" value={stats.toBeConfirmed} suffix="单" />
          </Card>
        </Col>
        <Col span={8}>
          <Card loading={statsLoading}>
            <Statistic title="待派送" value={stats.confirmed} suffix="单" />
          </Card>
        </Col>
        <Col span={8}>
          <Card loading={statsLoading}>
            <Statistic title="派送中" value={stats.deliveryInProgress} suffix="单" />
          </Card>
        </Col>
      </Row>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={data}
        pagination={pagination}
        onChange={({ current, pageSize }) => fetchData(current, pageSize)}
      />

      <Drawer title="订单详情" width={520} open={drawerVisible} onClose={closeDrawer} destroyOnClose>
        {currentOrder && (
          <>
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="订单号">{currentOrder.number}</Descriptions.Item>
              <Descriptions.Item label="客户">{currentOrder.consignee}</Descriptions.Item>
              <Descriptions.Item label="联系电话">{currentOrder.phone}</Descriptions.Item>
              <Descriptions.Item label="地址">{currentOrder.address}</Descriptions.Item>
              <Descriptions.Item label="金额">¥{currentOrder.amount}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={statusMeta(currentOrder.status).color}>
                  {statusMeta(currentOrder.status).label}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="支付状态">
                {PAY_STATUS[currentOrder.payStatus] || '未知'}
              </Descriptions.Item>
              <Descriptions.Item label="配送方式">
                {DELIVERY_STATUS[currentOrder.deliveryStatus] || '未知'}
              </Descriptions.Item>
              <Descriptions.Item label="下单时间">{formatDateTime(currentOrder.orderTime)}</Descriptions.Item>
              <Descriptions.Item label="预计送达">{formatDateTime(currentOrder.estimatedDeliveryTime)}</Descriptions.Item>
              <Descriptions.Item label="完成时间">{formatDateTime(currentOrder.checkoutTime)}</Descriptions.Item>
              <Descriptions.Item label="备注">{currentOrder.remark || '-'}</Descriptions.Item>
              {currentOrder.cancelReason ? (
                <Descriptions.Item label="取消原因">{currentOrder.cancelReason}</Descriptions.Item>
              ) : null}
              {currentOrder.rejectionReason ? (
                <Descriptions.Item label="拒绝原因">{currentOrder.rejectionReason}</Descriptions.Item>
              ) : null}
            </Descriptions>
            <h4 style={{ marginTop: 16 }}>菜品明细</h4>
            <Table
              rowKey="id"
              dataSource={currentOrder.orderDetailList || []}
              pagination={false}
              size="small"
              columns={[
                { title: '名称', dataIndex: 'name' },
                { title: '数量', dataIndex: 'number' },
                { title: '口味', dataIndex: 'dishFlavor' },
                { title: '金额', dataIndex: 'amount', render: value => `¥${value}` }
              ]}
            />
          </>
        )}
      </Drawer>

      <Modal
        title={reasonModal.type === 'reject' ? '拒单原因' : '取消原因'}
        open={reasonModal.open}
        onCancel={() => setReasonModal({ open: false, type: null, record: null })}
        onOk={() => reasonForm.submit()}
      >
        <Form form={reasonForm} layout="vertical" onFinish={handleReasonSubmit}>
          <Form.Item name="reason" label="原因" rules={[{ required: true, message: '请输入原因' }]}
            ><Input.TextArea rows={4} /></Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default OrderList;
