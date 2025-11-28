import React, { useEffect, useMemo, useState } from 'react';
import {
  Layout,
  Menu as AntMenu,
  Card,
  List,
  Button,
  message,
  Badge,
  Drawer,
  Space,
  Typography,
  Select,
  Tag,
  Divider,
  Form,
  Input,
  InputNumber
} from 'antd';
import { ShoppingCartOutlined, HomeOutlined, HistoryOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';
import userApi from '../../api/user';

const { Header, Sider, Content } = Layout;

const MenuPage = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [currentCategoryType, setCurrentCategoryType] = useState(1);
  const [menuItems, setMenuItems] = useState([]);
  const [cartVisible, setCartVisible] = useState(false);
  const [cart, setCart] = useState([]);
  const [addresses, setAddresses] = useState([]);
  const [selectedAddress, setSelectedAddress] = useState();
  const [flavorModal, setFlavorModal] = useState({ open: false, dish: null, selections: {} });
  const [checkoutVisible, setCheckoutVisible] = useState(false);
  const [checkoutForm] = Form.useForm();

  const handleLogout = () => {
    localStorage.removeItem('userToken');
    localStorage.removeItem('userInfo');
    navigate('/user/login');
  };

  const fetchCategories = async () => {
    try {
      const res = await userApi.fetchCategories();
      setCategories(res || []);
      if (res?.length) {
        setSelectedCategory(res[0].id);
        setCurrentCategoryType(res[0].type || 1);
      }
    } catch (error) {
      console.error(error);
    }
  };

  const fetchDishes = async categoryId => {
    try {
      const res = await userApi.fetchDishList(categoryId);
      setMenuItems(res || []);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchSetmeals = async categoryId => {
    try {
      const res = await userApi.fetchSetmealList(categoryId);
      setMenuItems(res || []);
    } catch (error) {
      console.error(error);
    }
  };

  const loadCart = async () => {
    try {
      const res = await userApi.fetchCart();
      setCart(res || []);
    } catch (error) {
      console.error(error);
    }
  };

  const loadAddresses = async () => {
    try {
      const res = await userApi.listAddresses();
      setAddresses(res || []);
      if (res?.length) {
        const defaultAddr = res.find(item => item.isDefault === 1);
        setSelectedAddress(defaultAddr?.id || res[0].id);
      }
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    fetchCategories();
    loadCart();
    loadAddresses();
  }, []);

  useEffect(() => {
    if (!selectedCategory) return;
    if (currentCategoryType === 2) {
      fetchSetmeals(selectedCategory);
    } else {
      fetchDishes(selectedCategory);
    }
  }, [selectedCategory, currentCategoryType]);

  const cartTotal = useMemo(
    () => cart.reduce((sum, item) => sum + Number(item.amount || 0) * (item.number || 1), 0),
    [cart]
  );
  const cartCount = useMemo(() => cart.reduce((sum, item) => sum + (item.number || 0), 0), [cart]);

  const parseFlavorOptions = value => {
    try {
      const arr = JSON.parse(value);
      if (Array.isArray(arr)) {
        return arr;
      }
    } catch (e) {
      // ignore
    }
    return value ? value.split(',') : [];
  };

  const addDishToCart = async (dish, flavorText = '') => {
    try {
      await userApi.addCartItem({ dishId: dish.id, dishFlavor: flavorText || undefined });
      message.success('已加入购物车');
      loadCart();
    } catch (error) {
      console.error(error);
    }
  };

  const addSetmealToCart = async setmeal => {
    try {
      await userApi.addCartItem({ setmealId: setmeal.id });
      message.success('套餐已加入购物车');
      loadCart();
    } catch (error) {
      console.error(error);
    }
  };

  const handleAddClick = item => {
    if (currentCategoryType === 2) {
      addSetmealToCart(item);
      return;
    }
    if (item.flavors && item.flavors.length) {
      setFlavorModal({ open: true, dish: item, selections: {} });
    } else {
      addDishToCart(item);
    }
  };

  const handleFlavorConfirm = async () => {
    const { dish, selections } = flavorModal;
    const values = Object.entries(selections);
    if (dish.flavors?.length && values.length < dish.flavors.length) {
      message.warning('请选择全部口味');
      return;
    }
    const flavorText = values.map(([name, val]) => `${name}:${val}`).join(';');
    await addDishToCart(dish, flavorText);
    setFlavorModal({ open: false, dish: null, selections: {} });
  };

  const updateFlavorSelection = (name, value) => {
    setFlavorModal(prev => ({ ...prev, selections: { ...prev.selections, [name]: value } }));
  };

  const buildCartPayload = item => {
    if (item.dishId) {
      return { dishId: item.dishId, dishFlavor: item.dishFlavor };
    }
    return { setmealId: item.setmealId };
  };

  const changeCartItem = async (item, action) => {
    try {
      const payload = buildCartPayload(item);
      if (action === 'add') {
        await userApi.addCartItem(payload);
      } else {
        await userApi.subCartItem(payload);
      }
      loadCart();
    } catch (error) {
      console.error(error);
    }
  };

  const clearCart = async () => {
    try {
      await userApi.clearCart();
      loadCart();
    } catch (error) {
      console.error(error);
    }
  };

  const openCheckout = () => {
    if (!cart.length) {
      message.warning('购物车为空');
      return;
    }
    if (!selectedAddress) {
      message.warning('请先选择地址');
      return;
    }
    checkoutForm.setFieldsValue({
      remark: '',
      tablewareNumber: 1,
      deliveryStatus: 1
    });
    setCheckoutVisible(true);
  };

  const submitOrder = async values => {
    const payload = {
      addressBookId: selectedAddress,
      payMethod: 1,
      remark: values.remark,
      estimatedDeliveryTime: dayjs().add(30, 'minute').format('YYYY-MM-DD HH:mm:ss'),
      deliveryStatus: values.deliveryStatus,
      packAmount: cart.length,
      tablewareNumber: values.tablewareNumber,
      tablewareStatus: 1,
      amount: Number(cartTotal.toFixed(2))
    };
    try {
      const res = await userApi.submitOrder(payload);
      message.success(`下单成功，订单号：${res.orderNumber}`);
      await userApi.payOrder({ orderNumber: res.orderNumber, payMethod: payload.payMethod });
      message.success('支付成功，等待商家接单');
      setCheckoutVisible(false);
      loadCart();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Layout style={{ height: '100vh' }}>
      <Header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography.Text style={{ color: 'white', fontSize: 20 }}>Moment Takeout</Typography.Text>
        <Space>
          <Button onClick={() => navigate('/')}>返回入口</Button>
          <Button icon={<HomeOutlined />} onClick={() => navigate('/user/addresses')}>
            地址簿
          </Button>
          <Button icon={<HistoryOutlined />} onClick={() => navigate('/user/orders')}>
            我的订单
          </Button>
          <Badge count={cartCount} size="small">
            <Button type="primary" icon={<ShoppingCartOutlined />} onClick={() => setCartVisible(true)}>
              购物车
            </Button>
          </Badge>
          <Button danger onClick={handleLogout}>
            退出登录
          </Button>
        </Space>
      </Header>
      <Layout>
        <Sider width={220} style={{ background: '#fff' }}>
          <AntMenu
            mode="inline"
            selectedKeys={selectedCategory ? [String(selectedCategory)] : []}
            style={{ height: '100%', borderRight: 0 }}
            onClick={({ key }) => {
              const id = Number(key);
              setSelectedCategory(id);
              const target = categories.find(item => item.id === id);
              setCurrentCategoryType(target?.type || 1);
            }}
            items={categories.map(c => ({ key: String(c.id), label: c.name }))}
          />
        </Sider>
        <Content style={{ padding: '24px', overflowY: 'auto' }}>
          <Typography.Title level={4}>选择地址</Typography.Title>
          <Space>
            <Select
              placeholder="请选择收货地址"
              style={{ minWidth: 320 }}
              value={selectedAddress}
              onChange={value => setSelectedAddress(value)}
              options={addresses.map(addr => ({
                label: `${addr.consignee} ${addr.phone} - ${addr.detail}`,
                value: addr.id
              }))}
            />
            <Button onClick={loadAddresses}>刷新</Button>
          </Space>
          <Divider />
          <List
            grid={{ gutter: 16, column: 4 }}
            dataSource={menuItems}
            renderItem={item => (
              <List.Item key={item.id}>
                <Card
                  hoverable
                  cover={
                    <img
                      alt={item.name}
                      src={item.image || 'https://via.placeholder.com/300x150?text=Moment'}
                      style={{ height: 150, objectFit: 'cover' }}
                    />
                  }
                  actions={[
                    <Button type="link" onClick={() => handleAddClick(item)}>
                      加入购物车
                    </Button>
                  ]}
                >
                  <Card.Meta
                    title={`${item.name} ¥${item.price}`}
                    description={
                      <div>
                        <Typography.Paragraph ellipsis={{ rows: 2 }}>
                          {item.description || '暂无描述'}
                        </Typography.Paragraph>
                        {currentCategoryType === 2 ? (
                          <Tag color="purple">精选套餐</Tag>
                        ) : item.flavors?.length ? (
                          <Tag color="blue">可选口味</Tag>
                        ) : (
                          <Tag>默认口味</Tag>
                        )}
                      </div>
                    }
                  />
                </Card>
              </List.Item>
            )}
          />
        </Content>
      </Layout>

      <Drawer
        title="我的购物车"
        open={cartVisible}
        onClose={() => setCartVisible(false)}
        width={420}
        extra={
          <Space>
            <Button onClick={clearCart}>清空</Button>
            <Button type="primary" onClick={openCheckout}>
              去结算
            </Button>
          </Space>
        }
      >
        {cart.length === 0 ? (
          <Typography.Text>购物车为空</Typography.Text>
        ) : (
          <List
            dataSource={cart}
            renderItem={item => (
              <List.Item
                key={item.id}
                actions={[
                  <Space>
                    <Button size="small" onClick={() => changeCartItem(item, 'sub')}>
                      -
                    </Button>
                    <Typography.Text>{item.number}</Typography.Text>
                    <Button size="small" onClick={() => changeCartItem(item, 'add')}>
                      +
                    </Button>
                  </Space>
                ]}
              >
                <List.Item.Meta
                  title={`${item.name} ¥${item.amount}`}
                  description={item.dishFlavor || (item.setmealId ? '套餐' : '默认口味')}
                />
              </List.Item>
            )}
          />
        )}
        <Divider />
        <Typography.Title level={5}>总计：¥{cartTotal.toFixed(2)}</Typography.Title>
      </Drawer>

      <Drawer
        title="确认下单"
        open={checkoutVisible}
        onClose={() => setCheckoutVisible(false)}
        width={420}
        destroyOnClose
      >
        <Form layout="vertical" form={checkoutForm} onFinish={submitOrder}>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={3} placeholder="口味、配送提示等" />
          </Form.Item>
          <Form.Item label="餐具数量" name="tablewareNumber" initialValue={1}
            >
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item label="配送方式" name="deliveryStatus" initialValue={1}>
            <Select
              options={[
                { label: '立即送达', value: 1 },
                { label: '预约配送', value: 0 }
              ]}
            />
          </Form.Item>
          <Form.Item>
            <Space>
              <Button onClick={() => setCheckoutVisible(false)}>取消</Button>
              <Button type="primary" htmlType="submit">
                提交订单
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Drawer>

      <Drawer
        title="选择口味"
        open={flavorModal.open}
        onClose={() => setFlavorModal({ open: false, dish: null, selections: {} })}
        width={420}
        destroyOnClose
      >
        {flavorModal.dish && (
          <Space direction="vertical" style={{ width: '100%' }}>
            {flavorModal.dish.flavors.map(flavor => (
              <div key={flavor.id}>
                <Typography.Text strong>{flavor.name}</Typography.Text>
                <Select
                  placeholder="请选择"
                  style={{ width: '100%', marginTop: 8 }}
                  onChange={value => updateFlavorSelection(flavor.name, value)}
                  options={parseFlavorOptions(flavor.value).map(item => ({ label: item, value: item }))}
                />
              </div>
            ))}
            <Button type="primary" onClick={handleFlavorConfirm}>
              确认
            </Button>
          </Space>
        )}
      </Drawer>
    </Layout>
  );
};

export default MenuPage;
