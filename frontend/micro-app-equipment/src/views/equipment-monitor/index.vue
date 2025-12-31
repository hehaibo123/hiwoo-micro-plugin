<template>
  <div class="layout-pd">
    <el-card shadow="hover">
      <section>
        <div class="equipment-box">
          <div class="tree-box">
            <h3>项目设备树</h3>
            <el-tree
                :data="state.equipments"
                default-expand-all
                :props="{children: 'children',label: 'name'}"
            >
              <template #default="{ node, data }">
                <div @click="changeEquipment(data)">
                  <el-tag v-if="data.resourceTag" type="success" style="margin-right:10px">设备</el-tag>
                  <span>{{ data.resourceTag ? data.resourceName : data.projectName }}</span>
                </div>
              </template>
            </el-tree>
          </div>
          <el-divider direction="vertical"/>
          <div class="content-box">
            <el-form-item label="变量选择:">
              <el-select v-model="state.variableId" placeholder="请选择" @change="changeVariable">
                <el-option v-for="variable in state.variables"
                           :key="variable.variableId" :label="variable.variableName" :value="variable.variableId"/>
              </el-select>
            </el-form-item>
            <div style="display: flex;height: calc(100% - 50px)">
              <div style="width: calc(50% - 16px);height: 100%">
                <h3>实时数据</h3>
                <el-table :data="state.realDatas" style="width: 100%;height: calc(100% - 32px)">
                  <el-table-column prop="ts" label="推送时间"/>
                  <el-table-column prop="value" label="变量值"/>
                </el-table>
              </div>
              <div style="width: calc(50% - 16px);margin-left: 32px;height: 100%">
                <h3>历史数据</h3>
                <el-table :data="state.historyDatas" style="width: 100%;height: calc(100% - 64px)">
                  <el-table-column prop="ts" label="推送时间"/>
                  <el-table-column prop="value" label="变量值"/>
                </el-table>
                <el-pagination
                    style="margin-top: 16px"
                    @size-change="handlePageSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="state.page.current"
                    :page-size="state.page.size"
                    layout="total, prev, pager, next"
                    :total="state.page.total">
                </el-pagination>
              </div>
            </div>
          </div>
        </div>
        <div class="alarm-box">
          <div class="tree-box">
            <h3>项目报警树</h3>
            <el-tree
                :data="state.alarms"
                default-expand-all
                :props="{children: 'children',label: 'name'}"
            >
              <template #default="{ node, data }">
                <div @click="changeAlarm(data)">
                  <el-tag v-if="data.resourceTag" type="success" style="margin-right:10px">报警规则</el-tag>
                  <span>{{ data.resourceTag ? data.resourceName : data.projectName }}</span>
                </div>
              </template>
            </el-tree>
          </div>
          <el-divider direction="vertical"/>
          <div class="content-box">
            <el-table :data="state.historyAlarms">
              <el-table-column prop="handlerMessage" label="报警消息"/>
              <el-table-column prop="level" label="报警级别"/>
              <el-table-column prop="triggerTime" label="触发时间"/>
              <el-table-column prop="eliminateTime" label="消除时间"/>
              <el-table-column prop="handlerStatus" label="报警状态" :formatter="alarmStatusFormatter"/>
            </el-table>
            <el-pagination
                style="margin-top: 16px"
                @size-change="handlePageSizeChange"
                @current-change="handleCurrentChange"
                :current-page="state.page.current"
                :page-size="state.page.size"
                layout="total, prev, pager, next"
                :total="state.page.total">
            </el-pagination>
          </div>
        </div>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {defineAsyncComponent, ref, onMounted, reactive, onBeforeMount, onDeactivated} from 'vue';
import {useI18n} from 'vue-i18n';
import {useBaseApi, useIotApi} from "/@/api";
import {storeToRefs} from "pinia";
import {useUserInfo} from "/@/stores/userInfo";
import {ElNotification} from "element-plus";


//引入组件


// 定义变量内容
const {t} = useI18n();
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const state = reactive({
  name: '',
  user: {} as User,
  equipments: [] as any[],
  alarms: [] as any[],
  variables: [] as any[],
  equipmentId: '',
  variableId: '',
  alarmId: '',
  realDatas: [] as any[],
  historyDatas: [] as any[],
  historyAlarms: [] as any[],
  page: {
    total: 0,
    current: 1,
    size: 10,
  },

  socket: null as WebSocket,
  socketTopic: "",
  socketConnected: false,
  socketRetryCount: 0,

  socketAlarm: null as WebSocket,
  socketAlarmTopic: "",
  socketAlarmConnected: false,
  socketAlarmRetryCount: 0,
});

// 页面加载前
onBeforeMount(() => {
});

// 页面加载时
onMounted(() => {
  getEquipments()
  getAlarms()
});

