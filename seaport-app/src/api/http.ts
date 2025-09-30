import axios from 'axios'
import { getFullURL } from '@/utils/http'
import { useUserStore } from '@/store'

const instance = axios.create({
  // Web 侧可以通过 vite.config.js 中的 proxy 配置，指定代理
  // 小程序APP里需写完整路径，如 https://service-rbji0bev-1256505457.cd.apigw.tencentcs.com/release
  // 可使用条件编译,详见 https://uniapp.dcloud.io/tutorial/platform.html#preprocessor
  // #ifdef H5
  baseURL: import.meta.env.VITE_APP_AXIOS_BASE_URL,
  // #endif
  // #ifndef H5
  // @ts-ignore
  // baseURL:'http://10.40.11.52:8080'
  // baseURL: 'http://10.40.11.4:8080',
  // 测试环境
  // baseURL: 'http://192.168.91.50:38080',
  // 正式环境
  baseURL: import.meta.env.VITE_APP_AXIOS_BASE_URL,
  // #endif
  adapter(config) {
    const { url, method, data, params, headers, baseURL, paramsSerializer } =
      config
    return new Promise((resolve, reject) => {
      uni.request({
        method: method!.toUpperCase() as any,
        url: getFullURL(baseURL || '', url!, params, paramsSerializer),
        header: {
          ...headers,
          OS: 'APP'
        },
        data,
        dataType: 'json',
        responseType: config.responseType,
        success: (res: any) => {
          resolve(res)
        },
        fail: (err: any) => {
          reject(err)
        }
      })
    })
  }
})

/**
 * 请求拦截
 */
instance.interceptors.request.use((config) => {
  const { method, params } = config
  // 附带鉴权的token
  const headers: any = {
    Authorization: `Bearer ${uni.getStorageSync('token')}`
  }
  // 不缓存get请求
  if (method === 'get') {
    headers['Cache-Control'] = 'no-cache'
  }
  // delete请求参数放入body中
  if (method === 'delete') {
    headers['Content-type'] = 'application/json;'
    Object.assign(config, {
      data: params,
      params: {}
    })
  }

  return {
    ...config,
    headers
  }
})

/**
 * 响应拦截
 */
instance.interceptors.response.use((v) => {
  const { data } = v
  if (data?.code === 401) {
    uni.removeStorageSync('token')
    uni.showToast({
      title: '登录已过期，请重新登录',
      icon: 'none'
    })
    const userStore = useUserStore()
    userStore.setToken('')
    uni.reLaunch({
      url: '/pages/login/login'
    })
    return v.data
  }

  // @ts-ignore
  if ((v.status || v.statusCode) === 200) {
    if (v.data.code !== 200) {
      return Promise.reject(data)
    }
    return data
  }
  return Promise.reject(data)

  // alert(v.statusText, '网络错误')
  return Promise.reject(data)
})
export default instance
