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
import ImageUploader from '../../components/ImageUploader';

const DEFAULT_PAGINATION = { current: 1, pageSize: 10, total: 0 };

const formatFlavorValueForForm = value => {
  if (!value) return '';
  try {
    const arr = JSON.parse(value);
    if (Array.isArray(arr)) {
      return arr.join(',');
    }
  } catch (e) {
    // ignore malformed JSON
  }
  return value;
};

const serializeFlavorValue = value => {
  if (!value) return '';
  try {
    JSON.parse(value);
    return value;
  } catch (e) {
    const arr = value
      .split(',')
      .map(item => item.trim())
      .filter(Boolean);
    return arr.length ? JSON.stringify(arr) : '';
  }
};

const normalizeFlavorsForForm = flavors => {
  if (!flavors || !flavors.length) {
    return [{ name: '', value: '' }];
  }
  return flavors.map(item => ({ ...item, value: formatFlavorValueForForm(item.value) }));
};

const buildFlavorPayload = flavors => {
  if (!Array.isArray(flavors)) return [];
  return flavors
    .filter(item => item && item.name)
    .map(item => ({
      ...item,
      value: serializeFlavorValue(item.value)
    }));
};

const DishList = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState(DEFAULT_PAGINATION);
  const [filters, setFilters] = useState({ name: '', categoryId: undefined, status: undefined });
  const [categories, setCategories] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();
  const [filterForm] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const fetchCategories = async () => {
    try {
      const list = await adminApi.listCategoriesByType(1);
      setCategories(list || []);
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载分类失败');
    }
  };

  const fetchData = async (page = pagination.current, pageSize = pagination.pageSize, extraFilters = filters) => {
    setLoading(true);
    try {
      const res = await adminApi.fetchDishPage({
        page,
        pageSize,
        name: extraFilters.name || undefined,
        categoryId: extraFilters.categoryId || undefined,
        status: extraFilters.status
      });
      setData(res?.records || []);
      setPagination(prev => ({ ...prev, current: page, pageSize, total: res?.total || 0 }));
      setSelectedRowKeys([]);
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const openModal = record => {
    setEditing(record || null);
    setModalVisible(true);
    form.resetFields();
    if (record) {
      form.setFieldsValue({
        ...record,
        price: record.price !== undefined && record.price !== null ? Number(record.price) : undefined,
        flavors: normalizeFlavorsForForm(record.flavors)
      });
    } else {
      form.setFieldsValue({ status: 1, flavors: [{ name: '', value: '' }] });
    }
  };

  const submitForm = async values => {
    try {
      const payload = {
        ...values,
        price: values.price !== undefined && values.price !== null ? Number(values.price) : undefined,
        flavors: buildFlavorPayload(values.flavors)
      };
      if (editing) {
        await adminApi.updateDish({ ...editing, ...payload });
        message.success('菜品已更新');
      } else {
        await adminApi.createDish(payload);
        message.success('菜品已创建');
      }
      setModalVisible(false);
      setEditing(null);
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '保存失败');
    }
  };

  const handleStatus = async (checked, record) => {
    try {
      await adminApi.toggleDishStatus(checked ? 1 : 0, record.id);
      message.success('状态已更新');
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '更新状态失败');
    }
  };

  const handleDelete = async id => {
    try {
      await adminApi.deleteDish([id]);
      message.success('菜品已删除');
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '删除失败，可能已关联套餐');
    }
  };

  const columns = [
    { title: '菜品', dataIndex: 'name' },
    {
      title: '图片',
      dataIndex: 'image',
      render: value => (
        value ? (
          <img
            src={value}
            alt="dish"
            style={{ width: 56, height: 56, objectFit: 'cover', borderRadius: 4 }}
          />
        ) : (
          '-'
        )
      )
    },
    { title: '分类', dataIndex: 'categoryName' },
    {
      title: '价格',
      dataIndex: 'price',
      render: value => `¥${value}`
    },
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
          <Tag color={record.status === 1 ? 'green' : 'red'}>
            {record.status === 1 ? '起售' : '停售'}
          </Tag>
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
          <Popconfirm title="确认删除该菜品？" onConfirm={() => handleDelete(record.id)}>
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
        form={filterForm}
        layout="inline"
        onFinish={values => {
          setFilters(values);
          fetchData(1, pagination.pageSize, values);
        }}
        style={{ marginBottom: 16 }}
      >
        <Form.Item name="name">
          <Input placeholder="菜品名称" allowClear />
        </Form.Item>
        <Form.Item name="categoryId">
          <Select
            placeholder="分类"
            allowClear
            style={{ width: 160 }}
            options={categories.map(item => ({ label: item.name, value: item.id }))}
          />
        </Form.Item>
        <Form.Item name="status">
          <Select allowClear placeholder="状态" style={{ width: 140 }}>
            <Select.Option value={1}>起售</Select.Option>
            <Select.Option value={0}>停售</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit">
              查询
            </Button>
            <Button
              onClick={() => {
                filterForm.resetFields();
                setFilters({ name: '', categoryId: undefined, status: undefined });
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
          新增菜品
        </Button>
        {selectedRowKeys.length > 0 && (
          <Popconfirm
            title={`确认删除选中的 ${selectedRowKeys.length} 个菜品？`}
            onConfirm={async () => {
              try {
                await adminApi.deleteDish(selectedRowKeys);
                message.success('批量删除成功');
                setSelectedRowKeys([]);
                fetchData();
              } catch (error) {
                console.error(error);
                message.error(error.message || '批量删除失败');
              }
            }}
          >
            <Button danger>批量删除</Button>
          </Popconfirm>
        )}
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={data}
        pagination={pagination}
        rowSelection={{ selectedRowKeys, onChange: setSelectedRowKeys }}
        onChange={({ current, pageSize }) => fetchData(current, pageSize)}
      />

      <Modal
        title={editing ? '编辑菜品' : '新增菜品'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          setEditing(null);
        }}
        onOk={() => form.submit()}
        width={720}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={submitForm}>
          <Form.Item name="name" label="菜品名称" rules={[{ required: true, message: '请输入菜品名称' }]}
            ><Input /></Form.Item>
          <Form.Item name="categoryId" label="所属分类" rules={[{ required: true, message: '请选择分类' }]}
            ><Select options={categories.map(item => ({ label: item.name, value: item.id }))} /></Form.Item>
          <Form.Item name="price" label="价格 (元)" rules={[{ required: true, message: '请输入价格' }]}
            ><InputNumber min={0} precision={2} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}
            ><Select options={[{ label: '起售', value: 1 }, { label: '停售', value: 0 }]} /></Form.Item>
          <Form.Item name="image" label="商品图片">
            <ImageUploader />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.List name="flavors">
            {(fields, { add, remove }) => (
              <div>
                <div style={{ marginBottom: 8 }}>口味配置</div>
                {fields.map(field => (
                  <Space key={field.key} align="baseline" style={{ marginBottom: 8 }}>
                    <Form.Item
                      {...field}
                      name={[field.name, 'name']}
                      rules={[{ required: true, message: '请输入口味名称' }]}
                    >
                      <Input placeholder="口味名称，如 辣度" />
                    </Form.Item>
                    <Form.Item
                      {...field}
                      name={[field.name, 'value']}
                      rules={[{ required: true, message: '请输入口味取值' }]}
                    >
                      <Input placeholder='多值用英文逗号分隔，如 "不辣,微辣,中辣"' />
                    </Form.Item>
                    <Button type="link" danger onClick={() => remove(field.name)}>
                      删除
                    </Button>
                  </Space>
                ))}
                <Button type="dashed" onClick={() => add()} block>
                  新增口味
                </Button>
              </div>
            )}
          </Form.List>
        </Form>
      </Modal>
    </div>
  );
};

export default DishList;
