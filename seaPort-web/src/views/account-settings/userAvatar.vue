<template>
  <div class="user-info-head" @click="editCropper()">
    <!-- <img :src="options.img" title="点击上传头像" class="img-circle img-lg" /> -->
    <SingleUploader />
  </div>
</template>

<script setup>
import { uploadAvatar } from "@/api/system/user";
import SingleUploader from "@/components/Upload/SingleUpload.vue";
import { useUserStoreHook } from "@/store/modules/user";
import "vue-cropper/dist/index.css";
import { getCurrentInstance, reactive, ref } from "vue";
const { proxy } = getCurrentInstance();

const open = ref(false);
const visible = ref(false);
const title = ref("修改头像");
const { avatar } = useUserStoreHook();

//图片裁剪数据
const options = reactive({
  img: import.meta.env.VITE_RESOURCE_API + useUserStoreHook()?.avatar, // 裁剪图片的地址
  autoCrop: true, // 是否默认生成截图框
  autoCropWidth: 200, // 默认生成截图框宽度
  autoCropHeight: 200, // 默认生成截图框高度
  fixedBox: true, // 固定截图框大小 不允许改变
  outputType: "png", // 默认生成截图为PNG格式
  filename: "avatar", // 文件名称
  previews: {} //预览数据
});

/** 编辑头像 */
function editCropper() {
  open.value = true;
}
/** 上传预处理 */
function beforeUpload(file) {
  if (file.type.indexOf("image/") == -1) {
    proxy.$modal.msgError(
      "文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。"
    );
  } else {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      options.img = reader.result;
      options.filename = file.name;
    };
  }
}

/** 上传图片 */
function uploadImg() {
  proxy.$refs.cropper.getCropBlob(data => {
    let formData = new FormData();
    formData.append("avatarfile", data, options.filename);
    uploadAvatar(formData).then(response => {
      open.value = false;
      options.img = import.meta.env.VITE_APP_BASE_API + response.imgUrl;
      userStore.avatar = options.img;
      proxy.$modal.msgSuccess("修改成功");
      visible.value = false;
    });
  });
}
</script>

<style lang="scss" scoped>
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
}

// .user-info-head:hover:after {
//   content: "+";
//   position: absolute;
//   left: 0;
//   right: 0;
//   top: 0;
//   bottom: 0;
//   color: #eee;
//   background: rgba(0, 0, 0, 0.5);
//   font-size: 24px;
//   font-style: normal;
//   -webkit-font-smoothing: antialiased;
//   -moz-osx-font-smoothing: grayscale;
//   cursor: pointer;
//   line-height: 110px;
//   border-radius: 50%;
// }
</style>
