package com.iwip.harbor.service.impl;

import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.excel.DockBerthUsaDetailExcel;
import com.iwip.harbor.domain.param.DockBerthUsageParam;
import com.iwip.harbor.domain.vo.DockBerthUsageRateVo;
import com.iwip.harbor.domain.vo.DockPlanDockingVo;
import com.iwip.harbor.mapper.DockPlanMapper;
import com.iwip.harbor.service.IDockBerthService;
import com.iwip.harbor.service.IDockStatisticsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author taoqz
 * @create 2025-04-17
 */
@Service
public class DockStatisticsServiceImpl implements IDockStatisticsService {

    private final DockPlanMapper dockPlanMapper;
    private final IDockBerthService dockBerthService;

    @Autowired
    public DockStatisticsServiceImpl(DockPlanMapper dockPlanMapper, IDockBerthService dockBerthService) {
        this.dockPlanMapper = dockPlanMapper;
        this.dockBerthService = dockBerthService;
    }

    @Override
    public List<DockBerthUsageRateVo> berthUsageRate(DockBerthUsageParam dockBerthUsageParam) {
        //1.初始化泊位信息
        String[] yearMonthStr = dockBerthUsageParam.getYearMonth().split("-");
        DockBerth dockBerth = new DockBerth();
        List<DockBerth> dockBerthList = dockBerthService.selectDockBerthList(dockBerth);
        Map<String, Long> berthMap = dockBerthList.stream()
                .collect(Collectors.toMap(
                        DockBerth::getBerthName,
                        b -> 0L,
                        (v1, v2) -> v1, // key 冲突时保留第一个
                        LinkedHashMap::new
                ));
        //2.设置时间范围
        int year = Integer.parseInt(yearMonthStr[0]);
        int month = Integer.parseInt(yearMonthStr[1]);
        // 本月第一天 00:00:00
        LocalDateTime dockingStartTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        // 获取该月的最后一天
        YearMonth yearMonth = YearMonth.of(year, month);
        int lastDay = yearMonth.lengthOfMonth();
        // 本月最后一天 23:59:59.999999999
        LocalDateTime dockingEndTime = LocalDateTime.of(year, month, lastDay, 23, 59, 59);
        //3.查询当月的靠泊计划数据
        List<DockPlanDockingVo> dockPlanDockingVoList = dockPlanMapper.selectListByDockingTimeAndOutBerthTime(dockingStartTime, dockingEndTime, null, null);
        // 排除之前泊位id是空的数据
        // dockPlanDockingVoList = dockPlanDockingVoList.stream().filter(e -> Objects.nonNull(e.getHbId())).toList();
        //4.找出存在时间重叠的靠泊计划
        List<DockPlanDockingVo> dockPlanCopyList = new ArrayList<>(dockPlanDockingVoList);

        // 初始化集合：保存所有有交集的靠泊计划组（每个组是一些时间重叠的 DockPlanDockingVo）
        List<List<DockPlanDockingVo>> overlapBerthListTotal = new ArrayList<>();
        // 保存已处理过的靠泊计划ID（避免重复处理）
        List<Long> overlapBerthPlanIdListTotal = new ArrayList<>();
        for (DockPlanDockingVo current : dockPlanDockingVoList) {
            // 跳过已处理的计划
            if (overlapBerthPlanIdListTotal.contains(current.getId())) {
                continue;
            }
            // 若有换泊时间，则将“离泊时间”设为换泊时间
            if (Objects.nonNull(current.getMoveBerthTime())) {
                current.setOutBerthTime(current.getMoveBerthTime());
            }

            // 检查是否存在其他船与当前船在同一个泊位、时间上有交集 创建临时变量存储当前靠泊计划的重叠项
            List<DockPlanDockingVo> overlapBerthList = new ArrayList<>();
            boolean flag = false;
            for (DockPlanDockingVo other : dockPlanCopyList) {

                boolean isSameBerth = false;
                // 判断是否在同一个泊位
                if (Objects.isNull(other.getHbId()) || Objects.isNull(current.getHbId())) {
                    isSameBerth = Objects.equals(other.getHbName(), current.getHbName());
                } else {
                    isSameBerth = Objects.equals(other.getHbId(), current.getHbId());
                }
                /*
                  1.判断时间是否有交集
                  2.排除自己；
                  3.确保同一泊位;
                  4.时间段是否重叠
                 */
                if (!Objects.equals(other.getId(), current.getId())
                        && isSameBerth
                        && isOverlap(current.getDockingTime(), current.getOutBerthTime(),
                        other.getDockingTime(), other.getOutBerthTime())) {

                    overlapBerthList.add(other);
                    overlapBerthPlanIdListTotal.add(other.getId());
                    flag = true;
                }

            }
            // 将当前计划加入重叠组中
            if (flag) {
                overlapBerthList.add(current);
                overlapBerthListTotal.add(overlapBerthList);

                overlapBerthPlanIdListTotal.add(current.getId());
            }
            // 清除已处理的计划
            dockPlanCopyList = dockPlanCopyList.stream()
                    .filter(plan -> !overlapBerthPlanIdListTotal.contains(plan.getId()))
                    .toList();
        }

        //5.处理重叠计划
        for (List<DockPlanDockingVo> dockPlans : overlapBerthListTotal) {
            long minutes = 0L;
            // 获取泊位名
            Optional<DockPlanDockingVo> firstPlanOpt = dockPlans.stream().findFirst();
            if (firstPlanOpt.isEmpty()) {
                continue; // 跳过空项，防止异常
            }
            String berthName = firstPlanOpt.map(DockPlanDockingVo::getHbName).orElse("未知泊位");

            //每条船预留靠泊+离泊各1小时（2小时/船）
            int size = dockPlans.size();
            minutes += (size * 2 * 60L);

            // 最早靠泊时间
            LocalDateTime earliestDocking = dockPlans.stream()
                    .map(DockPlanDockingVo::getDockingTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            // 最晚离泊时间
            LocalDateTime latestOutBerth = dockPlans.stream()
                    .map(DockPlanDockingVo::getOutBerthTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            // 计算总使用时间（分钟）
            long diffMinutes = Duration.between(earliestDocking, latestOutBerth).toMinutes();

            minutes += diffMinutes;

            // 若计划结束时间超过查询月份，则扣除多余的一小时
            List<DockPlanDockingVo> outBerthTimeIsOver = dockPlans.stream()
                    .filter(plan -> plan.getOutBerthTime().isAfter(dockingEndTime)).toList();

            minutes -= outBerthTimeIsOver.size() * 60L;

            //更新泊位使用时间
            if (berthMap.containsKey(berthName)) {
                if (berthMap.get(berthName) == null) {
                    berthMap.put(berthName, minutes);
                } else {
                    berthMap.put(berthName, berthMap.get(berthName) + minutes);
                }
            }
        }

        // 6.处理非重叠计划 处理没有交集的独立靠泊计划
        for (DockPlanDockingVo dockPlan : dockPlanDockingVoList) {
            if (dockPlan.getHbName().equals("15T#")) {
                System.out.println();
            }
            String berthName = dockPlan.getHbName();
            // 跳过已处理的重叠计划
            if (overlapBerthPlanIdListTotal.contains(dockPlan.getId())) {
                continue;
            }

            LocalDateTime dockingTime = dockPlan.getDockingTime();
            LocalDateTime outBerthTime = dockPlan.getOutBerthTime();
            // 基础使用时间 = 实际靠泊时间 + 靠离泊各1小时
            long diffMinutes = Duration.between(dockingTime, outBerthTime).toMinutes() + (2 * 60);
            // 累加到对应泊位
            if (berthMap.containsKey(berthName)) {
                berthMap.merge(berthName, diffMinutes, Long::sum);
            }
        }

        //7.处理跨月的靠泊计划 理跨月靠泊计划（上月靠泊，本月离泊）
        YearMonth thisMonth = YearMonth.of(year, month);
        // 上一个月
        YearMonth lastMonth = thisMonth.minusMonths(1);

        // 上月的开始时间：xxxx-xx-01 00:00:00
        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();

        // 上月的结束时间：上月最后一天 23:59:59
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atTime(23, 59, 59);
        //查询上月靠泊、本月离泊的数据
        List<DockPlanDockingVo> lastMonthList = dockPlanMapper.selectListByDockingTimeAndOutBerthTime(startOfLastMonth, endOfLastMonth, dockingStartTime, dockingEndTime);

        for (DockPlanDockingVo dockPlanDockingVo : lastMonthList) {
            String berthName = dockPlanDockingVo.getHbName();
            // 只统计在本月内的使用时间
            long diffMinutes = Duration.between(endOfLastMonth, dockPlanDockingVo.getOutBerthTime()).toMinutes() + (60);
            //累加到对应泊位
            if (berthMap.containsKey(berthName)) {
                berthMap.merge(berthName, diffMinutes, Long::sum);
            }
        }

        //8.整合结果并返回
        List<DockBerthUsageRateVo> result = new ArrayList<>();
        for (String berthName : berthMap.keySet()) {
            BigDecimal minutes = BigDecimal.valueOf(berthMap.get(berthName)).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

            DockBerthUsageRateVo dockBerthUsageRateVo = getDockBerthUsageRateVo(berthName, minutes, dockingStartTime);

            result.add(dockBerthUsageRateVo);
        }

        return result;
    }

    @NotNull
    private static DockBerthUsageRateVo getDockBerthUsageRateVo(String berthName, BigDecimal minutes, LocalDateTime dockingStartTime) {
        DockBerthUsageRateVo dockBerthUsageRateVo = new DockBerthUsageRateVo();
        dockBerthUsageRateVo.setBerthName(berthName);
        // minutes 表示当前泊位在查询月份内的总使用时间
        // dockingStartTime.toLocalDate().lengthOfMonth() 获取当前查询月份的总天数
        // lengthOfMonth() * 24L 将天数转换为小时数（例如 30 天 × 24 小时 = 720 小时），表示该月份内泊位的最大可用时间
        BigDecimal rate = minutes
                .divide(BigDecimal.valueOf(dockingStartTime.toLocalDate().lengthOfMonth() * 24L), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)) // 转为百分比
                .setScale(2, RoundingMode.HALF_UP);
        dockBerthUsageRateVo.setRate(rate.compareTo(BigDecimal.valueOf(100)) > 0 ? "100%" : rate + "%");

        dockBerthUsageRateVo.setActualRate(minutes
                .divide(BigDecimal.valueOf(dockingStartTime.toLocalDate().lengthOfMonth() * 24L), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)) // 转为百分比
                .setScale(2, RoundingMode.HALF_UP) + "%");
        return dockBerthUsageRateVo;
    }

