import { useRoute } from "vue-router";

export const usePageSessionStorage = () => {
  const route = useRoute();

  const key = `list_params_${route.path}`;
  const searchKey = `hasSearch${route.path}`;
  const getPageSessionStorage = () => {
    if (sessionStorage.getItem(searchKey) === "true") {
      const newSearchParams = JSON.parse(sessionStorage.getItem(key));
      sessionStorage.removeItem(searchKey);
      sessionStorage.removeItem(key);
      return newSearchParams;
    }
    return null;
  };

  const setPageSessionStorage = (value: any) => {
    sessionStorage.setItem(searchKey, "true");
    sessionStorage.setItem(key, JSON.stringify(value));
  };
  const isUpdatePageSessionStorage = () => {
    return sessionStorage.getItem(searchKey) === "true";
  };
  return {
    getPageSessionStorage,
    setPageSessionStorage,
    isUpdatePageSessionStorage
  };
};
