import React, { useEffect, useMemo, useState } from 'react';
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
  Tag,
  Divider,
  Spin
} from 'antd';
import dayjs from 'dayjs';
import adminApi from '../../api/admin';
import ImageUploader from '../../components/ImageUploader';

const DEFAULT_PAGINATION = { current: 1, pageSize: 10, total: 0 };

const normalizeSetmealDishes = list => {
  if (!list || !list.length) {
    return [{ dishId: undefined, copies: 1 }];
  }
  return list.map(item => ({
    ...item,
    copies: item.copies || 1,
    price: item.price !== undefined && item.price !== null ? Number(item.price) : undefined
  }));
};

const prepareDishPayload = (dishes, dishMap) => {
  if (!Array.isArray(dishes)) return [];
  return dishes
    .filter(item => item && item.dishId)
    .map(item => {
      const linkedDish = dishMap[item.dishId];
      return {
        ...item,
        name: item.name || linkedDish?.name,
        price:
          item.price !== undefined && item.price !== null
            ? Number(item.price)
            : linkedDish?.price !== undefined
              ? Number(linkedDish.price)
              : undefined,
        copies: item.copies ? Number(item.copies) : 1
      };
    })
    .filter(item => item.name && item.price !== undefined);
};

const SetmealList = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState(DEFAULT_PAGINATION);
  const [filters, setFilters] = useState({ name: '', categoryId: undefined, status: undefined });
  const [categories, setCategories] = useState([]);
  const [dishOptions, setDishOptions] = useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [form] = Form.useForm();
  const [filterForm] = Form.useForm();

  const dishMap = useMemo(() => {
    const map = {};
    dishOptions.forEach(item => {
      map[item.id] = item;
    });
    return map;
  }, [dishOptions]);

  const fetchCategories = async () => {
    try {
      const list = await adminApi.listCategoriesByType(2);
      setCategories(list || []);
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载套餐分类失败');
    }
  };

  const fetchDishOptions = async () => {
    try {
      const res = await adminApi.fetchDishPage({ page: 1, pageSize: 1000, status: 1 });
      setDishOptions(res?.records || []);
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载菜品列表失败');
    }
  };

  const fetchData = async (
    page = pagination.current,
    pageSize = pagination.pageSize,
    extraFilters = filters
  ) => {
    setLoading(true);
    try {
      const res = await adminApi.fetchSetmealPage({
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
    fetchDishOptions();
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const openModal = async record => {
    setModalVisible(true);
    setEditing(record || null);
    form.resetFields();
    if (record?.id) {
      setDetailLoading(true);
      try {
        const detail = await adminApi.getSetmealDetail(record.id);
        form.setFieldsValue({
          ...detail,
          price:
            detail?.price !== undefined && detail?.price !== null
              ? Number(detail.price)
              : undefined,
          setmealDishes: normalizeSetmealDishes(detail?.setmealDishes)
        });
      } catch (error) {
        console.error(error);
        message.error(error.message || '加载套餐详情失败');
      } finally {
        setDetailLoading(false);
      }
    } else {
      form.setFieldsValue({ status: 1, setmealDishes: [{ dishId: undefined, copies: 1 }] });
    }
  };

  const handleDishSelect = (fieldIndex, dishId) => {
    const target = dishMap[dishId];
    const list = form.getFieldValue('setmealDishes') || [];
    list[fieldIndex] = {
      ...list[fieldIndex],
      dishId,
      name: target?.name,
      price: target?.price !== undefined && target?.price !== null ? Number(target.price) : undefined,
      copies: list[fieldIndex]?.copies || 1
    };
    form.setFieldsValue({ setmealDishes: list });
  };

  const submitForm = async values => {
    try {
      const payload = {
        ...values,
        price: values.price !== undefined && values.price !== null ? Number(values.price) : undefined,
        setmealDishes: prepareDishPayload(values.setmealDishes, dishMap)
      };
      if (!payload.setmealDishes.length) {
        message.warning('请至少选择一个菜品');
        return;
      }
      if (editing) {
        await adminApi.updateSetmeal({ ...editing, ...payload });
        message.success('套餐已更新');
      } else {
        await adminApi.createSetmeal(payload);
        message.success('套餐已创建');
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
      await adminApi.toggleSetmealStatus(checked ? 1 : 0, record.id);
      message.success('状态已更新');
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '更新状态失败');
    }
  };

  const handleDelete = async ids => {
    try {
      await adminApi.deleteSetmeal(ids);
      message.success('套餐已删除');
      fetchData();
    } catch (error) {
      console.error(error);
      message.error(error.message || '删除失败，可能已关联订单');
    }
  };

  const columns = [
    { title: '套餐名称', dataIndex: 'name' },
    { title: '分类', dataIndex: 'categoryName' },
    {
      title: '价格',
      dataIndex: 'price',
      render: value => (value !== undefined && value !== null ? `¥${value}` : '-')
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
          <Popconfirm title="确认删除该套餐？" onConfirm={() => handleDelete([record.id])}>
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
          <Input placeholder="套餐名称" allowClear />
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
          新增套餐
        </Button>
        {selectedRowKeys.length > 0 && (
          <Popconfirm
            title={`确认删除选中的 ${selectedRowKeys.length} 个套餐？`}
            onConfirm={() => handleDelete(selectedRowKeys)}
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
        title={editing ? '编辑套餐' : '新增套餐'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          setEditing(null);
        }}
        onOk={() => form.submit()}
        width={820}
        destroyOnClose
      >
        <Spin spinning={detailLoading}>
          <Form form={form} layout="vertical" onFinish={submitForm}>
            <Form.Item name="name" label="套餐名称" rules={[{ required: true, message: '请输入套餐名称' }]}
              ><Input />
            </Form.Item>
            <Form.Item name="categoryId" label="所属分类" rules={[{ required: true, message: '请选择分类' }]}
              ><Select options={categories.map(item => ({ label: item.name, value: item.id }))} />
            </Form.Item>
            <Form.Item name="price" label="价格 (元)" rules={[{ required: true, message: '请输入价格' }]}
              ><InputNumber min={0} precision={2} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="status" label="状态" initialValue={1}
              ><Select options={[{ label: '起售', value: 1 }, { label: '停售', value: 0 }]} />
            </Form.Item>
            <Form.Item name="image" label="套餐图片">
              <ImageUploader />
            </Form.Item>
            <Form.Item name="description" label="描述">
              <Input.TextArea rows={3} />
            </Form.Item>

            <Divider orientation="left">套餐菜品</Divider>
            <Form.List name="setmealDishes">
              {(fields, { add, remove }) => (
                <div>
                  {fields.map(field => (
                    <Space key={field.key} align="baseline" style={{ display: 'flex', marginBottom: 12 }}>
                      <Form.Item
                        {...field}
                        name={[field.name, 'dishId']}
                        fieldKey={[field.fieldKey, 'dishId']}
                        rules={[{ required: true, message: '请选择菜品' }]}
                        style={{ width: 240 }}
                      >
                        <Select
                          placeholder="选择菜品"
                          showSearch
                          optionFilterProp="label"
                          options={dishOptions.map(dish => ({
                            label: `${dish.name} (¥${dish.price})`,
                            value: dish.id
                          }))}
                          onChange={value => handleDishSelect(field.name, value)}
                        />
                      </Form.Item>
                      <Form.Item
                        {...field}
                        name={[field.name, 'price']}
                        fieldKey={[field.fieldKey, 'price']}
                        rules={[{ required: true, message: '请输入价格' }]}
                      >
                        <InputNumber min={0} precision={2} placeholder="价格" />
                      </Form.Item>
                      <Form.Item
                        {...field}
                        name={[field.name, 'copies']}
                        fieldKey={[field.fieldKey, 'copies']}
                        rules={[{ required: true, message: '请输入份数' }]}
                      >
                        <InputNumber min={1} placeholder="份数" />
                      </Form.Item>
                      <Form.Item {...field} name={[field.name, 'name']} fieldKey={[field.fieldKey, 'name']} hidden>
                        <Input />
                      </Form.Item>
                      <Button type="link" danger onClick={() => remove(field.name)}>
                        删除
                      </Button>
                    </Space>
                  ))}
                  <Button type="dashed" onClick={() => add({ copies: 1 })} block>
                    新增菜品
                  </Button>
                </div>
              )}
            </Form.List>
          </Form>
        </Spin>
      </Modal>
    </div>
  );
};

export default SetmealList;
