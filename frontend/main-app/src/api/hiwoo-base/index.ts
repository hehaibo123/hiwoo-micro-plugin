import request from '/@/utils/request';

/**
 * 以下为模拟接口地址，gitee 的不通，就换自己的真实接口地址
 *
 * （不建议写成 request.POST(xxx)，因为这样 POST 时，无法 params 与 data 同时传参）
 *
 * 后端控制菜单模拟json，路径在 https://gitee.com/lyt-top/vue-next-admin-images/tree/master/menu
 * 后端控制路由，isRequestRoutes 为 true，则开启后端控制路由
 * @method getAdminMenu 获取后端动态路由菜单(admin)
 * @method getTestMenu 获取后端动态路由菜单(test)
 */
export function useUserApi() {
    return {
        headSculpture: (data: object) => {
            return request({
                url: '/system/users/head-sculpture',
                method: 'POST',
                data,
            });
        },
        resetPassword: (id: string) => {
            return request({
                url: '/system/users/reset-password/' + id,
                method: 'PUT',
            });
        },
        editUser: (data: object) => {
            return request({
                url: '/system/users/update',
                method: 'PUT',
                data,
            });
        },
    }
}

export function useSystemApi() {
    return {
        getSystemConfig: (params: string) => {
            return request({
                url: '/system/settings/get/' + params,
                method: 'GET',
                params,
            });
        },
    };
}
