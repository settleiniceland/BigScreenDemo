<template>
  <div class="cl-upload-item__wrap">
    <keep-alive>
      <div
        class="cl-upload-item"
        :class="[
          {
            'is-play': item.isPlay
          }
        ]"
      >
        <!-- 图片 -->
        <template v-if="item.type === 'image' && !item.error">
          <el-image
            class="cl-upload-item__image-cover"
            fit="contain"
            :src="item.preload || viteResourceApi + url"
            @error="item.error = '加载失败'"
          />
        </template>

        <!-- 视频 -->
        <template v-else-if="item.type === 'video'">
          <video
            ref="videoRef"
            :key="item.url"
            :src="viteResourceApi + item.url"
          />
        </template>

        <!-- 其他 -->
        <template v-else>
          <!-- 图标 -->
          <div class="cl-upload-item__icon">
            <!-- <cl-svg :name="item.type" /> -->
          </div>
          <!-- 文件名 -->
          <div class="cl-upload-item__name">
            <span>{{ fileName(item.name || url) }}</span>
            <span v-show="item.error" class="error">{{ item.error }}</span>
          </div>
        </template>

        <!-- 音频 -->
        <template v-if="item.type === 'audio'">
          <audio ref="audioRef" controls>
            <source
              :key="item.url"
              :src="viteResourceApi + item.url"
              type="audio/mpeg"
            />
          </audio>
        </template>

        <!-- 上传中 -->
        <div
          class="cl-upload-item__progress"
          :class="{
            'is-show': item.progress! >= 0 && item.progress! < 100,
            'is-hide':
              (item.progress == 100 && !item.uploading) || item.compressing
          }"
        >
          <div class="cl-upload-item__progress-bar">
            <el-progress :percentage="item.progress" :show-text="false" />
          </div>

          <span class="cl-upload-item__progress-value">{{
            item.progress
          }}</span>
          <span class="cl-upload-item__progress-text">上传中...</span>
        </div>

        <!-- 压缩中 -->
        <div
          class="cl-upload-item__progress compress"
          :class="{
            'is-show': item.compressing,
            'is-hide': !item.compressing
          }"
        >
          <!-- 压缩进度条 -->
          <!-- <div class="cl-upload-item__progress-bar">
						<el-progress :percentage="item.compressionProgress" :show-text="false" />
					</div> -->
          <!-- 压缩进度值 -->
          <span class="cl-upload-item__progress-value">{{
            item.compressionProgress
          }}</span>
          <!-- 压缩状态 -->
          <span class="cl-upload-item__compressing-text">压缩中...</span>
        </div>

        <!-- 角标 -->
        <span
          v-if="showTag"
          class="cl-upload-item__tag"
          :style="{
            backgroundColor: tag.color
          }"
        >
          {{ tag.name }}
        </span>

        <template v-if="url">
          <!-- 工具 -->
          <div class="cl-upload-item__actions">
            <template v-if="media.isMedia">
              <el-icon
                v-if="item.isPlay"
                class="action-pause"
                @click.stop="media.pause()"
              >
                <video-pause />
              </el-icon>

              <el-icon v-else class="action-play" @click.stop="media.play()">
                <video-play />
              </el-icon>
            </template>

            <template v-else>
              <el-icon class="action-preview" @click.stop="preview">
                <zoom-in />
              </el-icon>
            </template>
            <el-icon class="action-download" @click.stop="preview('download')">
              <Download />
            </el-icon>
            <el-icon
              v-if="!disabled"
              class="action-delete"
              @click.stop="remove"
            >
              <delete />
            </el-icon>
          </div>
        </template>
        <!-- 错误移除按钮 -->
        <div v-if="item.error" class="error-remove-btn">
          <el-icon class="cl-upload-item__icon" @click="remove">
            <delete />
          </el-icon>
        </div>
      </div>
    </keep-alive>

    <!-- 预览 -->
    <viewer ref="viewerRef" />
  </div>
</template>

<script lang="ts" name="cl-upload-item" setup>
import { computed, type PropType, onMounted, watch, reactive, ref } from "vue";
import {
  ZoomIn,
  Delete,
  VideoPause,
  VideoPlay,
  Download
} from "@element-plus/icons-vue";
import { fileName, getRule, extname, getType } from "./uploadTool";
import Viewer from "./viewer.vue";
import type { Upload } from "./types";
const viteResourceApi = import.meta.env.VITE_RESOURCE_API;

const props = withDefaults(
  defineProps<{
    item: any;
    list: Upload.Item[];
    disabled: boolean;
    // deletable: boolean;
    showTag: boolean;
  }>(),
  {
    disabled: false,
    // deletable: true,
    showTag: true
  }
);
// const props = defineProps({
// 	item: {
// 		type: Object as PropType<Upload.Item>,
//     required: true,
// 	},
// 	list: {
// 		type: Array as PropType<Upload.Item[]>,
// 		default: () => []
// 	},
// 	// 是否禁用
// 	disabled: Boolean,
// 	// 是否可以删除
// 	deletable: Boolean,
// 	// 显示角标
// 	showTag: {
// 		type: Boolean,
// 		default: true
// 	}
// } as const);
// const props = defineProps<{
//   item: Upload.Item
//   list: Upload.Item[]
//   disabled: boolean
//   deletable: boolean
//   showTag: boolean
// }>();
const emit = defineEmits(["remove"]);

