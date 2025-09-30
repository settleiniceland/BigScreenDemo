<template>
  <wd-tabbar
    v-model="tabbarVal"
    class="tabbar"
    fixed
    bordered
    safeAreaInsetBottom
    placeholder
    @change="handleChange"
  >
    <wd-tabbar-item
      v-for="item in store.tabbar"
      :title="t(item.title)"
      :icon="item.icon"
    ></wd-tabbar-item>
  </wd-tabbar>
</template>

<script setup lang="ts">
import { reactive, ref, watch, defineProps } from 'vue'
import { useUserStore } from '@/store'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const props = defineProps({
  // 当前的值
  popVal: {
    type: Number,
    default: 0
  }
})

uni.hideTabBar()
const store = useUserStore()
const tabbarVal = ref(0)

watch(
  () => props.popVal,
  (newVall) => {
    tabbarVal.value = newVall
  },
  { immediate: true }
)

const handleChange = (obj) => {
  tabbarVal.value = props.popVal
  uni.switchTab({
    url: store.tabbar[obj.value].url
  })
}
</script>

<style scoped lang="scss">
.tabbar {
  z-index: 1;
}
</style>
