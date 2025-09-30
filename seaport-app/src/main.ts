import dayjs from 'dayjs'
import { createSSRApp } from 'vue'
import * as Pinia from 'pinia'
import App from './App.vue'
import globalMixin from './mixin/global-mixin'
import i18n, { t } from './locale'
import 'uno.css'
import './styles/global.scss'
import './styles/tailwind.css'

export function createApp() {
  const app = createSSRApp(App)
  app.config.globalProperties.$dayjs = dayjs
  app.use(Pinia.createPinia())
  app.use(i18n)
  app.mixin(globalMixin)

  // 设置全局属性
  app.config.globalProperties.$t = t

  return {
    app,
    Pinia
  }
}

// 导出t函数供其他组件使用
export { t }