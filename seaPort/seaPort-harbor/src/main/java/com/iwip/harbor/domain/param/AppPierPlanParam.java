package com.iwip.harbor.domain.param;

import com.iwip.common.core.domain.BaseEntity;
import com.iwip.common.utils.StringUtils;

import java.util.List;

/**
 * app 船舶进度统计接口参数
 *
 * @author taoqz
 * @create 2025-04-14
 */
public class AppPierPlanParam extends BaseEntity {

     /**
      * 码头类型
      */
     private String pierType;

     /**
      * 泊位名称
      */
     private String berthName;

     /**
      * 物资名称
      */
     private String materialName;

     private List<String> berthNameList;

     private List<String> materialNameList;

     public String getPierType() {
          return pierType;
     }

     public void setPierType(String pierType) {
          this.pierType = pierType;
     }

     public String getBerthName() {
          return berthName;
     }

     public void setBerthName(String berthName) {
          this.berthName = berthName;
     }

     public String getMaterialName() {
          return materialName;
     }

     public void setMaterialName(String materialName) {
          this.materialName = materialName;
     }

     public List<String> getBerthNameList() {
          if (StringUtils.isNotBlank(this.berthName)) {
               return List.of(this.berthName.split(","));
          }
          return berthNameList;
     }

     public void setBerthNameList(List<String> berthNameList) {
          this.berthNameList = berthNameList;
     }

     public List<String> getMaterialNameList() {
          if (StringUtils.isNotBlank(this.materialName)) {
               return List.of(this.materialName.split(","));
          }
          return materialNameList;
     }

     public void setMaterialNameList(List<String> materialNameList) {
          this.materialNameList = materialNameList;
     }
}
