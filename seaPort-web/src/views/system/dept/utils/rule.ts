import { reactive } from "vue";
import type { FormRules } from "element-plus";
import { isPhone, isEmail } from "@pureadmin/utils";

/** 自定义表单规则校验 */
export const formRules = reactive(<FormRules>{
  deptName: [{ required: true, message: "部门名称为必填项", trigger: "blur" }],
  orderNum: [{ required: true, message: "顺序名称为必填项", trigger: "blur" }],
});
