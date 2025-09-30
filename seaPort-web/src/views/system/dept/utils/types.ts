interface FormItemProps {
  dpName;
  pierGeoJson;
  type;
}
interface FormItemProps1 {
  deptId;
  parentId;
  deptName;
  orderNum;
  leader;
  phone;
  email;
  berthStatus;
  higherDeptOptions;
  geoJson;
  // children;
}
interface FormProps {
  higherDeptOptions: any;
  deptName: any;
  formInline: FormItemProps1;
}

export type { FormItemProps, FormProps };
