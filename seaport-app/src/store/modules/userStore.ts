import { defineStore } from 'pinia'
import { Login } from '@/api/interface/login'
import { setTabBarByPermissions } from '@/utils/tabbar'

const useStore = defineStore('userInfo', {
	// arrow function recommended for full type inference
	state: () => ({
		// all these properties will have their type inferred automatically
		token: uni.getStorageSync('token') ? uni.getStorageSync('token') : '',
		userInfo: uni.getStorageSync('userInfo')
			? uni.getStorageSync('userInfo')
			: '',
		permissions: uni.getStorageSync('permissions')
			? uni.getStorageSync('permissions')
			: '',
		roles: uni.getStorageSync('roles') ? uni.getStorageSync('roles') : [],
		language: uni.getStorageSync('language') || 'zh-Hans', // 当前语言
		isUpdate: false,
		updateInfo: {},
		wgtinfo: {},
		tabbar: [
			// {
			// 	url: '/pages/home/index',
			// 	title: 'port.menu.home',
			// 	icon: 'home'
			// },
			{
				url: '/pages/shipPlan/index',
				title: 'port.menu.planManagement',
				icon: 'list'
			},
			{
				url: '/pages/system/system',
				title: 'port.menu.settings',
				icon: 'setting'
			},
		] as any[] // 用户权限对应的tabbar
	}),
	actions: {
		login(userInfo: Login.ResLogin) {
			return new Promise(async (resolve, reject) => {
				console.log('userInfo---', userInfo)
				this.userInfo = userInfo.user
				this.permissions = userInfo.permissions
				this.roles = userInfo.roles
				uni.setStorageSync('userInfo', userInfo.user)
				uni.setStorageSync('permissions', userInfo.permissions)
				uni.setStorageSync('roles', userInfo.roles)
				await setTabBarByPermissions()
				resolve(userInfo)
			})
		},

		logout() {
			this.userInfo = ''
			this.token = null
			this.permissions = []
			this.roles = []
			// this.tabbar = []
			uni.removeStorageSync('userInfo')
			uni.removeStorageSync('token')
			uni.removeStorageSync('permissions')
			uni.setStorageSync('roles', [])
		},
		setToken(token: string) {
			this.token = token
			uni.setStorageSync('token', token)
		},
		setIsUpdate(val: boolean) {
			this.isUpdate = val
		},
		setUpdateInfo(val: any) {
			this.updateInfo = val
		},
		setWgtinfo(val: any) {
			this.wgtinfo = val
		},
		// 设置当前语言
		setLanguage(lang: string) {
			this.language = lang
			uni.setStorageSync('language', lang)
			uni.setLocale(lang)
		}
	}
})

export default useStore