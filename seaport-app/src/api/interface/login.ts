export namespace Login {
  export interface ReqLoginParams {
    username: string
    password: string
    code: string
    uuid: string
  }
  export interface ResToken {
    token: string
  }
  export interface ResCode {
    captchaEnabled: boolean
    img: string
    uuid: string
  }
  export interface ResLogin {
    user: Login.UserInfo
    permissions: string[]
    roles: string[]
  }
  export interface UserInfo {
    admin: boolean
    avatar: string
    delFlag: string
    deptId: number
    email: string
    nickName: string
    phonenumber: string
    remark: string
    sex: string
    status: string
    userId: number
    userName: string
    roles?: Login.Role[]
    dept?: Login.Dept
  }
  export interface Role {
    admin: boolean
    dataScope: string
    deptCheckStrictly: boolean
    menuCheckStrictly: boolean
    roleId: number
    roleKey: string
    roleName: string
    roleSort: number
    status: string
  }
  export interface Dept {
    ancestors: string
    deptId: number
    deptName: string
    leader: string
    orderNum: number
    parentId: number
    status: string
    children: Login.Dept[]
  }
}
