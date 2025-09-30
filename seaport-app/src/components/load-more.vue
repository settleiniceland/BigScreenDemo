<template>
  <wd-loadmore :state="status"  :loading-text="loadText.loading" :finished-text="loadText.nomore" />
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const props = defineProps({
  status: {    //loading/finished/error
    type: String,
    deafult: 'loading'  
  }
})

const loadText = ref({
  loadmore: t('loadmore'),
  loading: t('loading'),
  nomore: t('nomore'),
})
</script>
<style scoped lang="less"></style>
