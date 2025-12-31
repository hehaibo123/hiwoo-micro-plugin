import {createApp} from 'vue';
import pinia from '/@/stores/index';
import App from '/@/App.vue';
import router from '/@/router';
import {directive} from '/@/directive/index';
import {i18n} from '/@/i18n/index';
import other from '/@/utils/other';

import ElementPlus from 'element-plus';
import '/@/theme/index.scss';
import VueGridLayout from 'vue-grid-layout';
import {renderWithQiankun, qiankunWindow} from 'vite-plugin-qiankun/dist/helper'
import {Session} from "/@/utils/storage";
import {useRequestOldRoutes} from "/@/stores/requestOldRoutes";


let instance = null;
let history = null;

const render = ({routes, routerBase, container} = {}) => {
    instance = createApp(App);
    directive(instance);
    other.elSvg(instance);
    instance.use(pinia).use(router).use(ElementPlus).use(i18n).use(VueGridLayout).mount(qiankunWindow.__POWERED_BY_QIANKUN__ ? container.querySelector('#app') : '#app');
}


export const bootstrap = async (): Promise<void> => {
}

export const mount = async (props: any): Promise<void> => {
    props.onGlobalStateChange &&
    props.onGlobalStateChange(
        (value, prev) => console.log(`[onGlobalStateChange - ${props.name}]:`, value, prev),
        true
    )
    useRequestOldRoutes().setRequestOldRoutes(props.oldRoutes);

    Session.set('userInfo', props.userInfo);
    Session.set('satoken', props.satoken);
    Session.set("status", props.status);

    props.setGlobalState &&
    props.setGlobalState({
        ignore: props.name,
        user: {
            name: props.name
        }
    })

    render(props)
    instance.config.globalProperties.$onGlobalStateChange = props.onGlobalStateChange
    instance.config.globalProperties.$setGlobalState = props.setGlobalState
}

export const unmount = async (): Promise<void> => {
    instance.unmount()
    instance._container.innerHTML = ''
    instance = null
}

export const update = async (): Promise<void> => {
    console.log('%c ', 'color: green ', 'vue3.0 app update')
}
qiankunWindow.__POWERED_BY_QIANKUN__ ? renderWithQiankun({bootstrap, mount, unmount, update}) : render();


