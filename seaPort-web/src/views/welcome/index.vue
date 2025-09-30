<script setup lang="ts">
import img1 from "@/assets/images/1.jpg";
import img2 from "@/assets/images/2.jpg";
import img3 from "@/assets/images/3.jpg";
import img4 from "@/assets/images/4.jpg";
import { ref } from "vue";
// Import Element Plus icons correctly
import * as ElementPlusIconsVue from "@element-plus/icons-vue";

const imgs = [img1, img2, img3, img4];
const activeIndex = ref(0); // 记录当前轮播索引
const coreFunctions = [
  {
    title: "码头数据可视化",
    icon: "TrendCharts",
    color: "#4381E4"
  },
  {
    title: "船舶计划单管理",
    icon: "Calendar",
    color: "#10B981"
  },
  {
    title: "作业卸货单管理",
    icon: "Document",
    color: "#F59E0B"
  },
  {
    title: "计划单归档管理",
    icon: "Folder",
    color: "#8B5CF6"
  },
  {
    title: "物资种类管理",
    icon: "List",
    color: "#EC4899"
  },
  {
    title: "码头泊位管理",
    icon: "Location",
    color: "#3B82F6"
  },
  {
    title: "卸货作业管理",
    icon: "Ship",
    color: "#0EA5E9"
  },
  {
    title: "更多功能建设",
    icon: "More",
    color: "#6B7280"
  }
];

// 监听轮播切换事件
const handleChange = (index: number) => {
  activeIndex.value = index;
};

defineOptions({
  name: "Welcome"
});
// const { isDark } = useDark();
</script>
<template>
  <div class="welcome-page">
    <!-- Hero Section with Carousel -->
    <div class="hero-section">
      <el-carousel
        :interval="4000"
        type="card"
        height="360px"
        @change="handleChange"
      >
        <el-carousel-item v-for="(item, index) in imgs" :key="item">
          <div class="carousel-image-container">
            <img :src="item" alt="港口景观" class="carousel-image" />
            <div class="overlay" :class="{ active: index === activeIndex }">
              <div v-if="index === activeIndex" class="carousel-content">
                <h1 class="welcome-title">Seaport Management System</h1>
                <p class="welcome-subtitle">海港管理系统</p>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- Introduction Section -->
    <div class="section intro-section">
      <div class="section-header">
        <h2 class="section-title">系统简介</h2>
        <div class="title-underline" />
      </div>
      <div class="section-content">
        <p class="intro-text">
          海港管理系统(Seaport Management System)是一款集<span class="highlight"
            >智能化</span
          >、<span class="highlight">数字化</span>、<span class="highlight"
            >可视化</span
          >于一体的港口运营管理平台。该系统通过信息化手段优化港口作业流程，提高运营效率，确保码头、泊位、船舶，卸货，货物等各环节的顺畅运转，实时监控船舶动态，实现船舶进出港智能调度船航线，卸货作业进度的实时管理，为港务部工作提供高效、实时、智能的管理和运行,提升管理效率。
        </p>
      </div>
    </div>

    <!-- Core Functions Section -->
    <div class="section functions-section">
      <div class="section-header">
        <h2 class="section-title">核心功能</h2>
        <div class="title-underline" />
        <p class="section-subtitle">可点击左侧菜单进行体验</p>
      </div>
      <div class="functions-grid">
        <div
          v-for="item in coreFunctions"
          :key="item.title"
          class="function-card"
          :style="{ backgroundColor: item.color + '15' }"
        >
          <div class="function-icon" :style="{ backgroundColor: item.color }">
            <!-- Fixed icon component usage -->
            <el-icon :size="24">
              <component :is="ElementPlusIconsVue[item.icon]" />
            </el-icon>
          </div>
          <div class="function-title" :style="{ color: item.color }">
            {{ item.title }}
          </div>
        </div>
      </div>
    </div>

    <!-- Features Section -->
    <div class="section features-section">
      <div class="section-header">
        <h2 class="section-title">系统优势</h2>
        <div class="title-underline" />
      </div>
      <div class="features-container">
        <div class="feature-item">
          <div class="feature-icon">
            <el-icon :size="24">
              <component :is="ElementPlusIconsVue.DataAnalysis" />
            </el-icon>
          </div>
          <h3>数据可视化</h3>
          <p>直观展示港口运营数据，提供实时监控和分析能力</p>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <el-icon :size="24">
              <component :is="ElementPlusIconsVue.Connection" />
            </el-icon>
          </div>
          <h3>智能调度</h3>
          <p>优化船舶进出港调度，提高泊位利用率和作业效率</p>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <el-icon :size="24">
              <component :is="ElementPlusIconsVue.Monitor" />
            </el-icon>
          </div>
          <h3>实时监控</h3>
          <p>全方位监控港口各环节运行状态，及时预警异常情况</p>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <el-icon :size="24">
              <component :is="ElementPlusIconsVue.Money" />
            </el-icon>
          </div>
          <h3>成本优化</h3>
          <p>减少资源浪费，降低运营成本，提升港口整体效益</p>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="footer">
      <p>技术支持：纬达贝信息技术部</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.welcome-page {
  padding: 0;
  font-family: "PingFang SC", "Helvetica Neue", Helvetica, "Hiragino Sans GB",
    Arial, sans-serif;
  color: #333;
  background-color: #f8fafc;
}

