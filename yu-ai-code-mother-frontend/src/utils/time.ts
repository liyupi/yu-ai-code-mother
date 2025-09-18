import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

/**
 * 格式化时间
 * @param time 时间字符串
 * @param format 格式化字符串，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的时间字符串，如果时间为空则返回空字符串
 */
export const formatTime = (time: string | undefined, format = 'YYYY-MM-DD HH:mm:ss'): string => {
  if (!time) return ''
  return dayjs(time).format(format)
}

/**
 * 格式化时间为相对时间
 * @param time 时间字符串
 * @returns 相对时间字符串，如 "2小时前"
 */
export const formatRelativeTime = (time: string | undefined): string => {
  if (!time) return ''
  return dayjs(time).fromNow()
}

/**
 * 格式化时间为日期
 * @param time 时间字符串
 * @returns 日期字符串，如 "2024-01-01"
 */
export const formatDate = (time: string | undefined): string => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD')
}
