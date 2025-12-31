<template>
  <div class="layout-pd">
    <el-card shadow="hover">
      <section>
        <div class="tree-box">
          <h3>项目设备树</h3>
          <el-tree
              :data="state.equipments"
              default-expand-all
              :props="{children: 'children',label: 'name'}"
          >
            <template #default="{ node, data }">
              <div @click="changeEquipment(data)" style="width: 100%;cursor: pointer;">
                <el-tag v-if="data.resourceTag" type="success" style="margin-right:10px">设备</el-tag>
                <span>{{ data.resourceTag ? data.resourceName : data.projectName }}</span>
              </div>
            </template>
          </el-tree>
        </div>
        <el-divider direction="vertical"/>
        <div class="content-box">
          <div style="display:flex">
            <el-form-item label="变量选择:">
              <el-select v-model="state.variableId" placeholder="请选择" @change="changeVariable">
                <el-option :key="state.equipmentId" v-for="variable in state.variables"
                           :label="variable.variableName" :value="variable.variableId"/>
              </el-select>
            </el-form-item>
            <el-link type="primary" style="margin-left:16px;height: 32px" @click="refreshData">刷新</el-link>
            <el-link type="primary" style="margin-left:16px;height: 32px" @click="exportData">导出</el-link>
          </div>
          <el-table id="tableData" :data="state.realDatas" :stripe="true"
                    :header-cell-style="{backgroundColor:'#F5F5F5',color:'#333333',height:'48px'}"
                    :cell-style="{height:'48px'}"
                    style="width: 100%;height: calc(100% - 50px)"
                    border>
            <el-table-column prop="ts" label="推送时间"/>
            <el-table-column prop="value" label="变量值"/>
          </el-table>
        </div>
        <el-divider direction="vertical"/>
        <div class="note-box">
          <h3>二次开发说明</h3>
          <h4>开发概述</h4>
          <el-text>
            本系统采用播件化构设计，支持第三方开发者进行二次开发和功能扩展。提供完整的API接口和开发文档，使于快速集成目定义功能。
          </el-text>
          <h4>插件开发</h4>
          <ul style="padding-left: 30px;">
            <li>支持功能插件开发</li>
            <li>提供插件标识机制</li>
            <li>标准API接规范</li>
            <li>插件市场集成</li>
          </ul>
          <h4>技术支持</h4>
          <ul style="padding-left: 30px;">
            <li>HTML5/CSS3/JavaScript</li>
            <li>Tailwind CSS框架</li>
            <li>Chartjs数据可视化</li>
            <li>RESTfuIAPI接口</li>
          </ul>
          <h4>开发资源</h4>
          <div style="display: flex;justify-content: space-between;">
            <el-button @click="goOpenAPI">API文档</el-button>
            <el-button @click="goExample">示例代码</el-button>
            <el-button @click="goTwoOpen">二开文档</el-button>
          </div>
        </div>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {defineAsyncComponent, ref, onMounted, reactive, onBeforeMount, onActivated, onDeactivated,} from 'vue';
import {useI18n} from 'vue-i18n';
import {useBaseApi, useIotApi} from "/@/api";
import {storeToRefs} from "pinia";
import {useUserInfo} from "/@/stores/userInfo";
import FileSaver from 'file-saver';
import * as XLSX from 'xlsx';
import twoOpenManuals from '/@/assets/doc/two_open_manuals.pdf'

//引入组件


// 定义变量内容
const {t} = useI18n();
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const state = reactive({
  name: '',
  user: {} as User,
  equipments: [] as any[],
  variables: [] as any[],
  equipmentId: '',
  variableId: '',
  realDatas: [] as any[],
  page: {
    current: 1,
    size: 10,
  },

  socket: null as WebSocket,
  socketTopic: "",
  socketConnected: false,
  socketRetryCount: 0,

});

// 页面加载前
onBeforeMount(() => {
});

// 页面加载时
onMounted(() => {
  getEquipments()
});

onDeactivated(() => {
  handleSocketClose();
})

const goExample = () => {
  window.open('https://github.com/hehaibo123/hiwoo-micro-plugin.git', '_blank');
}

