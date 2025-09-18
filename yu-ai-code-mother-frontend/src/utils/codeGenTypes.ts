/**
 * 代码生成类型枚举
 */
export enum CodeGenTypeEnum {
  HTML = 'html',
  MULTI_FILE = 'multi_file',
}

/**
 * 代码生成类型配置
 */
export const CODE_GEN_TYPE_CONFIG = {
  [CodeGenTypeEnum.HTML]: {
    label: '原生 HTML 模式',
    value: CodeGenTypeEnum.HTML,
  },
  [CodeGenTypeEnum.MULTI_FILE]: {
    label: '原生多文件模式',
    value: CodeGenTypeEnum.MULTI_FILE,
  },
} as const

/**
 * 代码生成类型选项（用于下拉选择）
 */
export const CODE_GEN_TYPE_OPTIONS = Object.values(CODE_GEN_TYPE_CONFIG).map((config) => ({
  label: config.label,
  value: config.value,
}))

/**
 * 格式化代码生成类型
 * @param type 代码生成类型
 * @returns 格式化后的类型描述
 */
export const formatCodeGenType = (type: string | undefined): string => {
  if (!type) return '未知类型'

  const config = CODE_GEN_TYPE_CONFIG[type as CodeGenTypeEnum]
  return config ? config.label : type
}

/**
 * 获取所有代码生成类型
 */
export const getAllCodeGenTypes = () => {
  return Object.values(CodeGenTypeEnum)
}

/**
 * 检查是否为有效的代码生成类型
 * @param type 待检查的类型
 */
export const isValidCodeGenType = (type: string): type is CodeGenTypeEnum => {
  return Object.values(CodeGenTypeEnum).includes(type as CodeGenTypeEnum)
}
