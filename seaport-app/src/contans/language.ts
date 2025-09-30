// 语言配置常量
export const LANGUAGE_OPTIONS = [
    {
        label: 'zh-Hans',
        value: 'zh-Hans',
        name: '简体中文',
        translationKey: 'language.zhHans'
    },
    {
        label: 'id',
        value: 'id',
        name: 'Bahasa Indonesia',
        translationKey: 'language.indonesian'
    }
]

// 默认语言
export const DEFAULT_LANGUAGE = 'zh-Hans'

// 支持的语言列表
export const SUPPORTED_LANGUAGES = ['zh-Hans', 'id']

// 语言切换成功提示键
export const LANGUAGE_CHANGE_SUCCESS_KEY = 'person.changeLanguageSuccess'

// 获取语言显示名称
export function getLanguageDisplayName(langCode: string): string {
    const option = LANGUAGE_OPTIONS.find(opt => opt.value === langCode)
    return option?.name || langCode
}

// 验证语言代码是否支持
export function isSupportedLanguage(langCode: string): boolean {
    return SUPPORTED_LANGUAGES.includes(langCode)
} 