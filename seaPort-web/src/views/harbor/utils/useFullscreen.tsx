import { ref } from "vue";
import * as echarts from "echarts";

export function useFullscreen() {
  const fullscreenState = ref<Record<string, boolean>>({});
  const activeComponent = ref<HTMLElement | null>(null);

  // Enter fullscreen
  const enterFullscreen = (componentKey: string, componentRef: HTMLElement) => {
    if (activeComponent.value) {
      exitFullscreen(
        Object.keys(fullscreenState.value).find(
          key => fullscreenState.value[key]
        )!
      );
    }

    activeComponent.value = componentRef;

    if (componentKey !== "berthList" && componentKey !== "berthEchat") {
      const parent = componentRef.parentElement;
      if (parent) {
        parent.style.zIndex = "999"; // Set parent z-index
        Array.from(parent.children).forEach(child => {
          if (child !== componentRef) {
            (child as HTMLElement).style.display = "none";
          }
        });
      }
    }

    componentRef.classList.add("fullscreen-container");
    fullscreenState.value[componentKey] = true;

    setTimeout(() => {
      window.dispatchEvent(new Event("resize"));

      const chartElements = componentRef.querySelectorAll(
        "[_echarts_instance_]"
      );
      chartElements.forEach(el => {
        const instance = echarts.getInstanceByDom(el as HTMLElement);
        if (instance) {
          instance.resize();
        }
      });
    }, 300);
  };

  // Exit fullscreen
  const exitFullscreen = (componentKey: string) => {
    if (activeComponent.value) {
      if (componentKey !== "berthList" && componentKey !== "berthEchat") {
        const parent = activeComponent.value.parentElement;
        if (parent) {
          parent.style.zIndex = ""; // Restore parent z-index
          Array.from(parent.children).forEach(child => {
            (child as HTMLElement).style.display = "block";
          });
        }
      }

      activeComponent.value.classList.remove("fullscreen-container");

      // Trigger resize after exiting fullscreen
      setTimeout(() => {
        window.dispatchEvent(new Event("resize"));

        // Force any ECharts instances to resize
        const chartElements = activeComponent.value?.querySelectorAll(
          "[_echarts_instance_]"
        );
        chartElements?.forEach(el => {
          const instance = echarts.getInstanceByDom(el as HTMLElement);
          if (instance) {
            instance.resize();
          }
        });
      }, 300);

      activeComponent.value = null;
    }

    fullscreenState.value[componentKey] = false;
  };

  // Check fullscreen status
  const isFullscreen = (componentKey?: string) => {
    if (componentKey) {
      return fullscreenState.value[componentKey] ?? false;
    }
    return Object.values(fullscreenState.value).some(v => v);
  };

  return { enterFullscreen, exitFullscreen, isFullscreen };
}
