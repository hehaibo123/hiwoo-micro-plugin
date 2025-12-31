import request from '/@/utils/request';

/**
 * 以下为模拟接口地址，gitee 的不通，就换自己的真实接口地址
 *
 * （不建议写成 request.post(xxx)，因为这样 post 时，无法 params 与 data 同时传参）
 *
 * 后端控制菜单模拟json，路径在 https://gitee.com/lyt-top/vue-next-admin-images/tree/master/menu
 * 后端控制路由，isRequestRoutes 为 true，则开启后端控制路由
 * @method getAdminMenu 获取后端动态路由菜单(admin)
 * @method getTestMenu 获取后端动态路由菜单(test)
 */
export function useBaseApi() {
    return {
        getResourcesByProjectTree: (resourceTag: string) => {
            return request({
                url: import.meta.env.VITE_SYSTEM_SUFFIX +  '/projects/v1/tree/'+resourceTag,
                method: 'GET',
            });
        },
    };
}

export function useIotApi() {
    return {
        getVariables: (params?: object) => {
            return request({
                url: import.meta.env.VITE_EQUIPMENT_SUFFIX +  '/v1/equipment/variable/list',
                method: 'GET',
                params,
            });
        },
        getHistoryData: (params?: object) => {
            return request({
                url: import.meta.env.VITE_EQUIPMENT_SUFFIX +  '/v1/equipment/history/list-by-page',
                method: 'GET',
                params,
            });
        },
        getHistoryAlarm: (params?: object) => {
            return request({
                url: import.meta.env.VITE_EQUIPMENT_SUFFIX +  '/v1/alarm/history/list-by-page',
                method: 'GET',
                params,
            });
        },
    };
}
