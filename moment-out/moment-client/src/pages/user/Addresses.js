import React, { useEffect, useState } from 'react';
import { Card, List, Button, Modal, Form, Input, Space, Select, Switch, message, Tag } from 'antd';
import { useNavigate } from 'react-router-dom';
import userApi from '../../api/user';

const UserAddresses = () => {
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await userApi.listAddresses();
      setData(res || []);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const openModal = record => {
    setEditing(record || null);
    setModalVisible(true);
    form.resetFields();
    if (record) {
      form.setFieldsValue({ ...record, isDefault: record.isDefault === 1 });
    }
  };

  const handleSubmit = async values => {
    const payload = { ...editing, ...values, isDefault: values.isDefault ? 1 : 0 };
    try {
      if (editing) {
        await userApi.updateAddress(payload);
        message.success('地址已更新');
      } else {
        await userApi.saveAddress(payload);
        message.success('地址已新增');
      }
      setModalVisible(false);
      fetchData();
    } catch (error) {
      console.error(error);
    }
  };

  const handleDelete = async id => {
    try {
      await userApi.deleteAddress(id);
      message.success('地址已删除');
      fetchData();
    } catch (error) {
      console.error(error);
    }
  };

  const handleDefault = async id => {
    try {
      await userApi.setDefaultAddress(id);
      message.success('已设置默认地址');
      fetchData();
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
      title="我的地址"
      extra={(
        <Space>
          <Button onClick={() => navigate('/user/menu')}>返回点餐</Button>
          <Button onClick={() => navigate('/')}>返回入口</Button>
          <Button danger onClick={handleLogout}>退出登录</Button>
          <Button type="primary" onClick={() => openModal()}>
            新增地址
          </Button>
        </Space>
      )}
    >
      <List
        loading={loading}
        dataSource={data}
        renderItem={item => (
          <List.Item
            actions={[
              <Button type="link" onClick={() => openModal(item)}>编辑</Button>,
              <Button type="link" danger onClick={() => handleDelete(item.id)}>删除</Button>,
              item.isDefault === 1 ? (
                <Tag color="blue">默认地址</Tag>
              ) : (
                <Button type="link" onClick={() => handleDefault(item.id)}>
                  设为默认
                </Button>
              )
            ]}
          >
            <List.Item.Meta
              title={`${item.consignee} ${item.phone}`}
              description={`${item.provinceName || ''}${item.cityName || ''}${item.districtName || ''}${item.detail || ''}`}
            />
          </List.Item>
        )}
      />

      <Modal
        title={editing ? '编辑地址' : '新增地址'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={() => form.submit()}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="consignee" label="收货人" rules={[{ required: true, message: '请输入收货人' }]}
            ><Input /></Form.Item>
          <Form.Item name="phone" label="手机号" rules={[{ required: true, message: '请输入手机号' }]}
            ><Input /></Form.Item>
          <Form.Item name="sex" label="性别">
            <Select allowClear options={[{ label: '男', value: '1' }, { label: '女', value: '0' }]} />
          </Form.Item>
          <Space size="middle">
            <Form.Item name="provinceName" label="省份">
              <Input placeholder="如：广东省" />
            </Form.Item>
            <Form.Item name="cityName" label="城市">
              <Input placeholder="如：深圳市" />
            </Form.Item>
            <Form.Item name="districtName" label="区县">
              <Input placeholder="如：南山区" />
            </Form.Item>
          </Space>
          <Form.Item name="detail" label="详细地址" rules={[{ required: true, message: '请输入详细地址' }]}
            ><Input /></Form.Item>
          <Form.Item name="label" label="标签">
            <Input placeholder="如：公司 / 家" />
          </Form.Item>
          <Form.Item name="isDefault" label="设为默认" valuePropName="checked">
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default UserAddresses;
