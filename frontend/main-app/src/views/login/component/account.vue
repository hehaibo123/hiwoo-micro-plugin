<template>
  <el-form size="large" class="login-content-form">
    <div class="login-animation1">
      <el-input text :placeholder="$t('message.login.placeholder.inputAccount')" v-model="state.ruleForm.account"
                clearable autocomplete="off"
                style="height: 48px; "
                :show-password="state.showCount"
      >
        <template #prefix>
          <el-icon class="el-input__icon" :size="19" color="#999999">
            <ele-User/>
          </el-icon>
        </template>
      </el-input>
    </div>
    <div class="login-animation2">
      <el-input
          style="height: 48px;"
          :type="state.isShowPassword ? 'text' : 'password'"
          :placeholder="$t('message.login.placeholder.inputPassword')"
          v-model="state.ruleForm.password"
          autocomplete="off"
      >
        <template #prefix>
          <el-icon class="el-input__icon" :size="19" color="#999999">
            <ele-Unlock/>
          </el-icon>
        </template>
        <template #suffix>
          <i
              class="iconfont el-input__icon login-content-password"
              :class="state.isShowPassword ? 'icon-yincangmima' : 'icon-xianshimima'"
              @click="state.isShowPassword = !state.isShowPassword"
          >
          </i>
        </template>
      </el-input>
    </div>
    <div class="login-animation3">
      <el-col :span="15">
        <el-checkbox v-model="state.checked" true-label="true" style="height:14px;line-height: 14px">记住密码
        </el-checkbox>
      </el-col>
      <el-col :span="1"></el-col>
      <el-col :span="8" style="text-align: right">
      </el-col>
    </div>
    <div v-if="state.innerHeight > 655" class="login-animation4">
      <div class="drag" ref="dragDiv">
        <div class="drag_bg"></div>
        <div class="drag_text">{{ data.confirmWords }}</div>
        <div ref="moveDiv" v-if="!data.confirmSuccess" @mousedown="mousedownFn($event)"
             @touchstart="mousedownFn($event)"
             class="handler handler_bg"
             style="position: absolute; top: 0;"
        >
          <el-icon color="#999999" size="24">
            <DArrowRight/>
          </el-icon>
        </div>
      </div>
    </div>
    <div class="login-animation5">
      <el-button type="primary" class="login-content-submit" @click="onSignIn"
                 :loading="state.loading.signIn">
        <span>{{ $t('message.login.account.accountBtnText') }}</span>
      </el-button>
    </div>

  </el-form>

</template>

<script setup lang="ts" name="loginAccount">
import {onMounted, getCurrentInstance, nextTick, reactive, computed, watch, vShow} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import {useI18n} from 'vue-i18n';
import Cookies from 'js-cookie';
import {storeToRefs} from 'pinia';
import {useThemeConfig} from '/@/stores/themeConfig';
import {Local, Session} from '/@/utils/storage';
import {formatAxis} from '/@/utils/formatTime';
import {NextLoading} from '/@/utils/loading';
import {DArrowRight} from '@element-plus/icons-vue'
import {useLoginApi} from "/@/api/login";
import {useChangeColor} from '/@/utils/theme';
import {JSEncrypt} from "jsencrypt/lib/JSEncrypt";
import request from '/@/utils/request';
import {useSystemApi} from "/src/api/hiwoo-base";
import {useUserInfo} from "/@/stores/userInfo";

// 定义子组件向父组件传值/事件
const props = defineProps({
  type: String,
  tag: Boolean,
  account: String,
  password: String,
  demoAccountData: {
    type: Object,
    default: () => ({
      username: '',
      password: '',
      title: ''
    })
  }
});

const emit = defineEmits(["update:type"]);

