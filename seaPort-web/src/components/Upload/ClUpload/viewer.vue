<template>
	<div class="viewer-image">
		<!-- 图片 -->
		<el-image-viewer
			v-if="img.visible"
			:url-list="[viteResourceApi + img.url]"
			infinite
			teleported
			@close="close"
		/>
	</div>

	<!-- 文档 -->
</template>

<script lang="ts" setup name="file-viewer">
import { reactive, nextTick } from "vue";
import { getType } from "./uploadTool";
import type { Upload } from "./types";
import { useFilePreview } from '@/utils/previewFile';
const previewFile = useFilePreview().previewFile;
const viteResourceApi = import.meta.env.VITE_RESOURCE_API
// 图片预览
const img = reactive({
	visible: false,
	url: ""
});

// 文档预览
const doc = reactive({
	visible: false,
	loading: false,
	url: ""
});
 const downloadFileFromStream = async (url, fileName) => {
    try {
      // 使用 fetch 获取文件流
      const response = await fetch(import.meta.env.VITE_RESOURCE_API + url, {
        method: 'GET',
      });

      // 检查响应状态
      if (!response.ok) {
        throw new Error(`Failed to download file: ${response.statusText}`);
      }

      // 获取文件数据为 Blob
      const blob = await response.blob();

      // 创建一个本地 URL 对象
      const blobUrl = URL.createObjectURL(blob);

      // 创建一个隐藏的 <a> 元素
      const a = document.createElement('a');
      a.href = blobUrl; // 设置文件的下载链接
      a.download = fileName; // 设置下载的文件名
      document.body.appendChild(a); // 添加到 DOM
      a.click(); // 触发点击事件
      document.body.removeChild(a); // 移除 <a> 元素

      // 释放 Blob URL
      URL.revokeObjectURL(blobUrl);
    } catch (error) {
      console.error('Error downloading file:', error);
    }
  };
// 打开
function open(item: Upload.Item, handleType?: string) {
	
	if (item?.type || item?.url) {
		// 链接
		const url = item.url || "";
		if (handleType == "download") {
			downloadFileFromStream(url, item.name || item.url);
			return;
		}
		// 类型
		const type = getType(url);
		
		// 图片预览
		if (type == "image") {
			img.visible = true;
			img.url = url;

			return true;
		}

		// 文档预览
		if (["word", "excel", "ppt", "pdf"].includes(type)) {

      previewFile(viteResourceApi + item.url, item.name || item.url);
			return true;
		}

		window.open(viteResourceApi + item.url);
	}
}

// 关闭
function close() {
	img.visible = false;
}

defineExpose({
	open
});
</script>

<style lang="scss" scoped>
.viewer-image {
	position: absolute;
}

.viewer-doc {
	height: 100%;
	width: 100%;

	iframe {
		border: 0;
		height: 100%;
		width: 100%;
	}
}
</style>
