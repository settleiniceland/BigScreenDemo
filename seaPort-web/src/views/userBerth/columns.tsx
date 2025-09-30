import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { transformI18n } from "@/plugins/i18n";
import { useRouter } from "vue-router";
import { getBerthUserList } from "@/api/userBerth";
export function useColumns() {
  const router = useRouter();
  const dataList = ref([]);
  const loading = ref(false);
  const form = ref({
    materialName: null,
    materialStatus: null,
    remark01: null
  });
  const columns: any[] = [
    {
      label: transformI18n("imip.page4.obj3"),
      sortable: false,
      width: 120,
      prop: "berthCode",
    },
    {
      label: transformI18n("imip.items.item10"),
      sortable: false,
      prop: "users",
    },
    {
      label: transformI18n("imip.page1.obj38"),
      fixed: "right",
      width: 100,
      slot: "operation"
    }
  ];
  /** 获取列表数据 */
  async function fetchData() {
    dataList.value = []
    loading.value = true;
    try {
      const { data } = (await getBerthUserList(form)) as any;
      const resultMap = new Map();
      for(const item of data){
        if (!resultMap.has(item.berthCode)) {
          resultMap.set(item.berthCode,[])
        }
        resultMap.get(item.berthCode).push(item);
      }
      resultMap.forEach((value,key)=>{
        let users='| ';
        value.forEach((item)=>{
          if(item.userName!==null && item.nickName!==null){
            users+=item.userName+":"+item.nickName+" | ";
          }
        })
        dataList.value.push({
          berthCode: key,
          users: users
        })
      })
    } catch (error) {
      ElMessage.error("获取数据失败", error);
    }
    loading.value = false;
  };
  onMounted(fetchData); // 组件挂载时加载数据
  return {
    columns,
    dataList,
    fetchData,
  };
}