const viewerRef = ref(null);

// 图片地址
const url = computed(() => props.item.url || "");

// 角标
const tag = computed(() => {
  const d = getRule(props.item.type);
  if (!props.item.type) {
    props.item.type = getType(props.item.url);
  }
  return {
    color: d.color,
    name: extname(props.item.name || url.value)
  };
});

// 移除
function remove() {
  emit("remove", props.item);
}

// 预览
function preview(type?: string) {
  viewerRef.value.open(props.item, type);
}

const audioRef = ref(null);
const videoRef = ref(null);

// 媒体
const media = reactive({
  isMedia: ["video", "audio"].includes(props.item.type!),

  play() {
    props.list.forEach(e => {
      e.isPlay = e.uid ? props.item.uid == e.uid : props.item.id == e.id;
    });
  },

  pause() {
    props.item.isPlay = false;
  },

  create() {
    if (!media.isMedia) {
      return false;
    }

    // 媒体元素
    let el: HTMLVideoElement | HTMLAudioElement | undefined;

    // 监听播放\暂停
    watch(
      () => props.item.isPlay,
      val => {
        if (!el) {
          // el = refs[props.item.type!];
          el = props.item.type === "video" ? videoRef.value : audioRef.value;

          // 监听播放完成
          el?.addEventListener("ended", () => {
            media.pause();
          });
        }

        if (val) {
          el?.play();
        } else {
          el?.pause();
        }
      }
    );
  }
});

onMounted(() => {
  media.create();
});
</script>

<style lang="scss" scoped>
.cl-upload-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  cursor: pointer;
  border-radius: 6px;
  overflow: hidden;
  background-color: var(--el-fill-color-light);
  border: 1px solid var(--el-fill-color-light);
  box-sizing: border-box;
  position: relative;
  .error-remove-btn {
    position: absolute;
    top: 3px;
    right: 6px;
    z-index: 10;
    color: orange;
    border: 1px solid orange;
    border-radius: 50%;
    height: 22px;
    width: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  audio {
    visibility: hidden;
    height: 0;
  }

  video {
    height: 100%;
  }

  &__wrap {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    width: 100%;
  }

  &__icon {
    .cl-svg {
      font-size: 100px;
      position: absolute;
      right: -24px;
      top: -24px;
      fill: var(--el-fill-color-dark);
    }
  }

  &__name {
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: 50px;
    width: 100%;
    font-size: 12px;
    overflow: hidden;
    padding: 0 10px;
    position: absolute;
    bottom: 0;
    left: 0;
    box-sizing: border-box;
    line-height: 1;

    span {
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;

      &.error {
        color: var(--el-color-danger);
        margin-top: 5px;
        z-index: 10;
        background: #fef0f0;
      }
    }
  }

  &__progress {
    display: flex;
    align-items: center;
    justify-content: center;
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    width: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    pointer-events: none;
    transition: opacity 0.3s;
    opacity: 0;
    &.compress {
      .cl-upload-item__progress-value {
        color: orange;
        font-size: 20px;
      }
      .cl-upload-item__compressing-text {
        color: orange;
        bottom: 0px;
        position: absolute;
        font-weight: 700;
      }
    }
    &-bar {
      position: absolute;
      bottom: 10px;
      left: 10px;
      width: calc(100% - 20px);
    }

    &-value {
      position: absolute;
      font-size: 26px;
      color: #fff;
      margin-bottom: 15px;
      &::after {
        content: "%";
        margin-left: 2px;
      }
    }
    &-text {
      position: absolute;
      bottom: 10px;
      color: #fff;
    }
    &.is-show {
      opacity: 1;
    }

    &.is-hide {
      opacity: 0;
    }
  }

  &__tag {
    position: absolute;
    top: 5px;
    left: 5px;
    color: #fff;
    font-size: 12px;
    padding: 2px 4px;
    border-radius: 2px;
    text-transform: uppercase;
    line-height: 1;
    max-width: 65px;
    box-sizing: border-box;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__actions {
    position: absolute;
    left: 0;
    top: 0;
    z-index: 9;
    height: 100%;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, 0.5);
    border-radius: 6px;
    opacity: 0;
    transition: opacity 0.3s cubic-bezier(0.55, 0, 0.1, 1);

    .el-icon {
      color: #fff;
      margin: 0 8px;
      font-size: 20px;

      &:hover {
        color: #eee;
      }
    }
  }

  &.is-play {
    animation: play 1s linear infinite;
  }

  &:hover {
    .cl-upload-item__actions {
      opacity: 1;
    }
  }
}

@keyframes play {
  0% {
    border-color: var(--el-color-primary);
  }
  100% {
    border-color: var(--el-fill-color-light);
  }
}
</style>
