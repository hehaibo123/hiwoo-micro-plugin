<template>
  <div class="login-container flex" v-show="state.show">
    <div class="login-left">
      <img v-if="state.backgroundUrl !== ''" class="background" :src="state.backgroundUrl"/>
      <img v-else-if="state.background == 'white'" class="background" :src="loginBgWhite"/>
      <img v-else-if="state.background == 'blue'" class="background" :src="loginBgBlue"/>
      <img v-else-if="state.background == 'green'" class="background" :src="loginBgGreen"/>
      <img v-else-if="state.background == 'orange'" class="background" :src="loginBgOrange"/>
      <img
          class="topLogo"
          :src="state.loginLogo || Logo"
      />
    </div>
    <div class="login-right flex">
      <div class="login-right-warp flex-margin"
           :style="{transform:'translateY(-50%)'}">
        <div class="login-right-warp-mian" style="margin-bottom: 16px;">
          <div  class="login-right-warp-main-title">
            {{ $t('message.staticRoutes.welcomeLogin') }}
          </div>
          <div  class="login-right-warp-main-title2">示例工程</div>
          <el-tabs v-model="activeIndex" class="demo-tabs" @tab-change="tabClickHandle">
            <el-tab-pane label="账号登录" name="account">
              <Account ref="AccountRef" :tag="state.tag"
                       :demo-account-data="demoAccountData"></Account>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {defineAsyncComponent, onMounted, reactive, computed, ref, nextTick, watch} from 'vue';
import {storeToRefs} from 'pinia';
import {useThemeConfig} from '/@/stores/themeConfig';
import {NextLoading} from '/@/utils/loading';

import loginBgWhite from '/@/assets/login/login-white.png';
import loginBgBlue from '/@/assets/login/login-blue.png';
import loginBgGreen from '/@/assets/login/login-green.png';
import loginBgOrange from '/@/assets/login/login-orange.png';
import {useLoginApi} from "/@/api/login";
import {ElMenu, ElMessage, TabPaneName} from "element-plus";
import {Local, Session} from "/@/utils/storage";
import Cookies from "js-cookie";
import {useRoute, useRouter} from "vue-router";
import {formatAxis} from "/@/utils/formatTime";
import {useChangeColor} from "/@/utils/theme";
import {JSEncrypt} from "jsencrypt/lib/JSEncrypt";

// 引入组件
const Account = defineAsyncComponent(() => import('/@/views/login/component/account.vue'));
import Logo from '/src/assets/logo.png';


const AccountRef = ref();
// 定义变量内容
const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);
const state = reactive({
  ruleForm: {
    account: '',
    password: '',
    client: 'PC',
    ip: '',
    address: '',
  },
  stopPolling: false, // 是否停止轮询
  openid: '',
  tenantId: '',
  nowSelectLoginType: 'account',
  backgroundUrl: '',
  loginLogo: '',
  copyright: '',
  companyName: '',
  tabsActiveName: 'account',
  isScan: false,
  background: 'white',
  imgUrl: '', // 二维码图片 URL
  key: '',    // 登录用的 key
  showQr: false,
  isShowPassword: false,
  loading: {
    signIn: false,
  },
  checked: false,
  tag: false,
  innerHeight: 0,
  isShowWechat: false,
  isShowPhone: false,
  show: true,
  theme: 'white',
});

// 选项栏
let activeIndex = ref(state.nowSelectLoginType);

// 获取布局配置信息
const getThemeConfig = computed(() => {
  return themeConfig.value;
});
// 获取当前年份
const getCurrentYear = new Date().getFullYear();

// 打开ICP备案信息
const openICP = () => {
  window.open('https://beian.miit.gov.cn/');
};

const getSysSettingByDomain = async () => {
  //根据当前域名获取自定义配置
  const domain = window.location.host;
  const params = {
    domain: domain
  };
  await useLoginApi().getSysSetting(params).then(response => {
    if (response.data) {
      themeConfig.value.globalTitle = response.data.loginName;
      themeConfig.value.globalViceTitle = response.data.systemName;
      themeConfig.value.smallLogo = response.data.systemLogo;
      state.backgroundUrl = response.data.background;
      state.companyName = response.data.company;
      state.copyright = response.data.copyright;
      state.background = response.data.loginTheme;
      changeTheme(response.data.loginTheme);
      state.tenantId = response.data.tenantId;
      state.loginLogo = response.data.loginLogo;
      state.theme = response.data.theme;
    }
  })
};
const route = useRoute();
const router = useRouter();
// 时间获取
const currentTime = computed(() => {
  return formatAxis(new Date());
});

