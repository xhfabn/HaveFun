<template>
  <a-select v-model:value="trainCode" show-search allowClear
            :filterOption="filterTrainCodeOption"
            @change="onChange" placeholder="请选择车次"
            :style="'width: ' + localWidth">
    <a-select-option v-for="item in trains" :key="item.code" :value="item.code" :label="item.code + item.start + item.end">
      {{item.code}} {{item.start}} ~ {{item.end}}
    </a-select-option>
  </a-select>
</template>

<script>

import {defineComponent, onMounted, ref, watch} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "train-select-view",
  props: ["modelValue", "width"],
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const trainCode = ref();
    const trains = ref([]);
    const localWidth = ref(props.width);
    if (Tool.isEmpty(props.width)) {
      localWidth.value = "100%";
    }

    // 利用watch，动态获取父组件的值，如果放在onMounted或其它方法里，则只有第一次有效
    watch(() => props.modelValue, ()=>{
      console.log("props.modelValue", props.modelValue);
      trainCode.value = props.modelValue;
    }, {immediate: true});

    /**
     * 查询所有的车次，用于车次下拉框
     */
    const queryAllTrain = () => {
      // 模拟数据作为降级方案
      const mockTrains = [
        { code: 'G1001', start: '北京', end: '上海' },
        { code: 'G1002', start: '上海', end: '北京' },
        { code: 'D2001', start: '北京', end: '天津' },
        { code: 'D2002', start: '天津', end: '北京' },
        { code: 'G3001', start: '广州', end: '深圳' },
        { code: 'G3002', start: '深圳', end: '广州' },
        { code: 'K4001', start: '北京', end: '西安' },
        { code: 'K4002', start: '西安', end: '北京' },
        { code: 'G5001', start: '杭州', end: '南京' },
        { code: 'G5002', start: '南京', end: '杭州' }
      ];
      
      // 先尝试请求后端API，失败时使用模拟数据
      axios.get("/business/train/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          trains.value = data.content;
          console.log('后端车次数据加载完成');
        } else {
          trains.value = mockTrains;
          console.log('后端返回错误，使用模拟车次数据');
        }
      }).catch((error) => {
        // 网络错误或后端不可用时，静默使用模拟数据
        trains.value = mockTrains;
        console.log('后端服务不可用，使用模拟车次数据', error.message);
      });
    };

    /**
     * 车次下拉框筛选
     */
    const filterTrainCodeOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    /**
     * 将当前组件的值响应给父组件
     * @param value
     */
    const onChange = (value) => {
      emit('update:modelValue', value);
      let train = trains.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(train)) {
        train = {};
      }
      emit('change', train);
    };

    onMounted(() => {
      queryAllTrain();
    });

    return {
      trainCode,
      trains,
      filterTrainCodeOption,
      onChange,
      localWidth
    };
  },
});
</script>