const {getLightColor, getDarkColor} = useChangeColor();
// 定义变量内容
const {t} = useI18n();
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);
const route = useRoute();
const router = useRouter();
const state = reactive({
  code: '',
  showCount: false,
  account: '',
  password: '',
  isShowPassword: false,
  ruleForm: {
    account: '',
    password: '',
    client: 'PC',
    ip: '',
    address: '',
  },
  loading: {
    signIn: false,
  },
  checked: false,
  tag: false,
  innerHeight: 0
});
watch(() => props.demoAccountData, (newVal) => {
  if (newVal.username && newVal.password) {
    state.ruleForm.account = newVal.username;
    state.ruleForm.password = newVal.password;
  }
}, {deep: true, immediate: true});
// 获取布局配置信息
const getThemeConfig = computed(() => {
  return themeConfig.value;
});

const {proxy} = getCurrentInstance()
const data = reactive({
  beginClientX: 0,           /*距离屏幕左端距离*/
  mouseMoveStata: false,     /*触发拖动状态  判断*/
  maxwidth: '',               /*拖动最大宽度，依据滑块宽度算出来的*/
  confirmWords: '拖动滑块验证',   /*滑块文字*/
  confirmSuccess: false /*验证成功判断*/,
});

//进入页面清空
onMounted(() => {
  nextTick(() => {

    // 根据滑块宽度计算可拖动最大宽度
    data.maxwidth = proxy.$refs.dragDiv.clientWidth - proxy.$refs.moveDiv.clientWidth;
    // 监听手指的触摸事件
    document.getElementsByTagName('html')[0].addEventListener('mousemove', mouseMoveFn);
    // 监听手指离开事件
    document.getElementsByTagName('html')[0].addEventListener('mouseup', moseUpFn)

    // 监听手指的触摸事件
    document.getElementsByTagName('html')[0].addEventListener('touchmove', mouseMoveFn);
    // 监听手指离开事件
    document.getElementsByTagName('html')[0].addEventListener('touchend', moseUpFn)

  })

  if (Cookies.get('checked')) {
    state.checked = Cookies.get('checked')
    state.ruleForm.account = Cookies.get('account')
    state.ruleForm.password = Cookies.get('password')
  }
  //获取当前地址ip
  getIp()
  if (window.location.href.includes('secureData')) {
    state.showCount = true;
  }
  state.innerHeight = window.innerHeight;
})

const checkLogin = () => {
  const urlParams = new URLSearchParams(window.location.search);
  const encryptedData = urlParams.get('secureData');

  // 解码 Base64 编码的查询字符串
  let decodedQueryString = '';
  if (encryptedData) {
    decodedQueryString = atob(encryptedData);
  }
  // 解析账号和密码信息
  const params = new URLSearchParams(decodedQueryString);
  state.ruleForm.account = state.account = params.get('account');
  state.ruleForm.password = state.password = params.get('password');
  setTimeout(() => {
    onSignIn();
  }, 50)
};

const getIp = () => {
  request({
    url: 'https://myip.ipip.net/',
    method: 'get',
  }).then(res => {
    const ipPattern = /当前 IP：(\d+\.\d+\.\d+\.\d+)/;
    const locationPattern = /来自于：([^\n<]+)/;
    const ipMatch = res.match(ipPattern);
    const locationMatch = res.match(locationPattern);
    if (ipMatch && locationMatch) {
      const ip = ipMatch[1];
      const location = locationMatch[1];
      state.ruleForm.ip = ip;
      state.ruleForm.address = location;
    }
  });
}

const mousedownFn = (e) => {
  if (!data.confirmSuccess) {
    e.preventDefault && e.preventDefault();   //阻止文字选中等 浏览器默认事件
    data.mouseMoveStata = true;
    data.beginClientX = e.clientX || e.changedTouches[0].clientX;
  }
}

