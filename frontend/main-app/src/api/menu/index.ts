import request from '/@/utils/request';

/**
 * 以下为模拟接口地址，gitee 的不通，就换自己的真实接口地址
 *
 * （不建议写成 request.post(xxx)，因为这样 post 时，无法 params 与 data 同时传参）
 *
 * 后端控制菜单模拟json，路径在 https://gitee.com/lyt-top/vue-next-admin-images/tree/master/menu
 * 后端控制路由，isRequestRoutes 为 true，则开启后端控制路由
 */
export function useMenuApi() {
    return {
        getMenus: () => {
            return request({
                url: '/system/menus/tree',
                method: 'GET'
            });
        },
        getAnnouncement: () => {
            return request({
                url: '/system/announcements/get',
                method: 'GET',
            });
        },
        getAnnouncements: (status: number) => {
            return request({
                url: '/system/announcements/list/' + status,
                method: 'GET',
            });
        },
        readAnnouncement: (data: object) => {
            return request({
                url: '/system/announcements/read',
                method: 'PUT',
                data
            });
        },
        allRead: () => {
            return request({
                url: '/system/announcements/read-all',
                method: 'PUT',
            });
        },
    };
}
