<template>
  <!-- 上传组件 -->
  <el-upload
    v-model="img"
    class="single-uploader"
    :show-file-list="false"
    list-type="picture-card"
    :before-upload="handleBeforeUpload"
    :http-request="uploadFile"
  >
    <img v-if="img" :src="img" class="single" />
    <el-icon v-else class="single-uploader-icon"><i-ep-plus /></el-icon>
  </el-upload>
</template>

<script setup lang="ts">
import { uploadAvatar } from "@/api/system/user";
import { useUserStoreHook } from "@/store/modules/user";
import { ElMessage, UploadRawFile, UploadRequestOptions } from "element-plus";
import { ref, watch } from "vue";

const img = ref(
  import.meta.env.VITE_RESOURCE_API + useUserStoreHook().$state.avatar
);

watch(
  () => useUserStoreHook().$state.avatar,
  newAvatar => {
    img.value = import.meta.env.VITE_RESOURCE_API + newAvatar;
  }
);

/**
 * 自定义图片上传
 *
 * @param options
 */
async function uploadFile(options: UploadRequestOptions): Promise<any> {
  const { msg } = (await uploadAvatar(options.file)) as any;
  useUserStoreHook().SET_AVATAR(msg);
  ElMessage.success("修改成功");

  // 更新响应式数据
  img.value = import.meta.env.VITE_RESOURCE_API + msg;

  // 更新本地存储路径
  const userInfo = JSON.parse(localStorage.getItem("user-info") || "{}");
  userInfo.avatar = msg;
  localStorage.setItem("user-info", JSON.stringify(userInfo));

  console.log(img.value, "更新后的");
}

/**
 * 限制用户上传文件的格式和大小
 */
function handleBeforeUpload(file: UploadRawFile) {
  let type = file.type;
  if (type.indexOf("image") < 0) {
    ElMessage.warning("请上传图片");
    return false;
  }

  if (file.size > 2 * 1048 * 1048) {
    ElMessage.warning("上传图片不能大于2M");
    return false;
  }
  return true;
}
</script>

<style scoped lang="scss">
:deep(.el-upload) {
  width: 110px;
  height: 110px;
  border-radius: 50%;
}

.single-uploader .single {
  display: block;
  width: 110px;
  height: 110px;
  border-radius: 50%;
}

.single-uploader .el-upload {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  transition: var(--el-transition-duration-fast);
}

.single-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.single-uploader-icon {
  width: 80px;
  height: 80px;
  font-size: 28px;
  color: #8c939d;
  text-align: center;
}
</style>
