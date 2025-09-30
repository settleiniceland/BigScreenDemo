import { t } from '@/main'
import { useUserStore } from '@/store'

/// 根据权限设置tabbar
export const setTabBarByPermissions = () => {
  return new Promise((resolve, reject) => {
    const store = useUserStore()
    // store.tabbar = []
    const roles = uni.getStorageSync('roles') || []
    console.log('setTabBar-用户-角色---', roles)
    const permissions = uni.getStorageSync('permissions') || []
    console.log('setTabBar-用户-权限---', permissions)

    //设置模块权限
    // if (
    //   roles.some(
    //     (item: string) => item === 'admin'
    //   )
    // ) {

    // } else {

    // }
    resolve(store.tabbar)
  })
}
