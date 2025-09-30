<template>
  <div class="marker">
    <div class="label" :style="{ fontSize: fontSize + 'px' }">{{ text }}</div>
    <div class="dot"></div>
  </div>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    required: true
  },
  fontSize: {
    type: Number,
    default: 14
  }
});
</script>

<style scoped>
.marker {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.label {
  background: white;
  border-radius: 8px;
  padding: 2px 6px;
  font-weight: bold;
  color: #333;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  white-space: nowrap;
  margin-bottom: 4px;
}

.dot {
  width: 18px;
  height: 18px;
  background: #2a9df4;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
}
</style>
