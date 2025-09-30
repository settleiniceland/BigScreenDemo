interface FormItemProps {
  menuId: any,
  addMenuId?: any,
  parentId: number,
  menuName: any,
  icon: any,
  menuType: string,
  orderNum: any,
  isFrame: string,
  isCache: string,
  visible: string,
  status: string,
  path: string,
  component: string,
  perms: string,
  query: string,
  menuNameEn:string,
  menuNameInd:string
}
interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
