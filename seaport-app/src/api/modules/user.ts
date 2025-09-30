import http from '../http'
import { Login } from '../interface/login'
import { Result } from '../interface/index'

function login(data: Login.ReqLoginParams) {
  return http.post<any, Login.ResToken>('login', data)
}

/**
 * 获取验证码
 * @param phone 手机号
 */
function getCode() {
  return http.get<any, Result & Login.ResCode>('captchaImage')
}
function getUserInfo() {
  return http.get<any, Login.ResLogin>('getInfo')
}
function updateUserPwd(oldPassword: string, newPassword: string) {
  return http.put<any, Result>(
    `system/user/profile/updatePwd?oldPassword=${oldPassword}&newPassword=${newPassword}`
  )
}
// 修改用户个人信息
function updateUserProfile(data: Login.UserInfo) {
  return http({
    url: '/system/user/profile',
    method: 'put',
    data
  })
}
export default {
  login,
  getCode,
  getUserInfo,
  updateUserPwd,
  updateUserProfile
}
