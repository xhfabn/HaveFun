import React, { useEffect, useMemo, useState } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  Select,
  Switch,
  message,
  Tag
} from 'antd';
import dayjs from 'dayjs';
import adminApi from '../../api/admin';

const DEFAULT_PAGINATION = { current: 1, pageSize: 10, total: 0 };

const EmployeeList = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState({ name: '', phone: '', status: undefined });
  const [pagination, setPagination] = useState(DEFAULT_PAGINATION);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [form] = Form.useForm();
  const [filterForm] = Form.useForm();

  const fetchData = async (page = pagination.current, pageSize = pagination.pageSize, extraFilters = filters) => {
    setLoading(true);
    try {
      const res = await adminApi.fetchEmployees({
        page,
        pageSize,
        name: extraFilters.name || undefined,
        phone: extraFilters.phone || undefined,
        status: typeof extraFilters.status === 'number' ? extraFilters.status : undefined
      });
      setData(res?.records || []);
      setPagination(prev => ({ ...prev, current: page, pageSize, total: res?.total || 0 }));
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载员工列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const openModal = record => {
    setEditingRecord(record || null);
    setModalVisible(true);
    form.resetFields();
    if (record) {
      form.setFieldsValue({ ...record });
    } else {
      form.setFieldsValue({ status: 1 });
    }
  };

  const handleSubmit = async values => {
    try {
      if (editingRecord) {
        await adminApi.updateEmployee({ ...editingRecord, ...values });
        message.success('员工信息已更新');
      } else {
        await adminApi.createEmployee(values);
        message.success('员工已创建，默认密码 123456');
      }
      setModalVisible(false);
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '保存失败');
    }
  };

  const handleStatusChange = async (checked, record) => {
    try {
      await adminApi.toggleEmployeeStatus(checked ? 1 : 0, record.id);
      message.success('状态已更新');
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '更新状态失败');
    }
  };

  const handleTableChange = ({ current, pageSize }) => {
    fetchData(current, pageSize);
  };

  const columns = useMemo(
    () => [
      { title: '姓名', dataIndex: 'name' },
      { title: '账号', dataIndex: 'username' },
      { title: '手机号', dataIndex: 'phone' },
      {
        title: '性别',
        dataIndex: 'sex',
        render: value => {
          if (value === '1') return '男';
          if (value === '0') return '女';
          return '-';
        }
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        render: value => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')
      },
      {
        title: '状态',
        dataIndex: 'status',
        render: (_, record) => (
          <Space>
            <Tag color={record.status === 1 ? 'green' : 'red'}>{record.status === 1 ? '启用' : '禁用'}</Tag>
            <Switch checked={record.status === 1} onChange={checked => handleStatusChange(checked, record)} />
          </Space>
        )
      },
      {
        title: '操作',
        render: (_, record) => (
          <Button type="link" onClick={() => openModal(record)}>
            编辑
          </Button>
        )
      }
    ],
    []
  );

  return (
    <div>
      <Form
        form={filterForm}
        layout="inline"
        onFinish={values => {
          setFilters(values);
          fetchData(1, pagination.pageSize, values);
        }}
        style={{ marginBottom: 16 }}
      >
        <Form.Item name="name">
          <Input placeholder="Name" allowClear />
        </Form.Item>
        <Form.Item name="phone">
          <Input placeholder="Phone" allowClear />
        </Form.Item>
        <Form.Item name="status">
          <Select
            placeholder="状态"
            allowClear
            options={[
              { label: '启用', value: 1 },
              { label: '禁用', value: 0 }
            ]}
          />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit">
              查询
            </Button>
            <Button
              onClick={() => {
                filterForm.resetFields();
                setFilters({ name: '', phone: '', status: undefined });
                fetchData();
              }}
            >
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>

      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => openModal()}>
          新增员工
        </Button>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={data}
        pagination={{ ...pagination }}
        onChange={handleTableChange}
      />

      <Modal
        title={editingRecord ? '编辑员工' : '新增员工'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={() => form.submit()}
        destroyOnClose
      >
        <Form layout="vertical" form={form} onFinish={handleSubmit} preserve={false}>
          <Form.Item name="name" label="姓名" rules={[{ required: true, message: '请输入姓名' }]}
            ><Input maxLength={20} /></Form.Item>
          <Form.Item
            name="username"
            label="账号"
            rules={[{ required: true, message: '请输入账号' }]}
          >
            <Input disabled={!!editingRecord} maxLength={50} />
          </Form.Item>
          <Form.Item
            name="phone"
            label="手机号"
            rules={[
              { required: true, message: '请输入手机号' },
              { pattern: /^1\d{10}$/, message: '手机号格式不正确' }
            ]}
          >
            <Input maxLength={11} />
          </Form.Item>
          <Form.Item name="sex" label="性别" rules={[{ required: true, message: '请选择性别' }]}>
            <Select options={[{ label: '男', value: '1' }, { label: '女', value: '0' }]} />
          </Form.Item>
          <Form.Item
            name="idNumber"
            label="身份证号"
            rules={[
              { required: true, message: '请输入身份证号' },
              { min: 18, max: 18, message: '身份证号应为18位' }
            ]}
          >
            <Input maxLength={18} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default EmployeeList;
