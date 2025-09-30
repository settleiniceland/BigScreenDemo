import Axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type CustomParamsSerializer
} from "axios";
import type {
  PureHttpError,
  RequestMethods,
  PureHttpResponse,
  PureHttpRequestConfig
} from "./types.d";
import { stringify } from "qs";
import NProgress from "../progress";
import { getToken, formatToken } from "@/utils/auth";
import { useUserStoreHook } from "@/store/modules/user";
import {
  ElMessage,
  ElMessageBox,
  ElNotification,
  ElLoading
} from "element-plus";
import { saveAs } from "file-saver";
import { blobValidate, tansParams } from "../ruoyi";

let downloadLoadingInstance: ReturnType<typeof ElLoading.service>;
const baseUrl = import.meta.env.VITE_BASE_API;

// 相关配置请参考：www.axios-js.com/zh-cn/docs/#axios-request-config-1
const defaultConfig: AxiosRequestConfig = {
  // 请求超时时间
  timeout: 18000,
  baseURL: baseUrl,
  headers: {
    Accept: "application/json, text/plain, */*",
    "Content-Type": "application/json",
    "X-Requested-With": "XMLHttpRequest",
    OS: "PC"
  },
  // 数组格式参数序列化（https://github.com/axios/axios/issues/5142）
  paramsSerializer: {
    serialize: stringify as unknown as CustomParamsSerializer
  }
};

const errorCode = {
  "401": "认证失败，无法访问系统资源",
  "403": "当前操作没有权限",
  "404": "访问资源不存在",
  default: "系统未知错误，请反馈给管理员"
} as Record<string, string>;

class PureHttp {
  constructor() {
    this.httpInterceptorsRequest();
    this.httpInterceptorsResponse();
  }

  /** `token`过期后，暂存待执行的请求 */
  private static requests = [];

  /** 防止重复刷新`token` */
  private static isRefreshing = false;

  /** 初始化配置对象 */
  private static initConfig: PureHttpRequestConfig = {};

  /** 保存当前`Axios`实例对象 */
  private static axiosInstance: AxiosInstance = Axios.create(defaultConfig);

  /** 重连原始请求 */
  private static retryOriginalRequest(config: PureHttpRequestConfig) {
    return new Promise(resolve => {
      PureHttp.requests.push((token: string) => {
        config.headers["Authorization"] = formatToken(token);
        resolve(config);
      });
    });
  }

  /** 请求拦截 */
  private httpInterceptorsRequest(): void {
    PureHttp.axiosInstance.interceptors.request.use(
      async (config: PureHttpRequestConfig): Promise<any> => {
        // 开启进度条动画
        NProgress.start();
        // 优先判断post/get等方法是否传入回调，否则执行初始化设置等回调
        if (typeof config.beforeRequestCallback === "function") {
          config.beforeRequestCallback(config);
          return config;
        }
        if (PureHttp.initConfig.beforeRequestCallback) {
          PureHttp.initConfig.beforeRequestCallback(config);
          return config;
        }
        /** 请求白名单，放置一些不需要`token`的接口（通过设置请求白名单，防止`token`过期后再请求造成的死循环问题） */
        const whiteList = ["/refresh-token", "/login"];
        return whiteList.some(url => config.url.endsWith(url))
          ? config
          : new Promise(resolve => {
              const data = getToken();
              if (data) {
                const now = new Date().getTime();
                const expired = parseInt(data.expires) - now <= 0;
                if (expired) {
                  if (!PureHttp.isRefreshing) {
                    PureHttp.isRefreshing = true;
                    // token过期刷新
                    useUserStoreHook()
                      .handRefreshToken({ refreshToken: data.refreshToken })
                      .then(res => {
                        const token = res.data.accessToken;
                        config.headers["Authorization"] = formatToken(token);
                        PureHttp.requests.forEach(cb => cb(token));
                        PureHttp.requests = [];
                      })
                      .finally(() => {
                        PureHttp.isRefreshing = false;
                      });
                  }
                  resolve(PureHttp.retryOriginalRequest(config));
                } else {
                  config.headers["Authorization"] = formatToken(
                    data.accessToken
                  );
                  resolve(config);
                }
              } else {
                resolve(config);
              }
            });
      },
      error => {
        return Promise.reject(error);
      }
    );
  }