onDeactivated(() => {
  handleSocketClose();
  handleSocketAlarmClose();
})

const createSocket = () => {
  state.socketTopic = userInfos.value.enterpriseId + (new Date()).getTime();
  if (state.socket) state.socket.close();  // 确保关闭任何现有的连接
  state.socket = new WebSocket((import.meta.env.VITE_SOCKET_URL || 'wss://iot.hiwooiot.cn') + import.meta.env.VITE_EQUIPMENT_WEBSOCKET_SUFFIX + state.socketTopic);

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

const createSocketAlarm = () => {
  state.socketTopic = userInfos.value.enterpriseId + (new Date()).getTime();
  if (state.socket) state.socket.close();  // 确保关闭任何现有的连接
  state.socket = new WebSocket((import.meta.env.VITE_SOCKET_URL || (window.location.protocol == 'https:' ? 'wss://' : 'ws://') + window.location.hostname) + import.meta.env.VITE_EQUIPMENT_WEBSOCKET_SUFFIX + state.socketTopic);

  state.socket.addEventListener('open', handleSocketAlarmOpen);
  state.socket.addEventListener('message', handleSocketAlarmMessage);
  state.socket.addEventListener('close', handleSocketAlarmClose);
  state.socket.addEventListener('error', handleSocketAlarmError);
  return state.socket;
}
const handleSocketAlarmOpen = (event: any) => {
  console.log('Socket 连接已打开' + new Date());
  state.socketAlarmConnected = true;

  if (state.socketAlarmConnected) {
    state.socketAlarm?.send(JSON.stringify({
      topic: state.socketTopic,
      tenantId: userInfos.value.enterpriseId,
      type: "equipment-alarm",
    }));
  } else {
    console.log('无法发送消息，Socket 连接未打开');
  }
}
const handleSocketAlarmClose = (event: any) => {
  console.log('Socket 连接已关闭');
  state.socketAlarmConnected = false;
}
const handleSocketAlarmMessage = (event: any) => {
  const socketData = JSON.parse(event.data)
  if (socketData.type == "trigger") {
    ElNotification({
      title: socketData.alarmName,
      offset: 200,
      dangerouslyUseHTMLString: true,
      message: `<div>级别：<span style="background: rgba(255,149,0,0.1);border-radius: 4px 4px 4px 4px;color: #ff6a00;border: 1px solid #ff6a00;padding: 3px 8px">${socketData.alarmLevel + '级'}</span></div><div>时间：${socketData.triggerTime}</div><div>原因：${socketData.message}</div>`,
    })
  }
}
const handleSocketAlarmError = (event: any) => {
  console.error('Socket 连接出错', event);
  // 链接出错重连
  if (state.socketAlarmRetryCount < 3) {
    setTimeout(() => {
      state.socketAlarmRetryCount++;
      createSocketAlarm(); // 重新创建WebSocket连接
    }, 1000 * Math.pow(2, state.socketAlarmRetryCount)); // 使用指数回退策略来避免在短时间内进行过多的重新连接尝试
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
    state.historyDatas = res.data.dataList || [];
    state.page.total = res.data.total || 0;
  });
}

const handleCurrentChange = async (newPage: number) => {
  state.page.current = newPage;
  getHistoryData();

};

// 每页显示条目数变化时的处理函数
const handlePageSizeChange = async (newPageSize: number) => {
  state.page.size = newPageSize;
  getHistoryData();
};

const getAlarms = () => {
  useBaseApi().getResourcesByProjectTree("alarm").then((res) => {
    state.alarms = res.data || [];
  });
}

const changeAlarm = (data) => {
  if (data.resourceTag) {
    state.alarmId = data.resourceId;
    getHistoryAlarm()
    createSocketAlarm();
  }
}
const getHistoryAlarm = () => {
  let nowDate = new Date();
  let oneHourNow = new Date(nowDate.getTime() - 1 * 60 * 60 * 1000)
  const params = {
    current: state.page.current,
    size: state.page.size,
    alarmId: state.alarmId,
    startTime: formatDate(oneHourNow, "YYYY-mm-dd HH:MM:SS"),
    endTime: formatDate(nowDate, "YYYY-mm-dd HH:MM:SS"),
  }
  useIotApi().getHistoryAlarm(params).then((res) => {
    state.historyAlarms = res.data.dataList || [];
    state.page.total = res.data.total || 0;
  });
}

const alarmStatusFormatter = (row: any) => {
  let title = row.eliminateTime !== null ? "已消除" : "触发中";
  row.alarmStatusTitle = title
  return title
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

      .equipment-box, .alarm-box {
        height: calc(50% - 8px);
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
        }

        .content-box {
          width: calc(100% - 360px);
        }
      }

      .alarm-box {
        margin-top: 16px;
      }
    }

    h3 {
      margin-bottom: 16px;
    }
  }
}

</style>