const goOpenAPI = () => {
  window.open(window.location.origin + '/api/doc.html#/home', '_blank');
}

const goTwoOpen = () => {
  window.open(twoOpenManuals, '_blank');
}

const refreshData = () => {
  getHistoryData()
}


const exportData = (tableName, name) => {
  let workbook = XLSX.utils.table_to_book(document.querySelector('#tableData'))
  const sheetName = workbook.SheetNames[0]
  const worksheet = workbook.Sheets[sheetName]
  worksheet['!cols'] = getAutoColumnWidths(worksheet)
  processDateFormattedCells(worksheet)
  let excelData = XLSX.write(workbook, {bookType: 'xlsx', bookSST: true, type: 'array'})

  try {
    FileSaver.saveAs(new Blob([excelData], {type: 'application/octet-stream'}), formatDateToString(new Date()) + '.xlsx')
  } catch (e) {
    if (typeof console !== 'undefined') console.log(e, excelData)
  }

  return excelData
}

// 自动计算列宽辅助函数
const getAutoColumnWidths = (worksheet) => {
  const colWidths = []
  const range = XLSX.utils.decode_range(worksheet['!ref'])

  // 遍历每一列
  for (let col = range.s.c; col <= range.e.c; col++) {
    let maxWidth = 10 // 最小宽度

    // 遍历每一行
    for (let row = range.s.r; row <= range.e.r; row++) {
      const cellAddress = XLSX.utils.encode_cell({r: row, c: col})
      const cell = worksheet[cellAddress]

      if (cell) {
        // 获取单元格内容长度
        const cellLength = cell.v ? cell.v.toString().length : 0
        maxWidth = Math.max(maxWidth, cellLength)
      }
    }

    // 设置列宽，加2个字符作为边距
    colWidths.push({wch: maxWidth + 2})
  }
  return colWidths
}

const processDateFormattedCells = (worksheet) => {
  // 获取工作表的范围
  const range = XLSX.utils.decode_range(worksheet['!ref'])

  // 遍历所有单元格
  for (let row = range.s.r; row <= range.e.r; row++) {
    for (let col = range.s.c; col <= range.e.c; col++) {
      const cellAddress = XLSX.utils.encode_cell({r: row, c: col})
      const cell = worksheet[cellAddress]
      if (cell && cell.t === 'n' && cell.z === "m/d/yy") {
        // Excel日期是序列号，需要转换
        const date = excelSerialToDate(cell.v)
        cell.v = formatDateToString(date)
        cell.t = 's' // 改为字符串类型
        delete cell.z // 删除数字格式
      }
    }
  }
}

// Excel序列号转日期（Excel日期从1900-01-01开始）
const excelSerialToDate = (serial) => {
  // Excel的日期系统从1900-01-01开始
  // 注意：Excel错误地将1900年当作闰年，所以需要调整
  const utc_days = Math.floor(serial - 25569)
  const utc_value = utc_days * 86400
  const date_info = new Date(utc_value * 1000)

  // 调整时区
  const fractional_day = serial - Math.floor(serial) + 0.0000001
  let total_seconds = Math.floor(86400 * fractional_day)
  const seconds = total_seconds % 60
  total_seconds -= seconds
  const hours = Math.floor(total_seconds / (60 * 60))
  const minutes = Math.floor(total_seconds / 60) % 60

  return new Date(
      date_info.getFullYear(),
      date_info.getMonth(),
      date_info.getDate(),
      hours,
      minutes,
      seconds
  )
}

