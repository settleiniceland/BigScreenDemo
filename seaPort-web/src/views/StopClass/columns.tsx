import { transformI18n } from "@/plugins/i18n";
export function useColumns() {
  const columns: any[] = [
    {
      label: transformI18n("内容"),
      sortable: false,
      prop: "detail",
    },
    {
      label: transformI18n("内容（印尼文）"),
      sortable: false,
      prop: "remark01"
    },
    {
      label: transformI18n("是否免滞期费"),
      sortable: false,
      width: 120,
      prop: "avoidCollectFee",
    },
    {
      label: transformI18n("imip.page1.obj38"),
      fixed: "right",
      width: 150,
      slot: "operation"
    }
  ];
  return {columns};
}