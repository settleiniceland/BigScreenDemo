<template>
  <div class="cl-upload-box">
    <!-- 文件区域 -->
    <!-- <el-scrollbar class="cl-upload-space-inner__file" v-loading="loading"  v-if="list.length > 0"> -->
      <template v-if="list.length > 0">
        <div
          class="cl-upload-space-inner__file-list"
          :class="{
            'is-mini': true
          }"
        >
          <div
            class="cl-upload-space-inner__file-item"
            v-for="item in list"
            :key="item.preload || item.url"
          >
            <upload-item :showTag="true" :disabled="disabled" :item="item" :list="list" @remove="remove"  />
          </div>
        </div>
      </template>
    <!-- </el-scrollbar> -->
    <cl-upload
      class="cl-upload-btn-box"
      ref="uploadRef"
      menu="space"
      type="file"
      multiple
      :show-file-list="false"
      :limit="9999"
      :accept="accept"
      small
      v-model="list"
      @change-list="onChangeList"
      @success="onSuccess"
      @upload="onUpload"
      v-if="!disabled"
    >
      <div class="upload-btn">
        <el-icon class="el-icon--upload" size="40">
          <upload-filled />
        </el-icon>
        <p>点击按钮上传</p>
      </div>
    </cl-upload>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import ClUpload from "./index.vue";
import { ElMessage, ElMessageBox } from "element-plus";
import UploadItem from "./UploadItem.vue";
import { UploadFilled } from "@element-plus/icons-vue";

const props = defineProps({
  // 绑定值，单选时字符串，多选时字符串数组
	modelValue: {
		type: [Object, Array],
		default: () => []
	},
	limit: {
		type: Number,
		default: 99
	},
	accept: String,
  selectable: Boolean,
   disabled: {
    type: Boolean,
    default: false
  }
});


// emit事件
const emit = defineEmits(["onSuccess", "upload", "update:modelValue", "onRemove"]);

const uploadRef = ref(null);
// 是否加载中
const loading = ref(false);

// 文件列表
const list = ref<any[]>([]);

// 上传成功
function onSuccess<T extends { id: number }>(data: T) {
	// emit("update:modelValue", data);
  emit("onSuccess", data);
}

// 上传失败
// function onError(err: any) {

// }
// 上传列表有变化
const onChangeList = (data: any) => {
  emit("update:modelValue", data);
};
// 上传时
function onUpload(data: any) {
  list.value.push(data);
  console.log(data, list.value);
  // debugger

}

// 删除选中
function remove(item?: any) {
	const ids = item ? [item.fileId] : [];

	ElMessageBox.confirm("此操作将删除文件, 是否继续?", "提示", {
		type: "warning"
	})
		.then(() => {
			ElMessage.success("删除成功");

			// 删除文件及选择
			ids.forEach((fileId) => {
				[list.value].forEach((list) => {
          const index = list.findIndex((e) => e.fileId === fileId);
					list.splice(index, 1);
          uploadRef.value.remove(index);
				});
      });
      // 更新绑定值
      emit("update:modelValue", list.value);
      emit("onRemove", list.value);
		})
		.catch(() => null);
}

const setFileList = (fileList: any) => {
  try {
    fileList = JSON.parse(JSON.stringify(fileList));
    list.value = fileList;
  } catch (error) {
    // fileList = []
  }
}

defineExpose({
	open,
  close,
  setFileList
});
</script>

<style lang="scss" scoped>
.cl-upload-box {
  width: 100%;
}
.cl-upload-space-inner__file {
  min-height: 120px;
  // padding: 0 10px;
  box-sizing: border-box;
  position: relative;

  &-item {
    height: 0;
    min-height: 100px;
    min-width: 100px;
    width: calc(12.5% - 10px);
    padding-top: calc(12.5% - 10px);
    margin: 0 10px 10px 0;
    position: relative;
    box-sizing: border-box;
  }

  &-index {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    width: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    border-radius: 5px;
    pointer-events: none;

    span {
      position: absolute;
      right: 5px;
      top: 5px;
      background-color: var(--el-color-success);
      color: #fff;
      display: inline-block;
      height: 20px;
      width: 20px;
      text-align: center;
      line-height: 20px;
      border-radius: 20px;
      font-size: 14px;
    }
  }

  &-list {
    display: flex;
    flex-wrap: wrap;

    &.is-mini {
      .cl-upload-space-inner__file-item {
        width: calc(50% - 10px);
      }
    }
  }


}

.upload-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    // position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    margin-top: 10px;
    // margin: auto;
    height: 100px;
    width: 100%;
    padding: 0 10px;
    border-radius: 6px;
    border: 1px dashed var(--el-border-color);
    box-sizing: border-box;
    cursor: pointer;

    &:hover {
      border-color: var(--color-primary);
    }

    i {
      font-size: 67px;
      color: #c0c4cc;
    }

    p {
      font-size: 14px;
      // margin-top: 15px;
      color: #999;
    }
  }
.cl-upload-btn-box {
  :deep(.cl-upload__file-btn) {
    width: 100%;
  }
}
</style>
