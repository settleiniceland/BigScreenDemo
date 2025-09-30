import L from "leaflet";
import "leaflet/dist/leaflet.css";
import pire from "@/assets/icons/pire.png";
import { ref, watch, onUnmounted, createApp } from "vue";
import "leaflet.gridlayer.googlemutant";
import PopupInfo from "../components/PopupInfo/index.vue";
import ShipPopupInfo from "../components/ShipPopupInfo/index.vue";
import { BerthStatusTagOptions } from "@/contans";
import { useWebSocketStore } from "@/store/modules/websocketStore";
import { listData } from "@/api/system/dict/data";
import { Colors } from "@/contans";
import barge_1 from "@/assets/images/barge_1.png";
import motherShip_1 from "@/assets/images/motherShip_1.png";
import selfPropelledShip_1 from "@/assets/images/selfPropelledShip_1.png";
import { getAccessToken } from "@/api/map/map";
import { useHarborHook } from "./hooks";
import pairMark from "@/views/harbor/utils/viewTool/pairMark.vue"
import berthDetail from "@/views/harbor/utils/viewTool/berthDetail.vue";
const { openBerthDialog } = useHarborHook();
// 节流函数
const throttle = (fn, wait) => {
  let lastCall = 0;
  let rafId = null;
  return (...args) => {
    const now = performance.now();
    if (now - lastCall < wait && rafId) return;
    lastCall = now;
    if (rafId) cancelAnimationFrame(rafId);
    rafId = requestAnimationFrame(() => {
      fn(...args);
      rafId = null;
    });
  };
};

// 防抖函数
const debounce = (fn, wait) => {
  let timeout;
  return (...args) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => fn(...args), wait);
  };
};

const shipIconMap = {
  0: motherShip_1,
  1: selfPropelledShip_1,
  2: barge_1
};

const websocketStore = useWebSocketStore();
const treeData = ref([]);
const planStatusOptions = ref();

// 北区南区地图中心点坐标
const areaCoordinates = {
  north: [-2.811485, 122.163855],
  south: [-2.869155, 122.184708]
};

const getDictOptions = async type => {
  const res: any = await listData({ dictType: type, pageSize: 50 });
  if (res.code === 200) {
    return res.rows.map(item => ({
      label: item.dictLabel,
      value: item.dictValue
    }));
  }
};

const getBerthColor = (status, list) => {
  if (list?.length > 0 && status === "1") {
    const planInfo = list[0];
    if (planInfo.status === "5") return Colors[Number(planInfo.status)];
  }
  return BerthStatusTagOptions[status]?.color || "blue";
};

const hasPlanInfoListChanged = (oldList, newList) => {
  if (!oldList && !newList) return false;
  if (!oldList || !newList || oldList.length !== newList.length) return true;
  for (let i = 0; i < oldList.length; i++) {
    if (JSON.stringify(oldList[i]) !== JSON.stringify(newList[i])) return true;
  }
  return false;
};