//验证通过
const successFunction = () => {
  data.confirmSuccess = true
  data.confirmWords = '验证通过';

  if (window.addEventListener) {
    document.getElementsByTagName('html')[0].removeEventListener('mousemove', mouseMoveFn);
    document.getElementsByTagName('html')[0].removeEventListener('mouseup', moseUpFn);
  } else {
    document.getElementsByTagName('html')[0].removeEventListener('mouseup', () => {
    });
  }
  document.getElementsByClassName('drag_text')[0].style.color = '#fff'
  document.getElementsByClassName('drag_text')[0].style.background = 'var(--el-color-primary)'
  document.getElementsByClassName('handler')[0].style.left = data.maxwidth + 'px';
  document.getElementsByClassName('drag_bg')[0].style.width = data.maxwidth + 'px';
}


const mouseMoveFn = (e) => {
  if (data.mouseMoveStata) {
    let width = (e.clientX || e.changedTouches[0].clientX) - data.beginClientX;
    if (width > 0 && width <= data.maxwidth) {
      if (width >= data.maxwidth / 3) {
        document.getElementsByClassName('drag_text')[0].style.color = '#fff';
      }
      document.getElementsByClassName('handler')[0].style.left = width + 'px';
      document.getElementsByClassName('drag_bg')[0].style.width = width + 'px';
    } else if (width > data.maxwidth) {
      successFunction();
    }
  }
}


const moseUpFn = (e) => {
  data.mouseMoveStata = false;
  var width = e.clientX - data.beginClientX;
  if (width < data.maxwidth) {
    document.getElementsByClassName('handler')[0].style.left = 0 + 'px';
    document.getElementsByClassName('drag_bg')[0].style.width = 0 + 'px';
  }
};

// 时间获取
const currentTime = computed(() => {
  return formatAxis(new Date());
});

const encrypt = (password: string) => {
  // 公钥加密
  const encrypt = new JSEncrypt()
  const publicKey = import.meta.env.VITE_PublicKey;
  encrypt.setPublicKey(publicKey)
  return encrypt.encrypt(password)
}
// 登录
const onSignIn = async () => {
  if (!(state.ruleForm.account && state.ruleForm.password)) {
    ElMessage.warning('账号和密码不能为空！');
    return;
  }
  if (!(state.account && state.password)) {
    if (!data.confirmSuccess && state.innerHeight >= 655) {
      ElMessage.warning('请先滑动登录验证！');
      return;
    }
  }
  state.loading.signIn = true;
  const submitData = JSON.parse(JSON.stringify(state.ruleForm));
  submitData.password = <string>encrypt(state.ruleForm.password)
  useLoginApi().signIn(submitData).then(response => {
    if (response.code == 200) {
      // 存储 token 到浏览器缓存

      Session.set(response.data.tokenInfo.tokenName, response.data.tokenInfo.tokenValue);
      Session.set('TenantId', response.data.tokenInfo.tag)
      let userInfo = response.data;
      userInfo.roles = ['admin'];
      userInfo.authBtnList = ['btn.add', 'btn.del', 'btn.edit', 'btn.link'];
      Session.set('userInfo', userInfo);

      // 模拟数据，对接接口时，记得删除多余代码及对应依赖的引入。用于 `/src/stores/userInfo.ts` 中不同用户登录判断（模拟数据）
      Session.set('userName', response.data.name);

      if (state.checked) {
        Cookies.set('checked', state.checked);
        Cookies.set('account', state.ruleForm.account);
        Cookies.set('password', state.ruleForm.password);
      } else {
        Cookies.set('checked', state.checked);
        Cookies.remove('account');
        Cookies.remove('password');
      }
      signInSuccess(false);
    } else {
      ElMessage.warning(response.msg);
      Session.clear();
    }
    state.loading.signIn = false;
  })
  setTimeout(() => {
    state.loading.signIn = false;
  }, 1000)
};

