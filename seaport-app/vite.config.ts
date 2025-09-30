import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'path'
import Unocss from 'unocss/vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni(), Unocss()],
  server: {
    port: 9000,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: "http://localhost:8081",
        // target: "http://192.168.91.50:38080",
        // target: "http://10.40.11.52:8080", // 陶庆洲
        // target: 'https://api.iwipwedabay.com/api/seaport/mis', // 线上环境
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api/, '')
      },
      '/prod-api': {
        target: 'http://192.168.91.51',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/prod-api/, '/prod-api')
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@components': path.resolve(__dirname, './src/components')
    }
  },
  css: {
    // 配置`scss`和`less`全局变量
    preprocessorOptions: {
      scss: {
        additionalData: '@import "@/styles/vars/_base.scss";'
      },
      less: {
        additionalData: '@import "@/styles/vars/_base.less";'
      }
    }
  }
})
