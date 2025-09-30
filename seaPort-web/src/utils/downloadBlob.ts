export const downloadFun = async (res, fileName?: string) => {
  // 确保返回的数据是 Blob
  const blob = new Blob([res], { type: "application/vnd.ms-excel" });

  // 创建下载链接
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `${fileName ?? "计划单模版"}_${new Date().getTime()}.xlsx`; // 下载文件名
  document.body.appendChild(a);
  a.click();

  // 清理对象 URL
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
};