export const useMap = () => {
  let map;
  const layerCache = new Map();
  const shipLayerCache = new Map();
  const popupInstanceCache = new Map();
  const popupDataCache = new Map();
  const iconCache = new Map(); // 缓存 L.divIcon 实例
  const layerGroup = L.layerGroup();
  const shipLayerGroup = L.layerGroup();
  const lastZoomScale = ref(1);
  const lastBounds = ref(null); // 缓存上一次地图视口

  // 码头图标 HTML 模板（保留名称原始样式，优化图标部分）
  const pierIconTemplate = (name, scale) => {
    const cacheKey = `${name}-${scale}`;
    if (iconCache.has(cacheKey)) {
      return iconCache.get(cacheKey).html;
    }
    const html = `
      <div style="display: flex; flex-direction: column; align-items: center;">
        <div style="font-size: ${16 * scale}px; font-weight: bold; padding: ${0.25 * scale}rem ${0.5 * scale}rem; border-radius: ${0.5 * scale}rem; white-space: nowrap; box-shadow: 0rem ${0.125 * scale}rem ${0.3125 * scale}rem rgba(163, 22, 22, 0.2); border: ${0.0625 * scale}rem solid rgba(200, 200, 200, 0.6); backdrop-filter: blur(${0.3125 * scale}rem); color: rgba(255, 255, 255, 1);">${name}</div>
        <img src="${pire}" alt="icon" style="width: ${30 * scale}px; height: ${30 * scale}px; margin-top: ${0.25 * scale}rem; border-radius: ${0.375 * scale}rem; object-fit: cover;" />
      </div>`;
    return html;
  };

  const processNode = node => {
    const id = node.pierId ? `pier-${node.pierId}` : `berth-${node.berthId}`;
    const name = node.pierName || node.berthName || "";
    const geoJsonStr = node.pierGeoJson || node.berthGeoJson;
    let geoJson = null;
    if (geoJsonStr) {
      try {
        geoJson = JSON.parse(geoJsonStr);
      } catch (error) {
        console.warn(`解析 geoJson 失败: ${error}`);
      }
    }
    return {
      id,
      name,
      geoJson,
      pierId: node.pierId,
      berthId: node.berthId,
      berthStatus: node.berthStatus || "",
      reason: node.remark || "",
      planInfoList: node.planInfoList || [],
      berthChildrenList: node.berthChildrenList
        ? node.berthChildrenList.map(processNode)
        : []
    };
  };

  const initializeMap = () => {
    getAccessToken().then(res => {
      const accessToken = res as string;
      
      // 获取本地存储的区域设置，默认为北区
      const savedArea = localStorage.getItem("activeHarborArea") || "north";
      
      map = L.map("map", {
        center: areaCoordinates[savedArea] || areaCoordinates.north, // 使用保存的区域设置初始化
        zoom: 16,
        zoomControl: false,
        attributionControl: false,
        scrollWheelZoom: { smooth: true, debounceTimeout: 200 },
        maxZoom: 18
      });

      L.tileLayer(
        "https://gps.etsingshan.com/rest/qmap/{z}/{x}/{y}.png?accessToken=" + 
        accessToken
      ).addTo(map);
      
      layerGroup.addTo(map);
      shipLayerGroup.addTo(map);

      // const createMaskLayer = () => {
      //   if (maskLayer) map.removeLayer(maskLayer);
      //   const maskPolygon = [
      //     [
      //       [-90, -180],
      //       [-90, 180],
      //       [90, 180],
      //       [90, -180]
      //     ],
      //     ...config.map(layer =>
      //       layer.geoJson.geometry.coordinates[0].map(coord => [
      //         coord[1],
      //         coord[0]
      //       ])
      //     )
      //   ];
      //   maskLayer = L.polygon(maskPolygon, {
      //     fillColor: "#082238",
      //     fillOpacity: 0.7,
      //     stroke: false
      //   }).addTo(map);
      // };

      // config.forEach(item => {
      //   const bounds = [
      //     [item.imageBounds.south, item.imageBounds.west],
      //     [item.imageBounds.north, item.imageBounds.east]
      //   ];
      //   L.imageOverlay(item.img, bounds, { opacity: 1 }).addTo(map);
      //   const coordinates = item.geoJson.geometry.coordinates[0].map(coord => [
      //     coord[1],
      //     coord[0]
      //   ]);
      //   L.polyline(coordinates, {
      //     color: "#4C818E",
      //     weight: 1,
      //     opacity: 1,
      //     fill: false
      //   }).addTo(map);
      // });

      // createMaskLayer();

      const originalShipIconSize = [26.2, 6.2];
      const originalPierIconSize = [30, 30];
      const originalPierIconAnchor = [18, 64];

      const getPierZoomScale = () => {
        const currentZoom = map.getZoom();
        const minZoom = 13;
        const maxZoom = 17;
        const minScale = 0.3;
        const maxScale = 2.0;
        if (currentZoom <= minZoom) return minScale;
        if (currentZoom >= maxZoom) return maxScale;
        const scale =
          minScale +
          ((maxScale - minScale) * (currentZoom - minZoom)) / (maxZoom - minZoom);
        return scale;
      };

      const getShipZoomScale = () => {
        const currentZoom = map.getZoom();
        const referenceZoom = 15;
        const minScale = 0.2;
        const scale = Math.pow(2, currentZoom - referenceZoom);
        return Math.max(scale, minScale);
      };

      const isMarkerInView = marker => {
        const bounds = map.getBounds();
        const latLng = marker.getLatLng();
        return bounds.contains(latLng);
      };

      const updateIconSizes = () => {
        const pierScale = getPierZoomScale();
        const shipScale = getShipZoomScale();
        const currentBounds = map.getBounds();

        // 检查缩放比例和视口是否变化
        if (
          pierScale === lastZoomScale.value &&
          lastBounds.value &&
          currentBounds.equals(lastBounds.value, 0.01)
        ) {
          return;
        }
        lastZoomScale.value = pierScale;
        lastBounds.value = currentBounds;

        const updateShips = () => {
          shipLayerCache.forEach((marker, id) => {
            if (!isMarkerInView(marker)) return;
            const scaledSize = [
              originalShipIconSize[0] * shipScale,
              originalShipIconSize[1] * shipScale
            ];
            const scaledAnchor = [scaledSize[0] / 2, scaledSize[1] / 2];
            const shipData = marker._shipData || {};
            const rotation = shipData.hdg ?? shipData.cog ?? 0;
            const cacheKey = `${id}-${shipScale}-${rotation}`;
            let icon = iconCache.get(cacheKey);
            if (!icon) {
              icon = L.divIcon({
                className: "custom-ship-icon",
                html: `<img src="${
                  marker._iconSrc
                }" style="width: 28.2px; height: 6.2px; transform: scale(${shipScale}) rotate(${rotation}deg); transform-origin: center; image-rendering: pixelated; filter: drop-shadow(0 0 2px rgba(0,0,0,0.5));">`,
                iconSize: scaledSize,
                iconAnchor: scaledAnchor
              });
              iconCache.set(cacheKey, icon);
            }
            marker.setIcon(icon);
          });
        };

        const updatePiers = () => {
          layerCache.forEach((layer, id) => {
            if (!id.startsWith("pier-") || !isMarkerInView(layer)) return;
            const scaledSize = [
              originalPierIconSize[0] * pierScale,
              originalPierIconSize[1] * pierScale
            ];
            const scaledAnchor = [
              originalPierIconAnchor[0] * pierScale,
              originalPierIconAnchor[1] * pierScale
            ];
            const cacheKey = `${id}-${pierScale}`;
            let icon = iconCache.get(cacheKey);
            if (!icon) {
              const iconHtml = pierIconTemplate(layer._name, pierScale);
              icon = L.divIcon({
                className: "custom-pier-icon",
                html: iconHtml,
                iconSize: scaledSize,
                iconAnchor: scaledAnchor
              });
              iconCache.set(cacheKey, icon);
            }
            layer.setIcon(icon);
          });
        };

        updateShips();
        updatePiers();
      };

      const throttledUpdateIconSizes = throttle(updateIconSizes, 100); // 增加节流时间

      const createPopup = (name, berthStatus, reason, planInfoList, id) => {
        if (popupInstanceCache.has(id)) {
          const { container, data } = popupInstanceCache.get(id);
          data.title = name;
          data.berthStatus = berthStatus;
          data.reason = reason;
          data.planInfoList = planInfoList;
          return container;
        }

        const popupData = ref({
          title: name,
          berthStatus,
          reason,
          planInfoList
        });
        popupDataCache.set(id, popupData);
        const popupContainer = document.createElement("div");
        popupContainer.classList.add("custom-berth-popup");
        const app = createApp(PopupInfo, {
          title: popupData.value.title,
          berthStatus: popupData.value.berthStatus,
          reason: popupData.value.reason,
          planInfoList: popupData.value.planInfoList
        });
        app.provide("popupData", popupData);
        app.mount(popupContainer);
        popupInstanceCache.set(id, {
          app,
          container: popupContainer,
          data: popupData.value
        });
        return popupContainer;
      };

      const addPolygonToMap = node => {
        const { geoJson, name, id, berthId, berthStatus, reason, planInfoList } =
          node;
        if (!geoJson) return;

        const isBerth = !!berthId;
        const coordinates = geoJson.geometry.coordinates[0].map(coord => [
          coord[1],
          coord[0]
        ]);
        const color = isBerth
          ? getBerthColor(berthStatus, planInfoList) || "blue"
          : "gray";

        if (layerCache.has(id)) {
          const layer = layerCache.get(id);
          if (isBerth) {
            const currentColor = layer.options.color;
            if (currentColor !== color) {
              layer.setStyle({ color, fillColor: color });
            }
            const newPopupContent = createPopup(
              name,
              berthStatus,
              reason,
              planInfoList,
              id
            );
            layer.getPopup()?.setContent(newPopupContent);
          }
          return;
        }

        if (isBerth) {
          const polygon = L.polygon(coordinates, {
            color,
            weight: 1,
            fillOpacity: 0.4
          });
          const popupContent = createPopup(
            name,
            berthStatus,
            reason,
            planInfoList,
            id
          );
          const popup = L.popup({
            autoPan: true,
            autoPanPadding: [20, 80], // 设置边距
            maxWidth: 300,
            minWidth: 200
          }).setContent(popupContent);
          polygon.bindPopup(popup);

          polygon.on("add", () => {
            if (polygon._path) {
              polygon._path.setAttribute("pointer-events", "auto");
            }
          });

          const popupState = {
            isOverPolygon: false,
            isOverPopup: false,
            popupElement: null,
            shouldKeepOpen: () =>
              popupState.isOverPolygon || popupState.isOverPopup
          };

          const bindPopupEvents = () => {
            polygon.off("click mouseover mouseout popupopen popupclose");

            polygon.on("click", () => {
              openBerthDialog({
                berthName: name,
                berthId: berthId
              });
            });

            const debouncedOpenPopup = debounce(() => {
              if (!popupState.popupElement) polygon.openPopup();
            }, 100);

            const debouncedClosePopup = debounce(() => {
              if (!popupState.shouldKeepOpen()) {
                polygon.closePopup();
                popupState.popupElement = null;
              }
            }, 150);

            polygon.on("mouseover", () => {
              popupState.isOverPolygon = true;
              debouncedOpenPopup();
            });

            polygon.on("mouseout", () => {
              popupState.isOverPolygon = false;
              debouncedClosePopup();
            });

            polygon.on("popupopen", e => {
              popupState.popupElement = e.popup._container;
              if (popupState.popupElement) {
                popupState.popupElement.addEventListener("mouseenter", () => {
                  popupState.isOverPopup = true;
                });
                popupState.popupElement.addEventListener("mouseleave", () => {
                  popupState.isOverPopup = false;
                  debouncedClosePopup();
                });
              }
            });

            polygon.on("popupclose", () => {
              popupState.popupElement = null;
              popupState.isOverPopup = false;
            });
          };

          setTimeout(bindPopupEvents, 0);

          layerGroup.addLayer(polygon);
          layerCache.set(id, polygon);
        } else {
          const tempPolygon = L.polygon(coordinates);
          const bounds = tempPolygon.getBounds();
          const topCenter = L.latLng(
            bounds.getNorth(),
            (bounds.getWest() + bounds.getEast()) / 2
          );
          const adjustedLatLng = map.layerPointToLatLng(
            map.latLngToLayerPoint(topCenter)
          );

          const scale = getPierZoomScale();
          const scaledSize = [
            originalPierIconSize[0] * scale,
            originalPierIconSize[1] * scale
          ];
          const scaledAnchor = [
            originalPierIconAnchor[0] * scale,
            originalPierIconAnchor[1] * scale
          ];
          const cacheKey = `${id}-${scale}`;
          let icon = iconCache.get(cacheKey);
          if (!icon) {
            const iconHtml = pierIconTemplate(name, scale);
            icon = L.divIcon({
              className: "custom-pier-icon",
              html: iconHtml,
              iconSize: scaledSize,
              iconAnchor: scaledAnchor
            });
            iconCache.set(cacheKey, icon);
          }

          const marker = L.marker(adjustedLatLng, {
            icon,
            interactive: false
          });

          marker._name = name;
          layerGroup.addLayer(marker);
          layerCache.set(id, marker);
        }
      };

      const addShipToMap = (ship, index) => {
        const {
          lat,
          lon,
          shipName,
          tonnage,
          materialName,
          status,
          shipType,
          cog,
          hdg,
          rot
        } = ship;
        if (!lat || !lon) return;

        const id = `ship-${index}`;
        const shipIcon = shipIconMap[shipType];

        if (shipLayerCache.has(id)) {
          const marker = shipLayerCache.get(id);
          const currentLatLng = marker.getLatLng();
          if (currentLatLng.lat !== lat || currentLatLng.lng !== lon) {
            marker.setLatLng([lat, lon]);
            marker._shipData = { hdg, cog, rot };

            const shipStatus = planStatusOptions.value?.find(
              item => item.value === status
            )?.label;
            marker
              .getPopup()
              ?.setContent(
                createShipPopup(
                  shipName || `船只 ${index + 1}`,
                  tonnage,
                  materialName,
                  lat,
                  lon,
                  status,
                  shipStatus,
                  shipType
                )
              );
            const scale = getShipZoomScale();
            const scaledSize = [
              originalShipIconSize[0] * scale,
              originalShipIconSize[1] * scale
            ];
            const scaledAnchor = [scaledSize[0] / 2, scaledSize[1] / 2];
            const rotation = hdg ?? cog ?? 0;
            const cacheKey = `${id}-${scale}-${rotation}`;
            let icon = iconCache.get(cacheKey);
            if (!icon) {
              icon = L.divIcon({
                className: "custom-ship-icon",
                html: `<img src="${shipIcon}" style="width: 28.2px; height: 6.2px; transform: scale(${scale}) rotate(${rotation}deg); transform-origin: center; image-rendering: pixelated; filter: drop-shadow(0 0 2px rgba(0,0,0,0.5));">`,
                iconSize: scaledSize,
                iconAnchor: scaledAnchor
              });
              iconCache.set(cacheKey, icon);
            }
            marker.setIcon(icon);
          }
          return;
        }

        const scale = getShipZoomScale();
        const scaledSize = [
          originalShipIconSize[0] * scale,
          originalShipIconSize[1] * scale
        ];
        const scaledAnchor = [scaledSize[0] / 2, scaledSize[1] / 2];
        const rotation = hdg ?? cog ?? 0;
        const cacheKey = `${id}-${scale}-${rotation}`;
        let icon = iconCache.get(cacheKey);
        if (!icon) {
          icon = L.divIcon({
            className: "custom-ship-icon",
            html: `<img src="${shipIcon}" style="width: 28.2px; height: 6.2px; transform: scale(${scale}) rotate(${rotation}deg); transform-origin: center; image-rendering: pixelated; filter: drop-shadow(0 0 2px rgba(0,0,0,0.5));">`,
            iconSize: scaledSize,
            iconAnchor: scaledAnchor
          });
          iconCache.set(cacheKey, icon);
        }

        const shipMarker = L.marker([lat, lon], {
          icon
        });

        shipMarker._iconSrc = shipIcon;
        shipMarker._shipData = { hdg, cog, rot };

        const shipStatus = planStatusOptions.value?.find(
          item => item.value === status
        )?.label;
        const popupContent = createShipPopup(
          shipName || `船只 ${index + 1}`,
          tonnage,
          materialName,
          lat,
          lon,
          status,
          shipStatus,
          shipType
        );
        const popup = L.popup({
          closeButton: true,
          autoClose: false,
          closeOnEscapeKey: true,
          className: "ship-info-popup",
          autoPan: false,
          maxWidth: 300,
          minWidth: 200
        }).setContent(popupContent);
        shipMarker.bindPopup(popup);
        shipMarker.on("mouseout", () => shipMarker.closePopup());

        shipLayerGroup.addLayer(shipMarker);
        shipLayerCache.set(id, shipMarker);
      };

      const createShipPopup = (
        shipName,
        tonnage,
        materialName,
        lat,
        lon,
        status,
        shipStatus,
        shipType
      ) => {
        const popupContainer = document.createElement("div");
        const popupInstance = createApp(ShipPopupInfo, {
          shipName,
          tonnage,
          materialName,
          lat,
          lon,
          shipStatus,
          color: Colors[Number(status)],
          shipType
        });
        popupInstance.mount(popupContainer);
        return popupContainer;
      };

      const renderMapData = newData => {
        treeData.value = newData;
        newData.forEach(item => {
          addPolygonToMap(item);
          if (item.berthChildrenList?.length) {
            item.berthChildrenList.forEach(child => addPolygonToMap(child));
          }
        });
      };

      const renderShipData = ships => {
        if (!ships?.length) return;
        ships.forEach((ship, index) => addShipToMap(ship, index));
      };

      const diffAndUpdate = (oldData, newData) => {
        const oldFlatMap = new Map();
        const newFlatMap = new Map();

        oldData.forEach(item => {
          oldFlatMap.set(item.id, item);
          if (item.berthChildrenList?.length) {
            item.berthChildrenList.forEach(child =>
              oldFlatMap.set(child.id, child)
            );
          }
        });

        newData.forEach(item => {
          newFlatMap.set(item.id, item);
          if (item.berthChildrenList?.length) {
            item.berthChildrenList.forEach(child =>
              newFlatMap.set(child.id, child)
            );
          }
        });

        newFlatMap.forEach((item, id) => {
          const oldItem = oldFlatMap.get(id);
          if (!oldItem) {
            addPolygonToMap(item);
            return;
          }

          const hasChanges =
            oldItem.name !== item.name ||
            oldItem.berthStatus !== item.berthStatus ||
            oldItem.reason !== item.reason ||
            hasPlanInfoListChanged(oldItem.planInfoList, item.planInfoList);
          if (hasChanges && layerCache.has(id)) {
            const layer = layerCache.get(id);
            if (item.berthId) {
              const color = getBerthColor(item.berthStatus, item.planInfoList);
              layer.setStyle({ color, fillColor: color });
              const newPopupContent = createPopup(
                item.name,
                item.berthStatus,
                item.reason,
                item.planInfoList,
                id
              );
              layer.getPopup()?.setContent(newPopupContent);
            }
          }
        });

        oldFlatMap.forEach((item, id) => {
          if (!newFlatMap.has(id)) {
            const layer = layerCache.get(id);
            if (layer) {
              layerGroup.removeLayer(layer);
              layerCache.delete(id);
              iconCache.delete(`${id}-${lastZoomScale.value}`);
            }
          }
        });
      };

      //我的测试方法
      function drawTestPolygon() {
        const coordinates = [
          [-2.8126603181669654, 122.16324967832016],
          [-2.8119744996463725, 122.16522289691652],
          [-2.8092740853029805, 122.16453656001343]
        ];

        // 画多边形
        const polygon = L.polygon(coordinates, {
          color: "black",
          weight: 2,
          fillColor: "black",
          fillOpacity: 0.1
        });
        layerGroup.addLayer(polygon);

        // 计算顶点的中心点
        const bounds = polygon.getBounds();
        const topCenter = L.latLng(
          bounds.getNorth(),
          (bounds.getWest() + bounds.getEast()) / 2
        );

        // 自定义 icon，始终显示 label
        const customIcon = L.divIcon({
          className: "custom-marker-label",
          html: `
            <div style="display: flex; flex-direction: column; align-items: center;">
              <div style="
                background: white;
                border-radius: 8px;
                padding: 2px 6px;
                font-size: 14px;
                font-weight: bold;
                color: #333;
                box-shadow: 0 1px 4px rgba(0,0,0,0.3);
                white-space: nowrap;
                margin-bottom: 4px;
              ">
                这是mark
              </div>
              <div style="
                width: 18px;
                height: 18px;
                background: #2a9df4;
                border-radius: 50%;
                border: 3px solid white;
                box-shadow: 0 1px 4px rgba(0,0,0,0.4);
              "></div>
            </div>
          `,
          iconSize: [60, 40],
          iconAnchor: [30, 40]
        });

        const marker = L.marker(topCenter, { icon: customIcon });
        layerGroup.addLayer(marker);

        // 定义 popup（鼠标移入才显示）
        const popupContent = `
          <div style="padding: 6px; font-size: 14px; color: #333;">
            <b>这里是弹窗</b><br/>
            你可以放任何展示内容，比如：<br/>
            - 一些文字<br/>
            - 一张图片<br/>
            - 表格信息
          </div>
        `;
        const popup = L.popup({
          autoPan: true,
          autoPanPadding: [20, 20],
          closeButton: false
        }).setContent(popupContent);

        // 绑定鼠标事件
        polygon.on("mouseover", (e) => {
          popup.setLatLng(e.latlng); // 在鼠标点显示
          polygon.bindPopup(popup).openPopup();
        });

        polygon.on("mouseout", () => {
          polygon.closePopup();
        });
      }

      // watch(
      //   () => websocketStore.screenPierList,
      //   (newVal, oldVal) => {
      //     drawTestPolygon()
      //     if (!newVal) return;
      //     row.value = newVal;
      //     const newTreeData = newVal.map(processNode);
      //     console.log("这一步的数据是",newTreeData)
      //     if (!oldVal) {
      //       // console.log("newVal",newVal,"oldVal",oldVal)
      //       // console.log("进入if",newTreeData)
      //       // renderMapData(newTreeData);
      //       // setTimeout(() => {
      //       //   map.invalidateSize();
      //       //   layerGroup.eachLayer(layer => {
      //       //     if (layer instanceof L.Polygon && layer.getPopup()) {
      //       //       if (layer._path) {
      //       //         layer._path.setAttribute("pointer-events", "auto");
      //       //       }
      //       //     }
      //       //   });
      //       // }, 1000);
      //     } else {
      //       console.log("newVal",newVal,"oldVal",oldVal)
      //       console.log("进入else",treeData.value,newTreeData)
      //       diffAndUpdate(treeData.value, newTreeData);
      //       treeData.value = newTreeData;
      //     }
      //   },
      //   { immediate: true }
      // );
      const pierLayerGroup = L.layerGroup().addTo(map);
      watch(
        ()=> websocketStore.screen_dockPiers,
        newVal => {
          if(newVal?.length){
            pierLayerGroup.clearLayers();
            for(const val of newVal){
              const points = JSON.parse(val.pierGeoJson).geometry.coordinates[0];
              const coordinates = []
              for(const point of points){
                coordinates.push([point[1],point[0]]);
              }
              const polygon = L.polygon(coordinates, {
                color: "black",
                weight: 1,
                fillColor: "black",
                fillOpacity: 0.1
              });
              pierLayerGroup.addLayer(polygon);
               // 计算顶点的中心点
              const bounds = polygon.getBounds();
              const topCenter = L.latLng(
                bounds.getNorth(),
                (bounds.getWest() + bounds.getEast()) / 2
              );
              const container = document.createElement("div");
              createApp(pairMark,{text:val.pierName,fontSize:26}).mount(container);
              const customIcon = L.divIcon({
                className: "custom-marker-label",
                html: container,
                iconSize: [60, 40],
                iconAnchor: [30, 40]
              })
              const marker = L.marker(topCenter, { icon: customIcon });
              pierLayerGroup.addLayer(marker);
            }
          }
        }
      )
      const berthLayerGroup = L.layerGroup().addTo(map);
      // let closeTimer = undefined;
      watch(
        ()=> websocketStore.screen_dockBerths,
        newVal => {
          if(newVal?.length){
            berthLayerGroup.clearLayers();
            for(const val of newVal){
              //构建数据
              const mapShowDatas = [];
              for(const item of websocketStore.screen_dockPlansForMap){//到港数据
                if(item.hbName === val.berthName){
                  mapShowDatas.push({
                    type: 3,
                    hbName: item.hbName,
                    shipName: item.shipName,
                    materialName: item.materialName,
                    arrivalTime: item.arrivalTime
                  })
                }
              }
              for(const item of websocketStore.screen_dockPlans1){//离泊数据
                if(item.outBerthTime!==undefined && val.berthName===item.hbName){
                  mapShowDatas.push({
                    type: 1,
                    hbName: item.hbName,
                    shipName: item.shipName,
                    materialName: item.materialName,
                    outBerthTime: item.outBerthTime
                  })
                }
              }
              for(const item of websocketStore.screen_dockPlans3){
                if(val.berthName===item.hbName){
                  const plan = {
                    type: 2,
                    hbName: item.hbName,
                    shipName: item.shipName,
                    children: [{
                      materialName: item.materialName,
                      tonnage: item.tonnage,
                      unloadWeight: item.unloadWeight,
                    }]
                  };
                  if(item.params.assistantList!==undefined&&item.params.assistantList.length>0){
                    for(const child of item.params.assistantList){
                      plan.children.push({
                        materialName: child.materialName,
                        tonnage: child.tonnage,
                        unloadWeight: child.unloadWeight,
                      })
                    }
                  }
                  mapShowDatas.push(plan)
                }
              }
              let myColor = mapShowDatas.length>0?"#49c703":"#117ec2";
              //画框  
              const points = JSON.parse(val.berthGeoJson).geometry.coordinates[0];
              const coordinates = []
              for(const point of points){
                coordinates.push([point[1],point[0]]);
              }
              const polygon = L.polygon(coordinates, {
                color: myColor,
                weight: 1,
                fillColor: myColor,
                fillOpacity: 0.5
              });
              //标签
              const bounds = polygon.getBounds();
              const topCenter = L.latLng(
                bounds.getNorth(),
                (bounds.getWest() + bounds.getEast()) / 2
              );
              const container = document.createElement("div");
              createApp(pairMark,{text:val.berthName,fontSize:10}).mount(container);
              const customIcon = L.divIcon({
                className: "custom-marker-label",
                html: container,
                iconSize: [30, 20],
                iconAnchor: [0, 0]
              })
              const marker = L.marker(topCenter, { icon: customIcon });
              //弹框
              const popupContainer = document.createElement("div");
              createApp(berthDetail, {berchDetail: mapShowDatas}).mount(popupContainer);
              const popup = L.popup({
                autoPan: true,
                autoPanPadding: [20, 20],
                closeButton: false,
                maxWidth: 400,
                minWidth: 300,
              }).setContent(popupContainer);
              polygon.on("mouseover", (e) => {
                popup.setLatLng(e.latlng);
                polygon.bindPopup(popup).openPopup();
              });
              // 关闭这个根据坐标自动关闭
              // polygon.on("mouseout", (e) => {
              //   console.log("触发了这个mouseout")
              //   if(closeTimer){
              //     clearTimeout(closeTimer);
              //   }
              //   closeTimer = setTimeout(()=>{
              //     const toElement = e.originalEvent.relatedTarget
              //     if (!popupContainer.contains(toElement)) {
              //       // polygon.closePopup();
              //     }
              //     closeTimer = undefined
              //   },300)
              // });
              // popupContainer.addEventListener("mouseleave", () => {
              //   console.log("触发了这个---mouseleave")
              //   if(closeTimer){
              //     clearTimeout(closeTimer);
              //   }
              //   closeTimer = setTimeout(()=>{
              //     // polygon.closePopup()
              //     closeTimer = undefined
              //   },300)
              // })
              setTimeout(()=>{//延时半秒，确保是在码头图层嵌入后在嵌入泊位图层
                berthLayerGroup.addLayer(polygon);
                berthLayerGroup.addLayer(marker);
              },500)
            }
          }
        }
      )

      map.on("click", e => {
        console.log(`您点击的位置：纬度: ${e.latlng.lat}, 经度: ${e.latlng.lng}`);
        map.closePopup();
      });

      map.on("zoomstart", () => {
        throttledUpdateIconSizes();
      });
      map.on("zoom", throttledUpdateIconSizes);
      map.on("moveend", () => {
        setTimeout(throttledUpdateIconSizes, 100); // 延迟更新
      });

      updateIconSizes();

      setTimeout(() => {
        map.invalidateSize();
        // 重新读取本地存储的区域设置并应用
        const currentArea = localStorage.getItem("activeHarborArea") || "north";
        if (areaCoordinates[currentArea]) {
          map.setView(areaCoordinates[currentArea], 16, { animate: false });
        }
        
        layerGroup.eachLayer(layer => {
          if (layer instanceof L.Polygon && layer.getPopup()) {
            if (layer._path) {
              layer._path.setAttribute("pointer-events", "auto");
            }
          }
        });
      }, 1000);

      (async () => {
        planStatusOptions.value = await getDictOptions("plan_status");
      })();
    });
  };

  // 新增切换区域方法
  const switchMapArea = (area) => {
    if (!map || !areaCoordinates[area]) return;
    
    // 平滑过渡到新位置
    map.flyTo(areaCoordinates[area], 16, {
      duration: 1.5, // 动画持续时间（秒）
      easeLinearity: 0.25
    });
  };

  const customMapArea = (area)=>{
    map.flyTo(area, 16, {
      duration: 1.5, // 动画持续时间（秒）
      easeLinearity: 0.25
    });
  }

  const fullscreenStatus = ref(0);
  const onContentFullScreenChange = () => {
    fullscreenStatus.value = fullscreenStatus.value === 0 ? 1 : 0;
    const appMain = document.querySelector(".grow");
    if (fullscreenStatus.value === 1) appMain.classList.add("fullscreen-mode");
    else appMain.classList.remove("fullscreen-mode");
    
    // 延迟刷新地图并保持当前视图
    setTimeout(() => {
      if (map) {
        map.invalidateSize();
        // 保持当前位置
        const currentArea = localStorage.getItem("activeHarborArea") || "north";
        if (areaCoordinates[currentArea]) {
          map.setView(areaCoordinates[currentArea], 16, { animate: false });
        }
      }
    }, 300);
  };

  const destroyMap = () => {
    if (map) {
      map.off();
      map.remove();
      layerGroup.clearLayers();
      shipLayerGroup.clearLayers();
      layerCache.clear();
      shipLayerCache.clear();
      popupInstanceCache.clear();
      popupDataCache.clear();
      iconCache.clear();
      map = null;
    }
  };

  onUnmounted(destroyMap);

  return { initializeMap, onContentFullScreenChange, fullscreenStatus, switchMapArea, customMapArea};
};
