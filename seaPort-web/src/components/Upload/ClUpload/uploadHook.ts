import { ElMessage } from "element-plus";
import { pathJoin, uuid, options } from "./uploadTool";
import { type AxiosProgressEvent } from "axios";
import type { Upload } from "./types";
import { merge } from "lodash-es";
import { getToken } from "@/utils/auth";
import { http } from "@/utils/http";
const baseUrl = import.meta.env.VITE_BASE_API;
export function useUpload() {

  // 上传
  async function toUpload(
    file: File,
    opts: Upload.Options = {}
  ): Upload.Respose {
    return new Promise((resolve, reject) => {
      const executor = async () => {
        // 合并配置
        const { prefixPath, onProgress } = merge(options, opts);

        // 文件id
        const fileId = uuid("");

        try {
          // 上传模式、类型

          // 本地上传
          const isLocal = "local";

          // 文件名
          const fileName = fileId + "_" + file.name;

          // Key
          let key = isLocal ? fileName : pathJoin(prefixPath!, fileName);

          // 多种上传请求
          const next = async ({ host, preview, data }: Upload.Request) => {
            const fd = new FormData();

            // key
            fd.append("key", key);

            // 签名数据
            for (const i in data) {
              if (!fd.has(i)) {
                fd.append(i, data[i]);
              }
            }

            // 文件
            fd.append("file", file, file.name);

            // 上传进度
            let progress = 0;

            // 上传
            await http
              .request(
                "post",
                host,
                {},
                {
                  headers: {
                  "Content-Type": "multipart/form-data",
                  Authorization: isLocal ? getToken().access_token : null
                  },
                  timeout: 600000,
                  data: fd,
                  onUploadProgress(e: AxiosProgressEvent) {
                    progress = e.total
                      ? Math.floor((e.loaded / e.total) * 100)
                      : 0;
                    onProgress?.(progress);
                  }
                }
              )
              .then((res: any) => {
                if (progress != 100) {
                  onProgress?.(100);
                }

                key = encodeURIComponent(key);

                let url = "";
                let name = file.name;
                if (isLocal) {
                  url = res.data?.url;
                  name = res.data?.name;
                } else {
                  url = pathJoin(preview || host, key);
                }

                resolve({
                  ...res.data,
                  key,
                  url,
                  name,
                  fileId,
                });
              })
              .catch(err => {
                ElMessage.error(err.message);
                reject(err);
              });
          };

          if (isLocal) {
            next({
              host: baseUrl + "/file/upload"
            });
          }
        } catch (err) {
          ElMessage.error("文件上传失败");
          console.error("[upload]", err);
          reject(err);
        }
      };

      executor();
    });
  }

  return {
    options,
    toUpload
  };
}
