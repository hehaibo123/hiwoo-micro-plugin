import {
    registerMicroApps,
    start
} from "qiankun";
import {Session, Local} from "/@/utils/storage";

/**
 * @name 声明子应用挂载dom，如果不需要做keep-alive，则只需要一个dom即可；
 */
const appContainer = "#application";

/**
 * @name 启用qiankun微前端应用
 * @param {Array} list 应用注册表信息
 */
const qianKunStart = async (list: Array<any>) => {
    /**
     * @name 处理子应用注册表数据
     */
    let apps: any[] = []; // 子应用数组盒子
    // 默认注册应用路由前缀
    menuHandle(list, list, apps);

    /**
     * @name 注册子应用
     * @param {Array} list subApps
     */
    registerMicroApps(apps, {
        beforeLoad: [
            app => {
                console.log('[LifeCycle] before load %c%s', 'color: green;', app.name);
            },
        ],
        beforeMount: [
            app => {
                console.log('[LifeCycle] before mount %c%s', 'color: green;', app.name);
            },
        ],
        afterMount: [
            app => {
                console.log('[LifeCycle] after mount %c%s', 'color: green;', app.name);
            },
        ],
        afterUnmount: [
            app => {
                console.log('[LifeCycle] after unmount %c%s', 'color: green;', app.name);
            },
        ],
    })

    /**
     * @name 设置默认进入的子应用
     * @param {String} 需要进入的子应用路由前缀
     */
// setDefaultMountApp(defaultApp);

    /**
     * @name 启动微前端
     */
    start({
        prefetch: 'all',
        sandbox: {
            // strictStyleIsolation: true,
            experimentalStyleIsolation: true
        },
    });

// 添加全局异常捕获
// addGlobalUncaughtErrorHandler((handler) => {
//     console.log("异常捕获", handler);
// });
// /**
//  * @name 微前端启动进入第一个子应用后回调函数
//  */
// runAfterFirstMounted(() => {
//
// });

}

const menuHandle = (oldMenus: any, menus: any, apps: any) => {
    menus.forEach(item => {
        if (import.meta.env.MODE === 'development') {
            switch (item.pluginId) {
                case "micro-app-industry":
                    item.entry = "//localhost:6001/micro-app-industry/"
                    break;
                case "micro-app-equipment":
                    item.entry = "//localhost:6002/micro-app-equipment/"
                    break;
                case "micro-app-enterprise":
                    item.entry = "//localhost:6003/micro-app-enterprise/"
                    break;
            }
        }
        let flag = true;
        apps.forEach(app => {
            if (item.pluginId == app.name) {
                flag = false;
                return flag;
            }
        })

        if (item.pluginId && flag) {
            apps.push({
                name: item.pluginId,
                entry: item.entry,
                container: appContainer,
                activeRule: item.activeRule,
                props: {
                    routes: [],
                    routerBase: item.activeRule,
                    satoken: Session.get("satoken"),
                    userInfo: Session.get("userInfo"),
                    status: Session.get("status"),
                    oldRoutes: oldMenus,
                    globalI18n: Local.get("themeConfig").globalI18n,
                    theme: Session.get("theme"),
                }
            })
        }

        if (item.children) {
            menuHandle(oldMenus, item.children, apps);
        }
    })
}

export default qianKunStart;
