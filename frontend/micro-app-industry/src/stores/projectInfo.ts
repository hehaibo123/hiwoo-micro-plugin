import {defineStore} from 'pinia';
import Cookies from 'js-cookie';
import {Session} from '/@/utils/storage';

/**
 * 项目信息
 * @methods setProjectInfos 设置项目信息
 */
export const useProjectInfo = defineStore('projectInfo', {
    state: (): ProjectInfosState => ({
        projectInfos: {
            id: '',
            name: '',
            type: '',
            pid: '',
            description: '',
            isEnabled: '',
            orgId: '',
            tenantId: '',
            tag: '',
        },
    }),
    actions: {
        async setProjectInfos() {
            // 存储项目信息到浏览器缓存
            if (Session.get('projectInfo')) {
                this.projectInfos = Session.get('projectInfo');
            }
        },
    },
});
