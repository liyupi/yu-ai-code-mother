import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { usePagination } from './usePagination'

interface UseTablePageOptions<TRecord, TParams extends Record<string, unknown>> {
  defaultParams: TParams
  fetchFn: (params: TParams) => Promise<{
    records?: TRecord[]
    totalRow?: number
  } | null | undefined>
  errorMessage?: string
}

/**
 * 管理后台表格页面的通用逻辑：搜索、分页、数据加载
 */
export function useTablePage<TRecord, TParams extends Record<string, unknown>>(
  options: UseTablePageOptions<TRecord, TParams>,
) {
  const { defaultParams, fetchFn, errorMessage = '获取数据失败' } = options

  const data = ref<TRecord[]>([])
  const total = ref(0)
  const loading = ref(false)
  const searchParams = reactive({ ...defaultParams }) as TParams

  const { pagination, resetPage, handleTableChange } = usePagination(searchParams, total)

  const fetchData = async () => {
    loading.value = true
    try {
      const result = await fetchFn(searchParams)
      if (result) {
        data.value = result.records ?? []
        total.value = result.totalRow ?? 0
      } else {
        message.error(errorMessage)
      }
    } catch (error) {
      console.error(errorMessage, error)
      message.error(errorMessage)
    } finally {
      loading.value = false
    }
  }

  const doSearch = () => {
    resetPage()
    fetchData()
  }

  const onTableChange = (page: { current: number; pageSize: number }) => {
    handleTableChange(page)
    fetchData()
  }

  onMounted(fetchData)

  return {
    data,
    total,
    loading,
    searchParams,
    pagination,
    fetchData,
    doSearch,
    onTableChange,
  }
}
