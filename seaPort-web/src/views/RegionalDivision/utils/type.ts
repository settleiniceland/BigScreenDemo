interface GeoJsonFeature {
  type: "Feature";
  geometry: {
    type: "Polygon" | "Point" | "LineString";
    coordinates: number[][] | number[];
  };
  properties?: Record<string, any>;
}

interface FormProps {
  formInline: GeoJsonFeature | null;
  areaName: string;
  status: number;
}

export type { GeoJsonFeature, FormProps };
