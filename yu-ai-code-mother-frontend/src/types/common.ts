export interface PageParams {
  pageNum?: number
  pageSize?: number
}

export interface PaginationState {
  current: number
  pageSize: number
  total: number
}