// 格式化为字符串
const formatDateToString = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
const createSocket = () => {
  state.socketTopic = userInfos.value.enterpriseId + (new Date()).getTime();
  if (state.socket) state.socket.close();  // 确保关闭任何现有的连接
  state.socket = new WebSocket((import.meta.env.VITE_SOCKET_URL || (window.location.protocol == 'https:' ? 'wss://' : 'ws://') + window.location.hostname) + import.meta.env.VITE_EQUIPMENT_WEBSOCKET_SUFFIX + state.socketTopic);

  state.socket.addEventListener('open', handleSocketOpen);
  state.socket.addEventListener('message', handleSocketMessage);
  state.socket.addEventListener('close', handleSocketClose);
  state.socket.addEventListener('error', handleSocketError);
  return state.socket;
}
const handleSocketOpen = (event: any) => {
  console.log('Socket 连接已打开' + new Date());
  state.socketConnected = true;

  if (state.socketConnected) {
    state.socket?.send(JSON.stringify({
      topic: state.socketTopic,
      tenantId: userInfos.value.enterpriseId,
      deviceId: state.equipmentId,
      variableId: state.variableId,
      socketType: "variable"
    }));
  } else {
    console.log('无法发送消息，Socket 连接未打开');
  }
}
const handleSocketClose = (event: any) => {
  console.log('Socket 连接已关闭');
  state.socketConnected = false;
}
const handleSocketMessage = (event: any) => {
  const socketData = JSON.parse(event.data)
  const data = {
    ts: tsFormat(parseInt(socketData.ts)).split(".")[0],
    value: socketData.value
  }
  state.realDatas.unshift(data)
}
const handleSocketError = (event: any) => {
  console.error('Socket 连接出错', event);
  // 链接出错重连
  if (state.socketRetryCount < 3) {
    setTimeout(() => {
      state.socketRetryCount++;
      createSocket(); // 重新创建WebSocket连接
    }, 1000 * Math.pow(2, state.socketRetryCount)); // 使用指数回退策略来避免在短时间内进行过多的重新连接尝试
  } else {
    console.error('Socket已达到最大重试次数。不尝试重新连接.');
  }
}


const tsFormat = (value: any) => {
  // 使用原生 JavaScript 来格式化时间
  const date = new Date(value);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  const milliseconds = String(date.getMilliseconds()).padStart(3, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
}

const getEquipments = () => {
  useBaseApi().getResourcesByProjectTree("equipment").then((res) => {
    state.equipments = res.data || [];
  });
}

const changeEquipment = (data: any) => {
  if (data.resourceTag) {
    state.equipmentId = data.resourceId;
    getVariables()
  }
}


const getVariables = () => {
  const params = {
    deviceId: state.equipmentId,
    type: "1,2,3"
  }
  useIotApi().getVariables(params).then((res) => {
    state.variables = res.data || [];
  });
}

const changeVariable = () => {
  createSocket();
  getHistoryData();
}


const getHistoryData = () => {
  let nowDate = new Date();
  let oneHourNow = new Date(nowDate.getTime() - 1 * 60 * 60 * 1000)
  const params = {
    current: state.page.current,
    size: state.page.size,
    variableId: state.variableId,
    startTime: formatDate(oneHourNow, "YYYY-mm-dd HH:MM:SS"),
    endTime: formatDate(nowDate, "YYYY-mm-dd HH:MM:SS"),
  }
  useIotApi().getHistoryData(params).then((res) => {
    state.realDatas = res.data.dataList || [];
  });
}

const formatDate = (date: Date, format: string) => {
  const opt: { [key: string]: string } = {
    'Y+': date.getFullYear().toString(), // 年
    'm+': (date.getMonth() + 1).toString(), // 月(月份从0开始，要+1)
    'd+': date.getDate().toString(), // 日
    'H+': date.getHours().toString(), // 时
    'M+': date.getMinutes().toString(), // 分
    'S+': date.getSeconds().toString(), // 秒
  };
  for (let k in opt) {
    let r = new RegExp('(' + k + ')').exec(format);
    // 若输入的长度不为1，则前面补零
    if (r) format = format.replace(r[1], RegExp.$1.length == 1 ? opt[k] : opt[k].padStart(RegExp.$1.length, '0'));
  }
  return format;
}


</script>

<style scoped lang="scss">
.layout-pd {
  .el-card {
    height: 100%;

    section {
      height: 100%;
      display: flex;

      .tree-box {
        width: 350px;

        :deep(.el-tree-node__content) {
          height: 40px !important;
          line-height: 40px !important;
        }
      }

      .el-divider {
        height: 100%;
        margin: 0 8px;
      }

      .content-box {
        width: calc(100% - 720px);
      }

      .note-box {
        width: 360px;

        h4 {
          margin: 16px 0;
        }
      }
    }

    h3 {
      margin-bottom: 16px;
    }
  }
}

</style>
