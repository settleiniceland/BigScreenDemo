import { useI18n } from 'vue-i18n'

/**
 * 字典项接口定义
 */
export interface DictItem {
    label: string
    value: string | number
    color?: string
    type?: string
    i18n?: boolean // 标识是否需要国际化
}

/**
 * 统一的字典格式化函数
 * 处理前端自定义和后端返回的字典项，支持国际化
 * @param item 字典项
 * @param useI18nTranslation 是否使用国际化翻译
 * @returns 格式化后的字典项
 */
export function formatDictItem(item: DictItem, useI18nTranslation: boolean = true): DictItem {
    const { t } = useI18n()

    // 如果不使用国际化或者item明确标识不需要国际化，直接返回
    if (!useI18nTranslation || item.i18n === false) {
        return item
    }

    // 尝试进行国际化翻译
    try {
        const translatedLabel = t(item.label)
        // 如果翻译结果和原始key相同，说明没有找到翻译，使用原始label
        const finalLabel = translatedLabel === item.label ? item.label : translatedLabel

        return {
            ...item,
            label: finalLabel
        }
    } catch (error) {
        console.warn(`翻译失败: ${item.label}`, error)
        return item
    }
}

/**
 * 批量格式化字典数组
 * @param items 字典项数组
 * @param useI18nTranslation 是否使用国际化翻译
 * @returns 格式化后的字典项数组
 */
export function formatDictItems(items: DictItem[], useI18nTranslation: boolean = true): DictItem[] {
    return items.map(item => formatDictItem(item, useI18nTranslation))
}

/**
 * 格式化字典对象
 * @param dictObject 字典对象
 * @param useI18nTranslation 是否使用国际化翻译
 * @returns 格式化后的字典对象
 */
export function formatDictObject(dictObject: Record<string, DictItem>, useI18nTranslation: boolean = true): Record<string, DictItem> {
    const result: Record<string, DictItem> = {}

    Object.keys(dictObject).forEach(key => {
        result[key] = formatDictItem(dictObject[key], useI18nTranslation)
    })

    return result
}

/**
 * 根据value查找字典项的label（已国际化）
 * @param items 字典项数组
 * @param value 要查找的值
 * @param useI18nTranslation 是否使用国际化翻译
 * @returns 找到的label，找不到返回空字符串
 */
export function findDictLabel(items: DictItem[], value: string | number, useI18nTranslation: boolean = true): string {
    const item = items.find(item => item.value === value)
    if (!item) return ''

    const formatted = formatDictItem(item, useI18nTranslation)
    return formatted.label
}

/**
 * 根据value从字典对象中查找label（已国际化）
 * @param dictObject 字典对象
 * @param value 要查找的值
 * @param useI18nTranslation 是否使用国际化翻译
 * @returns 找到的label，找不到返回空字符串
 */
export function findDictLabelFromObject(dictObject: Record<string, DictItem>, value: string | number, useI18nTranslation: boolean = true): string {
    const item = dictObject[value]
    if (!item) return ''

    const formatted = formatDictItem(item, useI18nTranslation)
    return formatted.label
} 