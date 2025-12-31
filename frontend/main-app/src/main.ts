import {createApp} from 'vue';
import pinia from '/@/stores/index';
import App from '/@/App.vue';
import router from '/@/router';
import {i18n} from '/@/i18n/index';
import other from '/@/utils/other';

import ElementPlus from 'element-plus';
import '/@/theme/index.scss';
import VueGridLayout from 'vue-grid-layout';
import * as ElIconModules from '@element-plus/icons-vue'

const app = createApp(App);

// directive(app);
other.elSvg(app);

for (let iconName in ElIconModules) {
    app.component(iconName, ElIconModules[iconName])
}

app.use(pinia).use(router).use(ElementPlus).use(i18n).use(VueGridLayout).mount('#main-app');
