import { setTabBarByPermissions } from "@/utils/tabbar";

export default {
  // onNavigationBarButtonTap(e: any) {
  //   console.log('全局监听到导航栏按钮点击:', e)
  //   uni.showActionSheet({
  //     itemList: ['当前：出库管理', '入库管理'],
  //     success: function (res) {
  //       console.log('选中了第' + (res.tapIndex + 1) + '个按钮');
  //     },
  //     fail: function (res) {
  //       console.log(res.errMsg);
  //     }
  //   });
  // },
  async onReady() {
    // console.log('全局mixin-onReady-start')
    // const pages = getCurrentPages();
    // const currentPage = pages[pages.length - 1];
    // console.log('当前页面路径:', currentPage.route);
    // if(currentPage.route !== 'pages/login/login' ) {
    //   await setTabBarByPermissions()
    // }
    // console.log('全局mixin-onReady-end')
  }
}