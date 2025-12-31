import request from '/@/utils/request';

/**
 * （不建议写成 request.post(xxx)，因为这样 post 时，无法 params 与 data 同时传参）
 *
 * 登录api接口集合
 * @method signIn 用户登录
 * @method signOut 用户退出登录
 */
export function useLoginApi() {
    return {

        getSysSetting: (params?: object) => {
            return request({
                url: '/system/settings/get-by-domain',
                method: 'GET',
                params,
            });
        },

        signIn: (data: object) => {
            return request({
                url: '/uaa/v1/auth/login',
                method: 'POST',
                data,
            });
        },
        signOut: (data: object) => {
            return request({
                url: '/uaa/v1/auth/logout',
                method: 'POST',
                data,
            });
        },
        getMessages: (readStatus: number) => {
            return request({
                url: '/system/messages/list/' + readStatus,
                method: 'GET',
            });
        },
        allRead: (data: object) => {
            return request({
                url: '/system/messages/read-all',
                method: 'PUT',
                data,
            });
        },
        readOne(param: any) {
            return request({
                url: '/system/messages/read/',
                method: 'PUT',
                params: param
            });
        },
        changePassword: (data: object) => {
            return request({
                url: '/system/users/change-password',
                method: 'POST',
                data,
            });
        },
    };
}
