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

import {defineComponent, onMounted, ref, watch} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "station-select-view",
  props: ["modelValue", "width"],
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const name = ref();
    const stations = ref([]);
    const localWidth = ref(props.width);
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
      axios.get("/business/admin/station/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          stations.value = data.content || [];
          console.log("加载车站成功，数量：", stations.value.length);
        } else {
          notification.error({description: data.message || "查询车站失败"});
        }
      }).catch(e => {
        console.error("查询车站接口异常：", e);
        notification.error({description: "查询车站接口异常，请检查后端服务或网络"});
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
      // 选项的 value 是站名 name，这里按 name 去匹配
      const station = stations.value.find(item => item.name === value) || {};
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
