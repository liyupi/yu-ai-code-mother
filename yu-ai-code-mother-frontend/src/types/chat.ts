export interface ChatMessage {
  type: 'user' | 'ai'
  content: string
  loading?: boolean
  createTime?: string
}
