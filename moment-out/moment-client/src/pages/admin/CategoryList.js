import React, { useEffect, useState } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  Switch,
  Popconfirm,
  message,
  Tag
} from 'antd';
import dayjs from 'dayjs';
import adminApi from '../../api/admin';

const CATEGORY_TYPES = [
  { label: '菜品分类', value: 1 },
  { label: '套餐分类', value: 2 }
];

const DEFAULT_PAGINATION = { current: 1, pageSize: 10, total: 0 };

const CategoryList = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState(DEFAULT_PAGINATION);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();
  const [filterForm] = Form.useForm();
  const [filters, setFilters] = useState({ type: undefined, name: '' });

  const fetchData = async (
    page = pagination.current,
    pageSize = pagination.pageSize,
    extraFilters = filters
  ) => {
    setLoading(true);
    try {
      const res = await adminApi.fetchCategoryPage({
        page,
        pageSize,
        name: extraFilters.name || undefined,
        type: extraFilters.type || undefined
      });
      setData(res?.records || []);
      setPagination(prev => ({ ...prev, current: page, pageSize, total: res?.total || 0 }));
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载分类失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const openModal = record => {
    setEditing(record || null);
    setModalVisible(true);
    form.resetFields();
    if (record) {
      form.setFieldsValue(record);
    } else {
      form.setFieldsValue({ sort: 0, type: 1 });
    }
  };

  const submitForm = async values => {
    try {
      if (editing) {
        await adminApi.updateCategory({ ...editing, ...values });
        message.success('分类已更新');
      } else {
        await adminApi.createCategory(values);
        message.success('分类已创建');
      }
      setModalVisible(false);
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '保存失败');
    }
  };

  const handleStatus = async (checked, record) => {
    try {
      await adminApi.toggleCategoryStatus(checked ? 1 : 0, record.id);
      message.success('状态已更新');
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '更新状态失败');
    }
  };

  const handleDelete = async id => {
    try {
      await adminApi.deleteCategory(id);
      message.success('分类已删除');
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '删除失败，可能已绑定菜品/套餐');
    }
  };

  const columns = [
    { title: '名称', dataIndex: 'name' },
    {
      title: '类型',
      dataIndex: 'type',
      render: value => CATEGORY_TYPES.find(item => item.value === value)?.label || '-'
    },
    { title: '排序', dataIndex: 'sort', sorter: true },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      render: value => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')
    },
    {
      title: '状态',
      dataIndex: 'status',
      render: (_, record) => (
        <Space>
          <Tag color={record.status === 1 ? 'green' : 'red'}>{record.status === 1 ? '启用' : '禁用'}</Tag>
          <Switch checked={record.status === 1} onChange={checked => handleStatus(checked, record)} />
        </Space>
      )
    },
    {
      title: '操作',
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => openModal(record)}>
            编辑
          </Button>
          <Popconfirm title="确认删除该分类？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div>
      <Form
        layout="inline"
        form={filterForm}
        style={{ marginBottom: 16 }}
        onFinish={values => {
          setFilters(values);
          fetchData(1, pagination.pageSize, values);
        }}
      >
        <Form.Item name="name">
          <Input placeholder="分类名称" allowClear />
        </Form.Item>
        <Form.Item name="type">
          <Select placeholder="分类类型" allowClear options={CATEGORY_TYPES} style={{ width: 160 }} />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit">
              查询
            </Button>
            <Button
              onClick={() => {
                filterForm.resetFields();
                setFilters({ name: '', type: undefined });
                fetchData(1, pagination.pageSize, { name: '', type: undefined });
              }}
            >
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => openModal()}>
          新增分类
        </Button>
      </Space>
      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={data}
        pagination={pagination}
        onChange={({ current, pageSize }) => fetchData(current, pageSize)}
      />

      <Modal
        title={editing ? '编辑分类' : '新增分类'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={() => form.submit()}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={submitForm}>
          <Form.Item
            name="name"
            label="分类名称"
            rules={[{ required: true, message: '请输入分类名称' }]}
          >
            <Input maxLength={32} />
          </Form.Item>
          <Form.Item
            name="type"
            label="分类类型"
            rules={[{ required: true, message: '请选择分类类型' }]}
          >
            <Select options={CATEGORY_TYPES} placeholder="请选择" />
          </Form.Item>
          <Form.Item name="sort" label="排序" initialValue={0}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default CategoryList;
