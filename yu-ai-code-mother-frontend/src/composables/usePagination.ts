import { computed, reactive, type Ref } from 'vue'

/**
 * 通用分页逻辑
 */
export function usePagination<TParams extends PageParams>(
  searchParams: TParams,
  total: Ref<number>,
) {
  const pagination = computed(() => ({
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (count: number) => `共 ${count} 条`,
  }))

  const resetPage = () => {
    searchParams.pageNum = 1
  }

  const handleTableChange = (page: { current: number; pageSize: number }) => {
    searchParams.pageNum = page.current
    searchParams.pageSize = page.pageSize
  }

  return {
    pagination,
    resetPage,
    handleTableChange,
  }
}

interface PageParams {
  pageNum?: number
  pageSize?: number
}
