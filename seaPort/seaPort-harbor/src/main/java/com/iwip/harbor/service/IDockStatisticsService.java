package com.iwip.harbor.service;

import com.iwip.harbor.domain.excel.DockBerthUsaDetailExcel;
import com.iwip.harbor.domain.param.DockBerthUsageParam;
import com.iwip.harbor.domain.vo.DockBerthUsageRateVo;

import java.util.List;
import java.util.Map;

/**
 * @author taoqz
 * @create 2025-04-17
 */
public interface IDockStatisticsService {

    List<DockBerthUsageRateVo> berthUsageRate(DockBerthUsageParam dockBerthUsageParam);

    List<DockBerthUsaDetailExcel> exportBerthUsageDetail(DockBerthUsageParam dockBerthUsageParam);

}
