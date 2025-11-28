<template>
  <div class="ticket">
    <div class="ticket-header">
      <h2><search-outlined /> 余票查询</h2>
    </div>
    
    <!-- 查询表单 -->
    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="出发站">
          <station-select-view 
            v-model="searchForm.from" 
            width="200px"
            @change="onFromStationChange"
          />
        </a-form-item>
        <a-form-item label="到达站">
          <station-select-view 
            v-model="searchForm.to" 
            width="200px"
            @change="onToStationChange"
          />
        </a-form-item>
        <a-form-item label="出发日期">
          <a-date-picker 
            v-model:value="searchForm.date" 
            format="YYYY-MM-DD"
            placeholder="请选择日期"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchTickets">
            <search-outlined /> 查询
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 查询结果 -->
    <a-card class="result-card" v-if="tickets.length > 0">
      <a-table 
        :columns="columns" 
        :data-source="tickets" 
        :pagination="false"
        row-key="trainCode"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'trainCode'">
            <a-tag color="blue">{{ record.trainCode }}</a-tag>
          </template>
          <template v-else-if="column.key === 'time'">
            <div>
              <div>{{ record.startTime }} - {{ record.endTime }}</div>
              <div class="duration">{{ record.duration }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'seats'">
            <a-space direction="vertical" size="small">
              <div v-if="record.ydz > 0">
                一等座: <a-tag color="green">{{ record.ydz }}</a-tag>
              </div>
              <div v-if="record.edz > 0">
                二等座: <a-tag color="blue">{{ record.edz }}</a-tag>
              </div>
              <div v-if="record.rw > 0">
                软卧: <a-tag color="orange">{{ record.rw }}</a-tag>
              </div>
              <div v-if="record.yw > 0">
                硬卧: <a-tag color="purple">{{ record.yw }}</a-tag>
              </div>
            </a-space>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="primary" size="small" @click="bookTicket(record)">
              预订
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 空状态 -->
    <a-empty v-else-if="searched" description="暂无车次信息" />
  </div>
</template>

<script>
import { defineComponent, ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import StationSelectView from '@/components/station-select.vue';
import dayjs from 'dayjs';

export default defineComponent({
  name: 'ticket-view',
  components: {
    StationSelectView
  },
  setup() {
    const searched = ref(false);
    const tickets = ref([]);
    
    const searchForm = reactive({
      from: '',
      to: '',
      date: dayjs()
    });

    const columns = [
      {
        title: '车次',
        dataIndex: 'trainCode',
        key: 'trainCode',
        width: 100
      },
      {
        title: '出发站-到达站',
        dataIndex: 'stations',
        key: 'stations',
        width: 200
      },
      {
        title: '出发时间-到达时间',
        key: 'time',
        width: 180
      },
      {
        title: '余票',
        key: 'seats',
        width: 200
      },
      {
        title: '操作',
        key: 'action',
        width: 100
      }
    ];

    const onFromStationChange = (station) => {
      console.log('出发站选择:', station);
    };

    const onToStationChange = (station) => {
      console.log('到达站选择:', station);
    };

    const searchTickets = () => {
      if (!searchForm.from || !searchForm.to) {
        message.error('请选择出发站和到达站');
        return;
      }
      
      if (!searchForm.date) {
        message.error('请选择出发日期');
        return;
      }

      // 模拟查询数据
      tickets.value = [
        {
          trainCode: 'G1001',
          stations: `${searchForm.from} - ${searchForm.to}`,
          startTime: '08:00',
          endTime: '12:30',
          duration: '4小时30分',
          ydz: 99,
          edz: 156,
          rw: 0,
          yw: 0
        },
        {
          trainCode: 'D2002',
          stations: `${searchForm.from} - ${searchForm.to}`,
          startTime: '14:20',
          endTime: '19:45',
          duration: '5小时25分',
          ydz: 23,
          edz: 89,
          rw: 12,
          yw: 45
        },
        {
          trainCode: 'K3003',
          stations: `${searchForm.from} - ${searchForm.to}`,
          startTime: '22:15',
          endTime: '06:30+1',
          duration: '8小时15分',
          ydz: 0,
          edz: 0,
          rw: 8,
          yw: 67
        }
      ];
      
      searched.value = true;
      message.success('查询成功');
    };

    const bookTicket = (record) => {
      message.info(`预订车次 ${record.trainCode} 的功能正在开发中...`);
    };

    return {
      searched,
      tickets,
      searchForm,
      columns,
      onFromStationChange,
      onToStationChange,
      searchTickets,
      bookTicket
    };
  },
});
</script>

<style scoped>
.ticket {
  padding: 24px;
}

.ticket-header {
  margin-bottom: 24px;
}

.ticket-header h2 {
  margin: 0;
  color: #1890ff;
}

.search-card {
  margin-bottom: 24px;
}

.result-card {
  margin-bottom: 24px;
}

.duration {
  font-size: 12px;
  color: #999;
}
</style>