    @Override
    public List<DockBerthUsaDetailExcel> exportBerthUsageDetail(DockBerthUsageParam dockBerthUsageParam) {
        // 获取该月所有记录
        List<DockBerthUsaDetailExcel> rawList = dockPlanMapper.selectListByDockingTime(dockBerthUsageParam.getYearMonth());

        // 分组：按泊位名称
        Map<String, List<DockBerthUsaDetailExcel>> berthMap = rawList.stream()
                .collect(Collectors.groupingBy(DockBerthUsaDetailExcel::getHbName));

        // 排序泊位key：数字优先，字母次之
        List<String> sortedKeys = berthMap.keySet().stream()
                .sorted(Comparator.comparing((String k) -> {
                    String num = k.replaceAll("[^0-9]", "");
                    return Integer.parseInt(num);
                }).thenComparing(k -> {
                    String letter = k.replaceAll("[^A-Z]", "");
                    return letter.isEmpty() ? "Z" : letter;
                }))
                .toList();

        List<DockBerthUsaDetailExcel> result = new ArrayList<>();

        for (String key : sortedKeys) {
            List<DockBerthUsaDetailExcel> list = berthMap.get(key);
            list.sort(Comparator.comparing(DockBerthUsaDetailExcel::getDockingTime));

            for (int i = 0; i < list.size(); i++) {
                DockBerthUsaDetailExcel current = list.get(i);
                result.add(current);

                if (i + 1 < list.size()) {
                    DockBerthUsaDetailExcel next = list.get(i + 1);
                    if (current.getOutBerthTime() != null && next.getDockingTime() != null &&
                            current.getOutBerthTime().before(next.getDockingTime())) {

                        DockBerthUsaDetailExcel pause = new DockBerthUsaDetailExcel();
                        pause.setShipName("作业间隔");
                        pause.setHbName(key);
                        pause.setDockingTime(current.getOutBerthTime());
                        pause.setOutBerthTime(next.getDockingTime());
                        pause.setPauseInterval(getDurationStr(current.getOutBerthTime(), next.getDockingTime()));
                        result.add(pause);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 耗时格式化
     */
    private String getDurationStr(Date start, Date end) {
        long diffMs = end.getTime() - start.getTime();
        long totalMinutes = diffMs / (1000 * 60);
        long days = totalMinutes / (60 * 24);
        long hours = (totalMinutes % (60 * 24)) / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d天%d小时%d分钟", days, hours, minutes);
    }


    // 判断两个时间段是否重叠
    private boolean isOverlap(LocalDateTime start1, LocalDateTime end1,
                              LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }


}