// 登录成功后的跳转
const signInSuccess = (isNoPower: boolean | undefined) => {

  // if (isNoPower) {
  //   ElMessage.warning('抱歉，您没有登录权限');
  //   Session.clear();
  // } else {
  // 初始化登录成功时间问候语
  let currentTimeInfo = currentTime.value;
      // 登录成功，跳到转首页
      // 如果是复制粘贴的路径，非首页/登录页，那么登录成功后重定向到对应的路径中
      if (route.query?.redirect) {
        router.push({
          path: <string>route.query?.redirect,
          // query: Object.keys(<string>route.query?.params).length > 0 ? JSON.parse(<string>route.query?.params) : '',
        });
      } else {
        router.push('/');
      }
      // useSystemApi().startTrial();
      getSystemConfig()
      // 登录成功提示
      const signInText = t('message.signInText');
      ElMessage.success(`${currentTimeInfo}，${signInText}`);
      // 添加 loading，防止第一次进入界面时出现短暂空白
      NextLoading.start();
      // }
      state.loading.signIn = false;
};

const getSystemConfig = async () => {
  let userInfo = Session.get("userInfo")
  if (!userInfo) {
    return false
  }
  useSystemApi().getSystemConfig(userInfo.enterpriseId).then(response => {
    if (response.data) {
      getThemeConfig.value.smallLogo = response.data.systemLogo
      switch (response.data.theme) {
        case "white":
          getThemeConfig.value.primary = "#1890FF";
          getThemeConfig.value.menuBar = "#FFFFFF";
          getThemeConfig.value.menuBarColor = "#333333";
          getThemeConfig.value.menuBarActiveColor = "rgba(10,177,255,0.1)";
          document.documentElement.style.setProperty("--el-is-active-title-color", "#1890FF");
          document.documentElement.style.setProperty("--el-is-active-a-border", "4px solid #1890FF");
          document.documentElement.style.setProperty("--el-is-active-text-color", "#1890FF");
          document.documentElement.style.setProperty("--el-menu-child-bg-color", "#FFFFFF");
          document.documentElement.style.setProperty("--el-is-active-navbar-bg-color", "#ebf5ff");
          document.documentElement.style.setProperty("--el-is-active-navbar-color", "#1890FF");
          break;
        case "green":
          getThemeConfig.value.primary = "#12B36D";
          getThemeConfig.value.menuBar = "#1C2622";
          getThemeConfig.value.menuBarColor = "#FFFFFF";
          getThemeConfig.value.menuBarActiveColor = "#12B36D";
          document.documentElement.style.setProperty("--el-is-active-title-color", "#12B36D");
          document.documentElement.style.setProperty("--el-is-active-a-border", "4px solid #12B36D");
          document.documentElement.style.setProperty("--el-is-active-text-color", "#FFFFFF");
          document.documentElement.style.setProperty("--el-menu-child-bg-color", "#00110C");
          document.documentElement.style.setProperty("--el-is-active-navbar-bg-color", "#19b36614");
          document.documentElement.style.setProperty("--el-is-active-navbar-color", "#12B36D");
          break;
        case "blue":
          getThemeConfig.value.primary = "#1890FF";
          getThemeConfig.value.menuBar = "#222B33";
          getThemeConfig.value.menuBarColor = "#FFFFFF";
          getThemeConfig.value.menuBarActiveColor = "#1890FF";
          document.documentElement.style.setProperty("--el-is-active-title-color", "#1890FF");
          document.documentElement.style.setProperty("--el-is-active-a-border", "4px solid #1890FF");
          document.documentElement.style.setProperty("--el-is-active-text-color", "#FFFFFF");
          document.documentElement.style.setProperty("--el-menu-child-bg-color", "#091A29");
          document.documentElement.style.setProperty("--el-is-active-navbar-bg-color", "#ebf5ff14");
          document.documentElement.style.setProperty("--el-is-active-navbar-color", "#1890FF");
          break;
        case "orange":
          getThemeConfig.value.primary = "#FF770F";
          getThemeConfig.value.menuBar = "#302925";
          getThemeConfig.value.menuBarColor = "#FFFFFF";
          getThemeConfig.value.menuBarActiveColor = "#FF770F";
          document.documentElement.style.setProperty("--el-is-active-title-color", "#FF770F");
          document.documentElement.style.setProperty("--el-is-active-a-border", "4px solid #FF770F");
          document.documentElement.style.setProperty("--el-is-active-text-color", "#FFFFFF");
          document.documentElement.style.setProperty("--el-menu-child-bg-color", "#1F1710");
          document.documentElement.style.setProperty("--el-is-active-navbar-bg-color", "#ff770f14");
          document.documentElement.style.setProperty("--el-is-active-navbar-color", "#FF770F");
          break;
      }
      onColorPickerChange();
      onBgColorPickerChange('menuBar');
      onBgColorPickerChange('menuBarColor');
      onBgColorPickerChange('menuBarActiveColor');
      Session.set("theme", response.data.theme);
    }
  })
}
// 1、全局主题
const onColorPickerChange = () => {
  // 颜色加深
  document.documentElement.style.setProperty('--el-color-primary-dark-2', `${getDarkColor(getThemeConfig.value.primary, 0.1)}`);
  document.documentElement.style.setProperty('--el-color-primary', getThemeConfig.value.primary);
  // 颜色变浅
  for (let i = 1; i <= 9; i++) {
    document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, `${getLightColor(getThemeConfig.value.primary, i / 10)}`);
  }
  setDispatchThemeConfig();
};

