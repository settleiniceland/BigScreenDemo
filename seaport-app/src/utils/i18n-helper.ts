import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/store'
import { SUPPORTED_LANGUAGES, DEFAULT_LANGUAGE } from '@/contans/language'

/**
 * 国际化辅助工具
 * 提供便捷的多语言功能
 */

// 获取当前语言
export function getCurrentLanguage(): string {
    return uni.getStorageSync('language') || uni.getLocale() || DEFAULT_LANGUAGE
}

// 设置语言并持久化
export function setLanguage(lang: string): boolean {
    if (!SUPPORTED_LANGUAGES.includes(lang)) {
        console.warn(`不支持的语言: ${lang}`)
        return false
    }

    const store = useUserStore()
    store.setLanguage(lang)
    return true
}

// 在组件中使用的hook
export function useAppI18n() {
    const { t, locale } = useI18n()
    const store = useUserStore()

    // 切换语言
    const switchLanguage = (lang: string) => {
        if (setLanguage(lang)) {
            locale.value = lang
            console.log(`语言已切换至: ${lang}`)
        }
    }

    // 获取当前语言状态
    const currentLang = () => store.language

    return {
        t,
        locale,
        switchLanguage,
        currentLang
    }
}

// 格式化文本（支持参数替换）
export function formatMessage(key: string, params?: Record<string, any>): string {
    const { t } = useI18n()
    return t(key, params)
} 