// seaport-app/src/locale/index.ts
import { createI18n } from 'vue-i18n'
import zhHans from './zh-Hans.json' // 中文简体
import id from './id.json' // 印尼语

// 获取当前系统语言，优先从存储中获取，否则获取系统默认语言
const defaultLang =
  uni.getStorageSync('language') || uni.getLocale() || 'zh-Hans'

const messages = {
  'zh-Hans': zhHans,
  id
}

const i18n = createI18n({
  legacy: false, // 推荐组合式API，支持Vue 3
  locale: defaultLang, // 当前语言
  messages,
  globalInjection: true, // 全局注入$t函数
  silentTranslationWarn: true, // 静默翻译警告
  silentFallbackWarn: true // 静默回退警告
})

// 导出t函数供组件使用
export const { t } = i18n.global

export default i18n