// 2、菜单 / 顶栏
const onBgColorPickerChange = (bg: string) => {
  document.documentElement.style.setProperty(`--next-bg-${bg}`, themeConfig.value[bg]);
  if (bg === 'menuBar') {
    document.documentElement.style.setProperty(`--next-bg-menuBar-light-1`, getLightColor(getThemeConfig.value.menuBar, 0.05));
  }
  onTopBarGradualChange();
  onMenuBarGradualChange();
  onColumnsMenuBarGradualChange();
  setDispatchThemeConfig();
};

// 2、菜单 / 顶栏 --> 顶栏背景渐变
const onTopBarGradualChange = () => {
  setGraduaFun('.layout-navbars-breadcrumb-index', getThemeConfig.value.isTopBarColorGradual, getThemeConfig.value.topBar);
};
// 2、菜单 / 顶栏 --> 菜单背景渐变
const onMenuBarGradualChange = () => {
  setGraduaFun('.layout-container .el-aside', getThemeConfig.value.isMenuBarColorGradual, getThemeConfig.value.menuBar);
};
// 2、菜单 / 顶栏 --> 分栏菜单背景渐变
const onColumnsMenuBarGradualChange = () => {
  setGraduaFun('.layout-container .layout-columns-aside', getThemeConfig.value.isColumnsMenuBarColorGradual, getThemeConfig.value.columnsMenuBar);
};
// 2、菜单 / 顶栏 --> 背景渐变函数
const setGraduaFun = (el: string, bool: boolean, color: string) => {
  nextTick(() => {
    setTimeout(() => {
      let els = document.querySelector(el);
      if (!els) return false;
      document.documentElement.style.setProperty('--el-menu-bg-color', document.documentElement.style.getPropertyValue('--next-bg-menuBar'));
      if (bool) els.setAttribute('style', `background:linear-gradient(to bottom , ${color}, ${getLightColor(color, 0.5)})`);
      else els.setAttribute('style', ``);
      setLocalThemeConfig();
    }, 300);
  });
};

