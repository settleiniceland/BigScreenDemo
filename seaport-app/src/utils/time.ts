import dayjs from 'dayjs';

const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

/**
 * 将时间戳（毫秒）格式化为日期字符串
 * @param timestamp 时间戳，支持 number 或 string 类型
 * @returns 格式化后的日期字符串，如果无效则返回空字符串
 */
export const formatTimestampToDateTime = (timestamp: number | string, format: string = DATE_TIME_FORMAT): string => {
  if (!timestamp) return '';
  const date = dayjs(Number(timestamp));
  if (!date.isValid()) {
    console.warn('Invalid timestamp:', timestamp);
    return '';
  }
  return date.format(format);
};

/**
 * 将日期字符串转换为时间戳（毫秒）
 * @param time 日期字符串，格式应为 'YYYY-MM-DD HH:mm:ss'
 * @returns 对应的时间戳，如果输入无效则返回 null
 */
export const dateTimeToFormatTimestamp = (time: string): number | null => {
  const date = dayjs(time, DATE_TIME_FORMAT);
  if (!date.isValid()) {
    console.warn('Invalid date string:', time);
    return null;
  }
  return date.valueOf();
};