// Hero Section Styles
.hero-section {
  margin-bottom: 2rem;
}

.carousel-image-container {
  position: relative;
  height: 100%;
  width: 100%;
  overflow: hidden;
  border-radius: 8px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    to bottom,
    rgba(0, 31, 63, 0.5),
    rgba(0, 95, 153, 0.8)
  );
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0.9;
  transition: opacity 0.5s ease;

  &.active {
    opacity: 1;
  }
}

.carousel-content {
  text-align: center;
  padding: 2rem;
  color: white;
}

.welcome-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.welcome-subtitle {
  font-size: 1.25rem;
  margin: 0;
  font-weight: 400;
}

// Section Common Styles
.section {
  padding: 2rem 2rem 3rem;
  margin-bottom: 1.5rem;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  text-align: center;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0 0 0.5rem;
  color: #0a3d62;
}

.title-underline {
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #0a3d62, #3498db);
  margin: 0.5rem auto 1rem;
  border-radius: 2px;
}

.section-subtitle {
  color: #6b7280;
  font-size: 1rem;
  margin: 0.5rem 0 0;
}

// Introduction Section Styles
.intro-section {
  background: linear-gradient(135deg, #ffffff, #f0f7ff);
}

.intro-text {
  font-size: 1.05rem;
  line-height: 1.8;
  text-align: justify;
  color: #4b5563;
}

.highlight {
  color: #0a3d62;
  font-weight: 600;
}

// Core Functions Section Styles
.functions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1.5rem;
}

.function-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.75rem 1.25rem;
  border-radius: 12px;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  }
}

.function-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 1rem;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.function-title {
  font-size: 1.1rem;
  font-weight: 600;
  text-align: center;
}

// Features Section Styles
.features-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-item {
  text-align: center;
  padding: 1.5rem;
  border-radius: 10px;
  background-color: #f8f9fa;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }
}

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0a3d62, #3498db);
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 1rem;
  color: white;
}

.feature-item h3 {
  font-size: 1.15rem;
  font-weight: 600;
  margin: 0.75rem 0;
  color: #0a3d62;
}

.feature-item p {
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
}

// Footer Styles
.footer {
  text-align: center;
  padding: 1.5rem;
  color: #6b7280;
  font-size: 0.9rem;
}

// Element Plus Carousel Customization
:deep(.el-carousel__item) {
  border-radius: 12px;
}

:deep(.el-carousel__indicators) {
  bottom: 1rem;
}

:deep(.el-carousel__indicator--card .el-carousel__button) {
  background-color: rgba(255, 255, 255, 0.7);
}

:deep(.el-carousel__indicator--card.is-active .el-carousel__button) {
  background-color: #3498db;
}

// Make icons more visible
:deep(.el-icon) {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
}
</style>