// 触发 store 布局配置更新
const setDispatchThemeConfig = () => {
  setLocalThemeConfig();
  setLocalThemeConfigStyle();
};
// 存储布局配置
const setLocalThemeConfig = () => {
  Local.remove('themeConfig');
  Local.set('themeConfig', getThemeConfig.value);
};
// 存储布局配置全局主题样式（html根标签）
const setLocalThemeConfigStyle = () => {
  Local.remove('themeConfigStyle');
  Local.set('themeConfigStyle', document.documentElement.style.cssText);
};
defineExpose({
  checkLogin,
  state
});
</script>
<style scoped lang="scss">
.login-content-form {
  margin-top: 15px;
  @for $i from 1 through 4 {
    .login-animation#{$i} {
      opacity: 0;
      animation-name: error-num;
      animation-duration: 0.5s;
      animation-fill-mode: forwards;
      animation-delay: calc($i/10) + s;
    }
  }

  .login-content-password {
    display: inline-block;
    width: 20px;
    cursor: pointer;

    &:hover {
      color: #909399;
    }
  }

  .login-content-code {
    width: 100%;
    padding: 0;
    font-weight: bold;
    letter-spacing: 5px;
  }

  .login-content-submit {
    width: 100%;
    //letter-spacing: 2px;
    font-weight: bold;
    font-size: 24px;
    height: 56px;
    border-radius: 8px;
  }
}

.login-animation1 {
  margin-bottom: 24px;
}

.login-animation2 {
  margin-bottom: 24px;
}

.login-animation3 {
  display: flex;
  justify-content: space-around;
  margin-bottom: 24px;
}

.login-animation4 {
  margin-bottom: 40px;
}

.login-animation5 {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.login-animation6 {
  display: flex;
  justify-content: center;
}

.drag {
  position: relative;
  background-color: #F4F6F8;
  width: 100%;
  height: 48px;
  opacity: 1;
  line-height: 48px;
  text-align: center;
  border-radius: 8px;
}

.handler {
  cursor: move;
  width: 83px;
  height: 48px;
  background: #FFFFFF;
  border-radius: 8px;
  box-shadow: 0px 0px 2px 0px rgba(65, 113, 129, 0.2);
  opacity: 1;
  padding-top: 6px;
}

.handler_ok_bg {
  background: #fff url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ZDhlNWY5My05NmI0LTRlNWQtOGFjYi03ZTY4OGYyMTU2ZTYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NDlBRDI3NjVGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NDlBRDI3NjRGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDphNWEzMWNhMC1hYmViLTQxNWEtYTEwZS04Y2U5NzRlN2Q4YTEiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NGQ4ZTVmOTMtOTZiNC00ZTVkLThhY2ItN2U2ODhmMjE1NmU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+k+sHwwAAASZJREFUeNpi/P//PwMyKD8uZw+kUoDYEYgloMIvgHg/EM/ptHx0EFk9I8wAoEZ+IDUPiIMY8IN1QJwENOgj3ACo5gNAbMBAHLgAxA4gQ5igAnNJ0MwAVTsX7IKyY7L2UNuJAf+AmAmJ78AEDTBiwGYg5gbifCSxFCZoaBMCy4A4GOjnH0D6DpK4IxNSVIHAfSDOAeLraJrjgJp/AwPbHMhejiQnwYRmUzNQ4VQgDQqXK0ia/0I17wJiPmQNTNBEAgMlQIWiQA2vgWw7QppBekGxsAjIiEUSBNnsBDWEAY9mEFgMMgBk00E0iZtA7AHEctDQ58MRuA6wlLgGFMoMpIG1QFeGwAIxGZo8GUhIysmwQGSAZgwHaEZhICIzOaBkJkqyM0CAAQDGx279Jf50AAAAAABJRU5ErkJggg==") no-repeat center;
}

.drag_bg {
  width: 83px;
  height: 48px;
  background: var(--el-color-primary);
  box-shadow: 0px 0px 2px 0px rgba(65, 113, 129, 0.2);
  border-radius: 10px;
  opacity: 1;
}

.drag_text {
  position: absolute;
  top: 0px;
  width: 100%;
  text-align: center;
  -moz-user-select: none;
  -webkit-user-select: none;
  user-select: none;
  -o-user-select: none;
  -ms-user-select: none;
  color: #999999;
}
</style>

