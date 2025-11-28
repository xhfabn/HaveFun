<template>
  <a-select v-model:value="name" show-search allowClear
            :filterOption="filterNameOption"
            @change="onChange" placeholder="请选择车站"
            :style="'width: ' + localWidth">
    <a-select-option v-for="item in stations" :key="item.name" :value="item.name" :label="item.name + item.namePinyin + item.namePy">
      {{item.name}} {{item.namePinyin}} ~ {{item.namePy}}
    </a-select-option>
  </a-select>
</template>

<script>

import {defineComponent, ref, onMounted, watch} from 'vue';
import axios from "axios";

export default defineComponent({
  name: "station-select-view",
  props: ["modelValue", "width"],
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const name = ref();
    const stations = ref([]);
    const localWidth = ref(props.width);
    // eslint-disable-next-line no-undef
    if (Tool.isEmpty(props.width)) {
      localWidth.value = "100%";
    }

    // 利用watch，动态获取父组件的值，如果放在onMounted或其它方法里，则只有第一次有效
    watch(() => props.modelValue, ()=>{
      console.log("props.modelValue", props.modelValue);
      name.value = props.modelValue;
    }, {immediate: true});

    /**
     * 查询所有的车站，用于车站下拉框
     */
    const queryAllStation = () => {
      // 模拟数据作为降级方案
      const mockStations = [
        { code: 'BJP', name: '北京', namePinyin: 'beijing', namePy: 'BJ' },
        { code: 'SHH', name: '上海', namePinyin: 'shanghai', namePy: 'SH' },
        { code: 'GZQ', name: '广州', namePinyin: 'guangzhou', namePy: 'GZ' },
        { code: 'SZN', name: '深圳', namePinyin: 'shenzhen', namePy: 'SZ' },
        { code: 'HGH', name: '杭州', namePinyin: 'hangzhou', namePy: 'HZ' },
        { code: 'NJH', name: '南京', namePinyin: 'nanjing', namePy: 'NJ' },
        { code: 'TJP', name: '天津', namePinyin: 'tianjin', namePy: 'TJ' },
        { code: 'CQW', name: '重庆', namePinyin: 'chongqing', namePy: 'CQ' },
        { code: 'CDW', name: '成都', namePinyin: 'chengdu', namePy: 'CD' },
        { code: 'XAY', name: '西安', namePinyin: 'xian', namePy: 'XA' },
        { code: 'WHN', name: '武汉', namePinyin: 'wuhan', namePy: 'WH' },
        { code: 'CSQ', name: '长沙', namePinyin: 'changsha', namePy: 'CS' },
        { code: 'ZZF', name: '郑州', namePinyin: 'zhengzhou', namePy: 'ZZ' },
        { code: 'JNK', name: '济南', namePinyin: 'jinan', namePy: 'JN' },
        { code: 'SJP', name: '石家庄', namePinyin: 'shijiazhuang', namePy: 'SJZ' }
      ];
      
      // 先尝试请求后端API，失败时使用模拟数据
      axios.get("/business/station/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          stations.value = data.content;
          console.log('后端车站数据加载完成');
        } else {
          stations.value = mockStations;
          console.log('后端返回错误，使用模拟车站数据');
        }
      }).catch((error) => {
        // 网络错误或后端不可用时，静默使用模拟数据
        stations.value = mockStations;
        console.log('后端服务不可用，使用模拟车站数据', error.message);
      });
    };

    /**
     * 车站下拉框筛选
     */
    const filterNameOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    /**
     * 将当前组件的值响应给父组件
     * @param value
     */
    const onChange = (value) => {
      emit('update:modelValue', value);
      let station = stations.value.filter(item => item.code === value)[0];
      // eslint-disable-next-line no-undef
      if (Tool.isEmpty(station)) {
        station = {};
      }
      emit('change', station);
    };

    onMounted(() => {
      queryAllStation();
    });

    return {
      name,
      stations,
      filterNameOption,
      onChange,
      localWidth
    };
  },
});
</script>