  /** 响应拦截 */
  /** 响应拦截 */
  private httpInterceptorsResponse(): void {
    const instance = PureHttp.axiosInstance;
    instance.interceptors.response.use(
      (response: PureHttpResponse) => {
        const $config = response.config;
        // 关闭进度条动画
        NProgress.done();
        // 优先判断post/get等方法是否传入回调，否则执行初始化设置等回调
        if (typeof $config.beforeResponseCallback === "function") {
          $config.beforeResponseCallback(response);
          return response.data;
        }
        if (PureHttp.initConfig.beforeResponseCallback) {
          PureHttp.initConfig.beforeResponseCallback(response);
          return response.data;
        }

        // 未设置状态码则默认成功状态
        const code = response.data.code || 200;
        // 获取错误信息
        const msg =
          errorCode[code] || response.data.msg || errorCode["default"];
        // 二进制数据则直接返回
        if (
          response.request.responseType === "blob" ||
          response.request.responseType === "arraybuffer"
        ) {
          return response.data;
        }
        if (code === 401) {
          // if (!isRelogin.show) {
          //   isRelogin.show = true;
          ElMessageBox.confirm(
            "登录状态已过期，您可以继续留在该页面，或者重新登录",
            "系统提示",
            {
              confirmButtonText: "重新登录",
              cancelButtonText: "取消",
              type: "warning"
            }
          ).then(() => {
            useUserStoreHook().logOut();
          });
          // }
          // return Promise.reject('无效的会话，或者会话已过期，请重新登录。');
        } else if (code === 500) {
          ElMessage({
            message: msg,
            type: "error"
          });
          return Promise.reject(new Error(msg));
        } else if (code === 601) {
          ElMessage({
            message: msg,
            type: "warning"
          });
          return Promise.reject(new Error(msg));
        } else if (code !== 200) {
          ElNotification.error({
            title: msg
          });
          return Promise.reject("error");
        } else {
          return Promise.resolve(response.data);
        }

        return response.data;
      },
      (error: PureHttpError) => {
        const $error = error;
        $error.isCancelRequest = Axios.isCancel($error);
        // 关闭进度条动画
        NProgress.done();
        // 所有的响应异常 区分来源为取消请求/非取消请求
        return Promise.reject($error);
      }
    );
  }

  /** 通用请求工具函数 */
  public request<T>(
    method: RequestMethods,
    url: string,
    param?: AxiosRequestConfig,
    axiosConfig?: PureHttpRequestConfig
  ): Promise<T> {
    const config = {
      method,
      url,
      ...param,
      ...axiosConfig
    } as PureHttpRequestConfig;

    // 单独处理自定义请求/响应回调
    return new Promise((resolve, reject) => {
      PureHttp.axiosInstance
        .request(config)
        .then((response: undefined) => {
          resolve(response);
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  /** 单独抽离的`post`工具函数 */
  public post<T, P>(
    url: string,
    params?: AxiosRequestConfig<P>,
    config?: PureHttpRequestConfig
  ): Promise<T> {
    return this.request<T>("post", url, params, config);
  }

  /** 单独抽离的`get`工具函数 */
  public get<T, P>(
    url: string,
    params?: AxiosRequestConfig<P>,
    config?: PureHttpRequestConfig
  ): Promise<T> {
    return this.request<T>("get", url, params, config);
  }

  public download = (
    url: string,
    params: any,
    filename: string,
    config: any = {}
  ) => {
    downloadLoadingInstance = ElLoading.service({
      text: "正在下载数据，请稍候",
      background: "rgba(0, 0, 0, 0.7)"
    });
    return this.post(url, params, {
      transformRequest: [
        p => {
          return tansParams(params);
        }
      ],
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      responseType: "blob",
      ...config
    })
      .then(async (data: any) => {
        const isBlob = blobValidate(data);
        if (isBlob) {
          const blob = new Blob([data]);
          saveAs(blob, filename);
        } else {
          const resText = await data.text();
          const rspObj = JSON.parse(resText);
          const errMsg =
            errorCode[rspObj.code] || rspObj.msg || errorCode["default"];
          ElMessage.error(errMsg);
        }
        downloadLoadingInstance.close();
      })
      .catch(r => {
        console.error(r);
        ElMessage.error("下载文件出现错误，请联系管理员！");
        downloadLoadingInstance.close();
      });
  };
}

export const http = new PureHttp();