const {getLightColor, getDarkColor} = useChangeColor();


// 登录成功后的跳转
const signInSuccess = (isNoPower: boolean | undefined) => {

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
  changeTheme(state.theme);
  // 登录成功提示
  const signInText = '欢迎回来！';
  ElMessage.success(`${currentTimeInfo}，${signInText}`);
  // 添加 loading，防止第一次进入界面时出现短暂空白
  NextLoading.start();
  // }
  state.loading.signIn = false;
};

const changeTheme = (theme: string) => {
  switch (theme) {
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


// 页面加载时
onMounted(async () => {
  //根据当前域名获取自定义配置
  await getSysSettingByDomain();
  state.innerHeight = window.innerHeight;
  if (window.location.href.includes('secureData')) {
  } else if (window.location.href.includes('jumpData')) {
    state.show = false
    state.stopPolling = true
    NextLoading.start();
    getLogin()
  }
});
const getLogin = () => {
  const urlParams = new URLSearchParams(window.location.search);
  // }else if(window.location.href.includes('jumpData')){
  const encryptedData = urlParams.get('jumpData');
  // 解码 Base64 编码的查询字符串
  let decodedQueryString = '';
  if (encryptedData) {
    decodedQueryString = atob(encryptedData);
  }
  // 解析账号和密码信息
  const params = new URLSearchParams(decodedQueryString);
  state.ruleForm.account = params.get('account');
  state.ruleForm.password = params.get('password');
  setTimeout(() => {
    onSignIn();
  }, 50)
};
const encrypt = (password: string) => {
  // 公钥加密
  const encrypt = new JSEncrypt()
  const publicKey = import.meta.env.VITE_PublicKey;
  encrypt.setPublicKey(publicKey)
  return encrypt.encrypt(password)
}
const onSignIn = async () => {
  state.loading.signIn = true;
  const submitData = JSON.parse(JSON.stringify(state.ruleForm));
  submitData.password = <string>encrypt(state.ruleForm.password)
  useLoginApi().signIn(submitData).then(response => {
    if (response.code == 200) {
      // 存储 token 到浏览器缓存
      Session.set('satoken', Math.random().toString(36).substr(0));
      Session.set(response.data.tokenInfo.tokenName, response.data.tokenInfo.tokenValue);
      Session.set('TenantId', response.data.tokenInfo.tag)
      let userInfo = response.data;
      userInfo.roles = ['admin'];
      userInfo.authBtnList = ['btn.add', 'btn.del', 'btn.edit', 'btn.link'];
      Session.set('userInfo', userInfo);

      // 模拟数据，对接接口时，记得删除多余代码及对应依赖的引入。用于 `/src/stores/userInfo.ts` 中不同用户登录判断（模拟数据）
      Cookies.set('userName', response.data.name);
      signInSuccess(false);
      // NextLoading.done();

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


/**
 * 切换tabs事件
 * @param tab
 * @param event
 */
const tabClickHandle = (tab: TabPaneName, event: Event) => {

}

const demoAccountData = reactive({
  username: '',
  password: '',
  title: ''
});
// 填充账号密码方法
const fillCredentials = (account: any) => {
  demoAccountData.username = account.username;
  demoAccountData.password = account.password;
  demoAccountData.title = account.title;

}

</script>

<style scoped lang="scss">
.topLogo {
  position: absolute;
  top: 48px;
  left: 48px;
  z-index: 1;
  width: 17%; /* 宽度设置为父容器宽度的10% */
  height: auto; /* 高度自动调整以保持图像比例 */
}

.login-container {
  height: 100%;
  background: var(--el-color-white);

  ::v-deep(.el-tabs__item) {
    font-size: 18px;
    font-weight: 500;

  }

  /* 在整个屏幕底部居中显示版权样式 */
  .footer {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    text-align: center;
    padding: 10px 0;
    font-size: 14px;
    color: #666;

    .footerSpan {
      background-color: white;
      //设置透明度
      opacity: 0.5;

      //圆角
      border-radius: 5px;
      padding: 5px;
    }
  }


  .login-left {
    //flex: 1;
    position: relative;
    //background-color: rgba(211, 239, 255, 1);
    //margin-right: 100px;
    height: 100%;
    width: 100%;

    .background {
      width: 100%;
      height: 100%;
    }

    .login-left-logo {
      display: flex;
      align-items: center;
      position: absolute;
      top: 50px;
      left: 80px;
      z-index: 1;
      animation: logoAnimation 0.3s ease;

      img {
        width: 52px;
        height: 52px;
      }

      .login-left-logo-text {
        display: flex;
        flex-direction: column;

        span {
          margin-left: 10px;
          font-size: 28px;
          color: #26a59a;
        }

        .login-left-logo-text-msg {
          font-size: 12px;
          color: #32a99e;
        }
      }
    }

    .login-left-img {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 100%;
      height: 52%;

      img {
        width: 100%;
        height: 100%;
        animation: error-num 0.6s ease;
      }
    }

    .login-left-waves {
      position: absolute;
      top: 0;
      right: -100px;
    }
  }

  .login-right {
    width: 70%;

    .login-right-warp {

      //border: 1px solid var(--el-color-primary-light-3);
      border-radius: 3px;
      width: 550px;
      position: fixed;
      overflow: hidden;
      background-color: var(--el-color-white);
      //padding: 0px 0px 0px 100px;
      padding: 16px 16px 16px 7%;
      top: 50%;
      //transform: translateY(-50%) scale(0.5);


      .login-right-warp-one,
      .login-right-warp-two {
        position: absolute;
        display: block;
        width: inherit;
        height: inherit;

        &::before,
        &::after {
          content: '';
          position: absolute;
          z-index: 1;
        }
      }

      .login-right-warp-one {
        &::before {
          filter: hue-rotate(0deg);
          top: 0px;
          left: 0;
          width: 100%;
          height: 3px;
          background: linear-gradient(90deg, transparent, var(--el-color-primary));
          animation: loginLeft 3s linear infinite;
        }

        &::after {
          filter: hue-rotate(60deg);
          top: -100%;
          right: 2px;
          width: 3px;
          height: 100%;
          background: linear-gradient(180deg, transparent, var(--el-color-primary));
          animation: loginTop 3s linear infinite;
          animation-delay: 0.7s;
        }
      }

      .login-right-warp-two {
        &::before {
          filter: hue-rotate(120deg);
          bottom: 2px;
          right: -100%;
          width: 100%;
          height: 3px;
          background: linear-gradient(270deg, transparent, var(--el-color-primary));
          animation: loginRight 3s linear infinite;
          animation-delay: 1.4s;
        }

        &::after {
          filter: hue-rotate(300deg);
          bottom: -100%;
          left: 0px;
          width: 3px;
          height: 100%;
          background: linear-gradient(360deg, transparent, var(--el-color-primary));
          animation: loginBottom 3s linear infinite;
          animation-delay: 2.1s;
        }
      }

      .login-right-warp-mian {
        display: flex;
        flex-direction: column;
        height: 100%;

        .login-right-warp-main-title {
          height: 32px;
          line-height: 32px;
          font-size: 32px;
          //font-family: PingFang SC-Bold, PingFang SC;
          font-weight: bold;
          color: #333333;
          //text-align: center;
          //letter-spacing: 3px;
          animation: logoAnimation 0.3s ease;
          animation-delay: 0.3s;
          color: var(--el-text-color-primary);
        }

        .login-right-warp-main-title2 {
          margin-top: 16px;
          height: 36px;
          line-height: 36px;
          font-size: 36px;
          font-weight: bold;
          color: #333333;
          //letter-spacing: 3px;
          animation: logoAnimation 0.3s ease;
          animation-delay: 0.3s;
          color: var(--el-text-color-primary);
          margin-bottom: 50px;
        }

        .login-right-warp-main-form {
          flex: 1;
          //padding: 0 50px 50px;

          .login-content-main-sacn {
            position: absolute;
            top: 0;
            right: 0;
            width: 50px;
            height: 50px;
            overflow: hidden;
            cursor: pointer;
            transition: all ease 0.3s;
            color: var(--el-color-primary);

            &-delta {
              position: absolute;
              width: 35px;
              height: 70px;
              z-index: 2;
              top: 2px;
              right: 21px;
              background: var(--el-color-white);
              transform: rotate(-45deg);
            }

            &:hover {
              opacity: 1;
              transition: all ease 0.3s;
              color: var(--el-color-primary) !important;
            }

            i {
              width: 47px;
              height: 50px;
              display: inline-block;
              font-size: 48px;
              position: absolute;
              right: 1px;
              top: 0px;
            }
          }
        }
      }
    }
  }

  .account-card {
    backdrop-filter: blur(8px);
    background-color: rgba(255, 255, 255, 0.8);

    &:hover {
      background-color: rgba(255, 255, 255, 0.95);
      transform: translateY(-2px);
    }
  }

  .grid {
    display: grid;
    gap: 1.5rem;
  }

  .grid-cols-2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .text-primary {
    color: var(--el-color-primary);
  }

  .border-primary {
    border-color: var(--el-color-primary);
  }
}
</style>
