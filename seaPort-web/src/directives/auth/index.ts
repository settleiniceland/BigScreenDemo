import type { Directive, DirectiveBinding } from "vue";
import { useUserStore } from "@/store/modules/user";

export const auth: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const userStore = useUserStore();
    const permissions = userStore.permissions;
    const { value } = binding;

    const isAdmin = permissions.includes("*:*:*");

    if (
      !isAdmin &&
      (!Array.isArray(value) || !value.some(v => permissions.includes(v)))
    ) {
      el.parentNode?.removeChild(el);
    }
  }
};
