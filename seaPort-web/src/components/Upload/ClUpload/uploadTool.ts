import { last } from "lodash-es";
// import { module } from "/@/cool";
import type { Upload } from "./types";

// 模块参数
// const { options } = module.get("upload");
export const options = {
    // 尺寸
    size: 120,
    // 限制
    limit: {
      // 上传最大数量
      upload: 9,
      // 上传大小限制
      size: 100
    },
    // 云端上传路径前缀
    prefixPath: "app/base",
    // 规则
    rules: [
      {
        name: "图片",
        type: "image",
        color: "#67C23A",
        exts: ["bmp", "jpg", "jpeg", "png", "tif", "gif", "svg", "webp"]
      },
      {
        name: "视频",
        type: "video",
        color: "#826aec",
        exts: [
          "avi",
          "wmv",
          "mpg",
          "mpeg",
          "mov",
          "rm",
          "ram",
          "swf",
          "flv",
          "mp4"
        ]
      },
      {
        name: "音频",
        type: "audio",
        color: "#826aec",
        exts: [
          "mp3",
          "wav",
          "wma",
          "mp2",
          "flac",
          "midi",
          "ra",
          "ape",
          "aac",
          "cda"
        ]
      },
      {
        name: "文档",
        type: "word",
        color: "#53B7F4",
        exts: ["doc", "dot", "wps", "wpt", "docx", "dotx", "docm", "dotm"]
      },
      {
        name: "表格",
        type: "excel",
        color: "#53D39C",
        exts: ["xls", "xlt", "et", "xlsx", "xltx", "xlsm", "xltm"]
      },
      {
        name: "演示",
        type: "ppt",
        color: "#F56C6C",
        exts: [
          "ppt",
          "pptx",
          "pptm",
          "ppsx",
          "ppsm",
          "pps",
          "potx",
          "potm",
          "dpt",
          "dps"
        ]
      },
      {
        name: "PDF",
        type: "pdf",
        exts: ["pdf"],
        color: "#8f3500"
      },
      {
        name: "压缩文件夹",
        type: "rar",
        color: "#FFC757",
        exts: ["rar", "zip"]
      },
      {
        name: "文件",
        type: "file",
        color: "#909399",
        exts: []
      }
    ]
  };
// 规则列表
const rules: Upload.Rule[] = options.rules || [];

// 文件大小
export function fileSize(size = 0): string {
  const num = 1024.0;

  if (size < num) return size + "B";
  if (size < Math.pow(num, 2)) return (size / num).toFixed(2) + "K";
  if (size < Math.pow(num, 3))
    return (size / Math.pow(num, 2)).toFixed(2) + "M";
  if (size < Math.pow(num, 4))
    return (size / Math.pow(num, 3)).toFixed(2) + "G";
  return (size / Math.pow(num, 4)).toFixed(2) + "T";
}

// 文件名
export function fileName(url: string) {
  return filename(url.substring(url.indexOf("_") + 1));
}

// 文件规则
export function fileRule(path?: string) {
  const d = rules.find(e => {
    return e.exts.find(a => a == extname(path || "").toLocaleLowerCase());
  });

  return (d || last(rules))!;
}

// 获取规则
export function getRule(type?: string) {
  return (rules.find(e => e.type == type) || last(rules))!;
}

// 获取类型
export function getType(path: string) {
  return fileRule(path).type;
}

// 拼接数组下的url
export function getUrls(list: any[]) {
  return list.map(e => e.url.replace(/,/g, encodeURIComponent(",")));
}

// 路径拼接
export function pathJoin(...parts: string[]): string {
  if (parts.length === 0) {
    return "";
  }

  const firstPart = parts[0];
  let isAbsolute = false;

  // 检查第一个部分是否以 "http" 开头，以确定路径类型（绝对还是相对）
  if (firstPart.startsWith("http")) {
    isAbsolute = true;
  }

  // 标准化路径，去除任何开头或结尾的斜杠
  const normalizedParts = parts.map(part => part.replace(/(^\/+|\/+$)/g, ""));

  if (isAbsolute) {
    // 如果是绝对路径，使用斜杠连接部分
    return normalizedParts.join("/");
  } else {
    // 如果是相对路径，使用平台特定的分隔符连接部分
    return normalizedParts.join("/");
  }
}

export function uuid(separator = "-"): string {
  const s: any[] = [];
  const hexDigits = "0123456789abcdef";
  for (let i = 0; i < 36; i++) {
    s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
  }
  s[14] = "4";
  s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
  s[8] = s[13] = s[18] = s[23] = separator;

  return s.join("");
}
// 路径名称
export function basename(path: string): string {
  let index = path.lastIndexOf("/");
  index = index > -1 ? index : path.lastIndexOf("\\");
  if (index < 0) {
    return path;
  }
  return path.substring(index + 1);
}
// 文件扩展名
export function extname(path: string): string {
  return path.substring(path.lastIndexOf(".") + 1).split(/(\?|&)/)[0];
}
// 文件名
export function filename(path: string): string {
  return basename(path.substring(0, path.lastIndexOf(".")));
}
// 是否Promise
export function isPromise(val: any) {
	return val && Object.prototype.toString.call(val) === "[object Promise]";
}
