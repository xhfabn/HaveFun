import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Statistic, Button, DatePicker, Space, Spin, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import {
  ThunderboltOutlined,
  ShoppingOutlined,
  CarOutlined,
  ReloadOutlined,
  OrderedListOutlined
} from '@ant-design/icons';
import { Area, Line, Column } from '@ant-design/plots';
import dayjs from 'dayjs';
import adminApi from '../../api/admin';

const { RangePicker } = DatePicker;

const defaultStats = { toBeConfirmed: 0, confirmed: 0, deliveryInProgress: 0 };
const defaultRange = [dayjs().subtract(6, 'day'), dayjs()];

const csvToArray = (text = '') => (text ? text.split(',').map(item => item.trim()).filter(Boolean) : []);
const csvToNumberArray = text => csvToArray(text).map(item => Number(item) || 0);

const Dashboard = () => {
  const navigate = useNavigate();
  const [dateRange, setDateRange] = useState(defaultRange);
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState(defaultStats);
  const [reportData, setReportData] = useState({
    turnover: [],
    userSeries: [],
    orderSeries: [],
    top10: [],
    orderSummary: { total: 0, valid: 0, rate: 0 }
  });

  const buildTurnoverData = res => {
    const dates = csvToArray(res?.dateList);
    const values = csvToNumberArray(res?.turnoverList);
    return dates.map((date, idx) => ({ date, value: values[idx] || 0 }));
  };

  const buildUserSeries = res => {
    const dates = csvToArray(res?.dateList);
    const totalList = csvToNumberArray(res?.totalUserList);
    const newList = csvToNumberArray(res?.newUserList);
    const totalSeries = dates.map((date, idx) => ({ date, value: totalList[idx] || 0, type: '累计用户' }));
    const newSeries = dates.map((date, idx) => ({ date, value: newList[idx] || 0, type: '新增用户' }));
    return [...newSeries, ...totalSeries];
  };

  const buildOrderSeries = res => {
    const dates = csvToArray(res?.dateList);
    const totalList = csvToNumberArray(res?.orderCountList);
    const validList = csvToNumberArray(res?.validOrderCountList);
    const totalSeries = dates.map((date, idx) => ({ date, value: totalList[idx] || 0, type: '全部订单' }));
    const validSeries = dates.map((date, idx) => ({ date, value: validList[idx] || 0, type: '有效订单' }));
    return [...totalSeries, ...validSeries];
  };

  const buildTop10Data = res => {
    const names = csvToArray(res?.nameList);
    const numbers = csvToNumberArray(res?.numberList);
    return names.map((name, idx) => ({ name, value: numbers[idx] || 0 }));
  };

  const fetchReports = async currentRange => {
    if (!currentRange?.length) return;
    setLoading(true);
    const params = {
      begin: currentRange[0].format('YYYY-MM-DD'),
      end: currentRange[1].format('YYYY-MM-DD')
    };
    try {
      const [turnoverRes, userRes, orderRes, top10Res, statsRes] = await Promise.all([
        adminApi.fetchTurnoverReport(params),
        adminApi.fetchUserReport(params),
        adminApi.fetchOrderReport(params),
        adminApi.fetchSalesTopReport(params),
        adminApi.fetchOrderStats()
      ]);
      setReportData({
        turnover: buildTurnoverData(turnoverRes),
        userSeries: buildUserSeries(userRes),
        orderSeries: buildOrderSeries(orderRes),
        top10: buildTop10Data(top10Res),
        orderSummary: {
          total: orderRes?.totalOrderCount || 0,
          valid: orderRes?.validOrderCount || 0,
          rate: ((orderRes?.orderCompletionRate || 0) * 100)
        }
      });
      setStats(statsRes || defaultStats);
    } catch (error) {
      console.error(error);
      message.error(error.message || '加载报表数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports(dateRange);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dateRange]);

  const turnoverConfig = {
    data: reportData.turnover,
    xField: 'date',
    yField: 'value',
    smooth: true,
    height: 260,
    areaStyle: { fillOpacity: 0.2 }
  };

  const userConfig = {
    data: reportData.userSeries,
    xField: 'date',
    yField: 'value',
    seriesField: 'type',
    smooth: true,
    height: 260,
    yAxis: { min: 0 }
  };

  const orderConfig = {
    data: reportData.orderSeries,
    xField: 'date',
    yField: 'value',
    seriesField: 'type',
    smooth: true,
    height: 260,
    yAxis: { min: 0 }
  };

  const top10Config = {
    data: reportData.top10,
    xField: 'name',
    yField: 'value',
    height: 260,
    columnWidthRatio: 0.6,
    legend: false,
    tooltip: { formatter: datum => ({ name: '销量', value: datum.value }) }
  };

  const completionRate = Number(reportData.orderSummary.rate || 0).toFixed(2);

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Space wrap size="middle">
          <span>统计范围：</span>
          <RangePicker
            allowClear={false}
            value={dateRange}
            ranges={{
              '近7天': [dayjs().subtract(6, 'day'), dayjs()],
              '近30天': [dayjs().subtract(29, 'day'), dayjs()]
            }}
            onChange={value => value?.length === 2 && setDateRange(value)}
          />
          <Button icon={<ReloadOutlined />} onClick={() => fetchReports(dateRange)}>
            刷新
          </Button>
          <Button icon={<OrderedListOutlined />} onClick={() => navigate('/admin/orders')}>
            查看订单
          </Button>
        </Space>
      </Card>

      <Spin spinning={loading}>
        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col span={8}>
            <Card>
              <Statistic
                title="待接单"
                value={stats.toBeConfirmed}
                prefix={<ShoppingOutlined style={{ color: '#faad14' }} />}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="待派送"
                value={stats.confirmed}
                prefix={<ThunderboltOutlined style={{ color: '#1677ff' }} />}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="派送中"
                value={stats.deliveryInProgress}
                prefix={<CarOutlined style={{ color: '#52c41a' }} />}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col span={8}>
            <Card>
              <Statistic title="总订单" value={reportData.orderSummary.total} />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic title="有效订单" value={reportData.orderSummary.valid} />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic title="完成率" value={Number(completionRate)} suffix="%" />
            </Card>
          </Col>
        </Row>

        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col span={12}>
            <Card title="营业额趋势">
              <Area {...turnoverConfig} />
            </Card>
          </Col>
          <Col span={12}>
            <Card title="用户增长">
              <Line {...userConfig} />
            </Card>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Card title="订单走势">
              <Line {...orderConfig} />
            </Card>
          </Col>
          <Col span={12}>
            <Card title="销量Top10">
              <Column {...top10Config} />
            </Card>
          </Col>
        </Row>
      </Spin>
    </div>
  );
};

export default Dashboard;
