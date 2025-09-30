package com.iwip.harbor.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import com.iwip.common.annotation.DataScope;
import com.iwip.common.constant.Constants;
import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.DateUtils;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.common.utils.ServletUtils;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.*;
import com.iwip.harbor.domain.excel.DockUnloadExcel;
import com.iwip.harbor.domain.screen.ScreenUnloadVo;
import com.iwip.harbor.domain.vo.DockUnloadVo;
import com.iwip.harbor.mapper.DockPlanAssistantMapper;
import com.iwip.harbor.mapper.DockPlanMapper;
import com.iwip.harbor.mapper.DockUnloadDetailMapper;
import com.iwip.harbor.mapper.DockUnloadWorkMapper;
import com.iwip.harbor.service.IDockBerthService;
import com.iwip.harbor.service.IDockPlanService;
import com.iwip.harbor.service.IDockUnloadWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.iwip.common.utils.DateUtils.*;
import static java.math.RoundingMode.HALF_UP;


/**
 * 卸货单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-02-05
 */
@Slf4j
@Service
public class DockUnloadWorkServiceImpl implements IDockUnloadWorkService
{
    @Autowired
    private DockUnloadWorkMapper dockUnloadWorkMapper;
    @Autowired
    private DockPlanMapper dockPlanMapper;
    @Autowired
    private IDockPlanService dockPlanService;

    @Autowired
    private DockUnloadDetailMapper dockUnloadDetailMapper;
    @Autowired
    private IDockBerthService dockBerthService;

    /**
     * 查询卸货单
     *
     * @param duId 卸货单主键
     * @return 卸货单
     */
    @Override
    public DockUnloadWork selectDockUnloadWorkByDuId(Long duId)
    {
        return dockUnloadWorkMapper.selectDockUnloadWorkByDuId(duId);
    }

    @Override
    @DataScope(deptAlias = "d")
    public Map<Object, Object> summaryCalculation(DockUnloadWork dockUnloadWork) {

        Map<Object, Object> map = new HashMap<>();

        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectDockUnloadWorkList(dockUnloadWork);
        if (unloadWorkList.isEmpty()) {
            return map;
        }
        DockPlan dockPlan = dockPlanService.selectDockPlanById(unloadWorkList.get(0).getPlanId());
        if (StringUtils.equals(dockPlan.getUnloadStatus(),"2")){

            BigDecimal tonnage = new BigDecimal(dockPlan.getTonnage());

            // 卸货总重量
            BigDecimal totalUnloadWeight = unloadWorkList.stream()
                    .map(DockUnloadWork::getTotalUnloadWeight)
                    .filter(Objects::nonNull) // 过滤掉 null值
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 卸货总件数
            BigDecimal totalUnloadNum = unloadWorkList.stream()
                    .map(DockUnloadWork::getUnloadNum)
                    .filter(Objects::nonNull) // 过滤掉 null值
                    .map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalMinutes = unloadWorkList.stream()
                    .mapToLong(work -> Duration.between(work.getStartTime(), work.getEndTime()).toMinutes())
                    .sum();

            // 累加暂停时间
            Duration totalPauseDuration = unloadWorkList.stream()
                    .filter(x -> x.getPauseInterval() != null)
                    .map(work -> parsePauseInterval(work.getPauseInterval())) // 用 lambda 代替 :: 解决爆红
                    .reduce(Duration.ZERO, Duration::plus);
            String totalPauseInterval = formatDuration(totalPauseDuration);

            // 累加有效时间
            Duration totalEffectiveDuration = unloadWorkList.stream()
                    .filter(x -> x.getEffectiveTime() != null)
                    .map(work -> parsePauseInterval(work.getEffectiveTime())) // 用 lambda 代替 :: 解决爆红
                    .reduce(Duration.ZERO, Duration::plus);
            String totalEffectiveTime = formatDuration(totalEffectiveDuration);

            // 有效时间
            BigDecimal totalEffectiveHours = BigDecimal.valueOf(totalEffectiveDuration.toMinutes()).divide(BigDecimal.valueOf(60), 2, HALF_UP);
            // 总小时
            BigDecimal totalHours = BigDecimal.valueOf(totalMinutes).divide(BigDecimal.valueOf(60), 2, HALF_UP);

            BigDecimal totalEffectiveRate = tonnage.divide(totalEffectiveHours, 2, HALF_UP);
            BigDecimal totalAvgDischargeRate = tonnage.divide(totalHours, 2, HALF_UP);

            map.put("totalUnloadWeight", totalUnloadWeight);         // 卸货总重量
            map.put("totalUnloadNum", totalUnloadNum);               // 卸货总件数
            map.put("totalAvgDischargeRate", totalAvgDischargeRate); // 平均卸率
            map.put("totalEffectiveRate", totalEffectiveRate);       // 有效卸率
            map.put("totalPauseInterval", totalPauseInterval);       // 累加暂停时间
            map.put("totalEffectiveTime", totalEffectiveTime);       // 累加有效时间
        }
        return map;
    }



    /**
     * 查询卸货单列表
     *
     * @param dockUnloadWork 卸货单
     * @return 卸货单
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<DockUnloadWork> selectDockUnloadWorkList(DockUnloadWork dockUnloadWork)
    {
        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectDockUnloadWorkList(dockUnloadWork);
        DockPlan dockPlan = dockPlanService.selectById(dockUnloadWork.getPlanId());
        Map<String,DockPlanAssistant> toolMap = new HashMap<>();
        DockPlanAssistant dpa = new DockPlanAssistant();
        dpa.setUsageUnit(dockPlan.getUsageUnit());
        dpa.setMaterialName(dockPlan.getMaterialName());
        toolMap.put("1",dpa);
        List<DockPlanAssistant> dockPlanAssistants = (List<DockPlanAssistant>) (dockPlan.getParams().get("subMaterial"));
        for(DockPlanAssistant dpaAssistant : dockPlanAssistants){
            toolMap.put(String.valueOf(dpaAssistant.getLoadSequence()),dpaAssistant);
        }
        for (DockUnloadWork unloadWork : unloadWorkList) {
            List<DockUnloadDetail> unloadDetailList = dockUnloadDetailMapper.selectDockUnloadDetailByDuId(unloadWork.getDuId());
            if (!unloadDetailList.isEmpty()){
                unloadWork.setStopCount(unloadDetailList.size());
            }
            DockPlanAssistant dockPlanAssistant = toolMap.get(unloadWork.getRemark01()==null?"1":unloadWork.getRemark01());
            Map<String,Object> map = new HashMap<>();
            map.put("UsageUnit",dockPlanAssistant.getUsageUnit());
            map.put("MaterialName",dockPlanAssistant.getMaterialName());
            unloadWork.setParams(map);
        }
        return unloadWorkList;
    }



    /**
     * 新增卸货单
     *
     * @param dockUnloadWork 卸货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDockUnloadWork(DockUnloadWork dockUnloadWork) {


        // 查询最新一条，如果状态不是已结束不可新增
        DockUnloadWork unloadDescWork = dockUnloadWorkMapper.selectDockUnloadWorkDesc(dockUnloadWork.getPlanId());
        if (unloadDescWork != null){
            if (!StringUtils.equals(unloadDescWork.getWorkType(),"2")){
                throw new ServiceException("上一条卸货单未完成 不可新增！");
            }
        }

        if (dockUnloadWork.getPlanId() == null) {
            throw new ServiceException("新增失败，未关联计划单");
        }
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(dockUnloadWork.getPlanId());
        if (dockPlan == null) {
            throw new ServiceException("未找到计划单，新增失败");
        }

        if (StringUtils.equals(dockPlan.getUnloadStatus(),"2")){
            throw new ServiceException("卸货单已完成，不可再新增");
        }

        DockBerth dockBerth = dockBerthService.selectDockBerthByDbId(dockPlan.getHbId());
        if (dockBerth == null){
            throw new ServiceException("操作失败：没有查询到泊位信息，请联系管理员");
        }

        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectUnloadWorkListByPlanId(dockUnloadWork.getPlanId());
        if (!unloadWorkList.isEmpty()){
            for (DockUnloadWork unloadWork : unloadWorkList) {
                if (unloadWork.getEndTime() != null){
                    boolean validate = isWithinRange(dockUnloadWork.getStartTime(), unloadWork.getStartTime(), unloadWork.getEndTime());
                    if (validate){
                        throw new ServiceException("班次开始时间不能在之前的班次的作业范围之内！");
                    }
                }else {
                    boolean validate = DateUtils.validateTime(dockUnloadWork.getStartTime(),unloadWork.getStartTime());
                    if (!validate){
                        throw new ServiceException("班次开始时间不能在之前的班次的作业开始时间之前！");
                    }
                }
            }
        }

        // 根据时间和班次查询是否已存在
        DockUnloadWork unloadWork = dockUnloadWorkMapper.selectUnloadWorkByTimeAndClass(dockUnloadWork.getPlanId(),dockUnloadWork.getClassTime(),dockUnloadWork.getClasses());
        if (unloadWork != null){
            throw new ServiceException(dockUnloadWork.getClassTime()+" - "+unloadWork.getClasses()+" 已存在，新增失败");
        }
        dockUnloadWork.setDelFlag("0");
        dockUnloadWork.setWorkType("0");
        // 新建卸货单时判断上一次的卸货单是否在暂停状态下结束的，如果是新建的卸货单状态也要为 暂停
        if (!unloadWorkList.isEmpty()) {
            if (Objects.equals(unloadWorkList.get(unloadWorkList.size() - 1).getPausedAtEnd(), Constants.YES)) {
                dockUnloadWork.setWorkType("1");
            }
        }
        dockUnloadWork.setUserId(SecurityUtils.getUserId());
        dockUnloadWork.setCreateBy(SecurityUtils.getNickName());
        dockUnloadWork.setCreateTime(LocalDateTime.now());
        dockUnloadWork.setDeptId(SecurityUtils.getDeptId());
        int row = dockUnloadWorkMapper.insertDockUnloadWork(dockUnloadWork);

        dockPlan.setUnloadStatus("1"); // 卸货状态（0未卸货 1正在卸货 2卸货完成）

        int i = dockPlanService.updateStatus(dockPlan);

        // 新增暂停日志
        if (StringUtils.equals("1",dockUnloadWork.getWorkType())){
            DockUnloadDetail unloadDetail = new DockUnloadDetail();
            if (unloadDescWork != null) {
                DockUnloadDetail dockUnloadDetail = dockUnloadDetailMapper.selectDetailDescByDuId(unloadDescWork.getDuId());
                unloadDetail.setDuId(dockUnloadWork.getDuId());
                unloadDetail.setStartTime(dockUnloadWork.getStartTime().withSecond(0));
                unloadDetail.setReason(dockUnloadDetail != null ? dockUnloadDetail.getReason() : null);
                unloadDetail.setCreateTime(LocalDateTime.now());
                unloadDetail.setDeptId(SecurityUtils.getDeptId());
                dockUnloadDetailMapper.insertDockUnloadDetail(unloadDetail);
            }

            dockBerth.setBerthStatus("3"); // 暂停
        }else {
            dockBerth.setBerthStatus("1"); // 进行中
        }

        // 开始任务不读取
//        dockLoadRateService.startTask(dockUnloadWork.getDuId());

        dockBerthService.updateDockBerthStatus(dockBerth);

        return row;
    }

    /**
     * 修改卸货单
     *
     * @param dockUnloadWork 卸货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDockUnloadWork(DockUnloadWork dockUnloadWork)
    {

//        if (StringUtils.equals(dockUnloadWork.getWorkType(),"2")){
//            throw new ServiceException("卸货单已结束，不可再修改!");
//        }
        if (dockUnloadWork.getPlanId() != null){
            DockPlan dockPlan = dockPlanMapper.selectDockPlanById(dockUnloadWork.getPlanId());
            if (dockPlan == null){
                throw new ServiceException("未找到计划单，修改失败");
            }
//            if (StringUtils.equals(dockPlan.getUnloadStatus(),"2")){
//                throw new ServiceException("卸货单已完成，不可再修改");
//            }
        }else {
            throw new ServiceException("新增失败，未关联计划单");
        }
        if(dockUnloadWork.getStartTime()!=null&&dockUnloadWork.getEndTime()!=null
                &&dockUnloadWork.getStartTime().isAfter(dockUnloadWork.getEndTime())){
            throw new ServiceException("结束时间必须在开始时间之后");
        }
//        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectUnloadWorkListByPlanId(dockUnloadWork.getPlanId());
//        if (!unloadWorkList.isEmpty()){
//            for (DockUnloadWork unloadWork : unloadWorkList) {
//                if (unloadWork.getEndTime() != null){
//                    boolean validate = isWithinRange(dockUnloadWork.getStartTime(), unloadWork.getStartTime(), unloadWork.getEndTime());
//                    if (validate){
//                        if (Objects.equals(unloadWork.getDuId(), dockUnloadWork.getDuId())) {
//                            continue;
//                        }
//                        throw new ServiceException("班次开始时间不能在之前的班次的作业范围之内！");
//                    }
//                }else {
//                    boolean validate = DateUtils.validateTime(dockUnloadWork.getStartTime(),unloadWork.getStartTime());
//                    if (!validate){
//                        if (Objects.equals(unloadWork.getDuId(), dockUnloadWork.getDuId())) {
//                            continue;
//                        }
//                        throw new ServiceException("班次开始时间不能在之前的班次的作业开始时间之前！");
//                    }
//                }
//
//            }
//        }
        // 根据时间和班次查询是否已存在
        DockUnloadWork unloadWork = dockUnloadWorkMapper.selectUnloadWorkByTimeAndClass(dockUnloadWork.getPlanId(),dockUnloadWork.getClassTime(),dockUnloadWork.getClasses());
        if (unloadWork != null){
            if (!Objects.equals(unloadWork.getDuId(), dockUnloadWork.getDuId())){
                throw new ServiceException(dockUnloadWork.getClassTime()+" - "+unloadWork.getClasses()+" 已存在，新增失败");
            }
        }

        dockUnloadWork.setUpdateBy(SecurityUtils.getUsername());
        dockUnloadWork.setUpdateTime(LocalDateTime.now());

        return dockUnloadWorkMapper.updateDockUnloadWork(dockUnloadWork);
    }

    /**
     * 批量删除卸货单
     *
     * @param duIds 需要删除的卸货单主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDockUnloadWorkByDuIds(Long[] duIds)
    {
        if (duIds.length == 0){
            throw new ServiceException("参数为空，请选择参数之后进行删除操作！");
        }
        int row = 0;
        for (Long duId : duIds) {
            DockUnloadWork dockUnloadWork = dockUnloadWorkMapper.selectDockUnloadWorkByDuId(duId);

            DockPlan dockPlan = dockPlanService.selectDockPlanById(dockUnloadWork.getPlanId());
            if (dockPlan == null){
                throw new ServiceException("没有查询到计划单");
            }
            if (StringUtils.equals(dockPlan.getUnloadStatus(), "2")) {
                throw new ServiceException("删除失败，卸货单已完成卸货，不可删除");
            }
            if (StringUtils.equals(dockUnloadWork.getDelFlag(), "1")) {
                throw new ServiceException("删除失败，该条数据已被删除！");
            }
            // 如果不是已结束，不可删除
//            if (!StringUtils.equals(dockUnloadWork.getWorkType(), "2")){
//                throw new ServiceException("卸货单还未结束，不可删除！");
//            }
            dockUnloadWork.setDelFlag("1");
            row += dockUnloadWorkMapper.removeDockPierByDpId(dockUnloadWork);
            List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectUnloadWorkListByPlanId(dockUnloadWork.getPlanId());
            // 卸货状态（0未卸货 1正在卸货 2卸货完成）
            if (unloadWorkList.isEmpty()){
                dockPlan.setUnloadStatus("0");  // 否
            }else {
                dockPlan.setUnloadStatus("1");  // 是
            }
            // 修改计划单卸货状态
            int i = dockPlanService.updateStatus(dockPlan);
            // 删除日志
//            List<DockUnloadDetail> detailList = dockUnloadDetailMapper.selectDockUnloadDetailByDuId(duId);
//            if (!detailList.isEmpty()){
//                Long[] ids = detailList.stream().map(DockUnloadDetail::getDudId).toList().toArray(Long[]::new);
//                int i = dockUnloadDetailMapper.deleteDockUnloadDetailByDudIds(ids);
//            }

            // 删除卸货率
//            List<DockLoadRate> loadRateList = dockLoadRateService.selectDockLoadRateListById(duId);
//            if (!loadRateList.isEmpty()){
//                Long[] ids = loadRateList.stream().map(DockLoadRate::getId).toList().toArray(Long[]::new);
//                int i = dockLoadRateService.deleteDockLoadRateByIds(ids);
//            }
        }
        return row;
    }

    /**
     * 删除卸货单信息
     *
     * @param duId 卸货单主键
     * @return 结果
     */
    @Override
    public int deleteDockUnloadWorkByDuId(Long duId)
    {
        DockUnloadWork dockUnloadWork = dockUnloadWorkMapper.selectDockUnloadWorkByDuId(duId);
        if (StringUtils.equals(dockUnloadWork.getDelFlag(), "1")) {
            throw new ServiceException("删除失败，该条数据已被删除！");
        }
        dockUnloadWork.setDelFlag("1");
        return dockUnloadWorkMapper.removeDockPierByDpId(dockUnloadWork);
    }


    /**
     * 暂停卸货单信息
     *
     * @param dockUnloadVo 卸货单主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int stop(DockUnloadVo dockUnloadVo) {
        DockUnloadWork unloadWork = dockUnloadWorkMapper.selectDockUnloadWorkByDuId(dockUnloadVo.getDuId());
        if (unloadWork == null){
            throw new ServiceException("操作失败：没有查询到卸货单信息，请联系管理员");
        }
        DockPlan dockPlan = dockPlanService.selectDockPlanById(unloadWork.getPlanId());
        if (dockPlan == null){
            throw new ServiceException("操作失败：没有查询到计划单信息，请联系管理员");
        }
        DockBerth dockBerth = dockBerthService.selectDockBerthByDbId(dockPlan.getHbId());
        if (dockBerth == null){
            throw new ServiceException("操作失败：没有查询到泊位信息，请联系管理员");
        }
        if (StringUtils.equals("1",unloadWork.getWorkType())){
            throw new ServiceException("操作失败：卸货单已经是暂停状态，不可再操作暂停");
        }
        /**
         * 如果已经有暂停记录，校验当前暂停时间不能在之前的日志暂停时间之内
         * 如果没有暂停记录，校验当前暂停时间不能再卸货单开始时间之前，结束时间之后
         */
        List<DockUnloadDetail> unloadDetailList = dockUnloadDetailMapper.selectDockUnloadDetailByDuId(unloadWork.getDuId());
        if (!unloadDetailList.isEmpty()){
            for (DockUnloadDetail dockUnloadDetail : unloadDetailList) {
                if (dockUnloadDetail.getEndTime() != null){
                    boolean validate = isWithinRange(dockUnloadVo.getStartTime(), dockUnloadDetail.getStartTime(), dockUnloadDetail.getEndTime());
                    if (validate){
                        throw new ServiceException("暂停时间不能在日志的暂停时间范围之内！");
                    }
                }
            }
        }
        boolean validate = DateUtils.validateTime(dockUnloadVo.getStartTime(),unloadWork.getStartTime());
        if (!validate){
            throw new ServiceException("暂停时间不能在班次开始时间之前！");
        }
        unloadWork.setWorkType("1"); // 暂停
        dockUnloadWorkMapper.updateDockUnloadWork(unloadWork);
        // 如果是待离泊之后 不执行泊位判断 只有不在 5~9 范围内的状态才修改泊位状态
        if (!List.of( "6", "7", "8", "9").contains(dockPlan.getStatus())) {
            dockBerth.setBerthStatus("3"); // 暂停
        }else {
            // 结束，判断泊位下是否有多个卸货单，如果有多个卸货单在卸货，泊位状态就是进行中 如果只有一个在卸货，并且是待离港之后就是空闲
            DockPlan plan = new DockPlan();
            plan.setHbId(dockPlan.getHbId());
            plan.setId(dockPlan.getId());
            List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanByStatus(plan);
            boolean statusFlag = dockPlanList != null && !dockPlanList.isEmpty();
            if (statusFlag) {
                dockBerth.setBerthStatus("1"); // 进行中
            } else {
                // 只有不在 6~9 范围内的状态才修改泊位状态
                dockBerth.setBerthStatus("0"); // 空闲
            }
        }
        dockBerthService.updateDockBerthStatus(dockBerth);
        // 根据主键查询子表
        DockUnloadDetail unloadDetail = new DockUnloadDetail();
        unloadDetail.setDuId(dockUnloadVo.getDuId());
        unloadDetail.setStartTime(dockUnloadVo.getStartTime());
        unloadDetail.setReason(dockUnloadVo.getPauseReason());
        unloadDetail.setCreateTime(LocalDateTime.now());
        unloadDetail.setCreateBy(dockUnloadVo.getOperateBy());
//        if (StringUtils.isNotBlank(dockUnloadVo.getRemark())){
//            unloadDetail.setRemark("暂停备注："+ dockUnloadVo.getRemark()+"；");
//        }
        unloadDetail.setRemark(dockUnloadVo.getRemark());
        unloadDetail.setDeptId(SecurityUtils.getDeptId());
        dockUnloadDetailMapper.insertDockUnloadDetail(unloadDetail);
        return 1;
    }

    /**
     * 恢复
     * @param dockUnloadVo 卸货单
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recover(DockUnloadVo dockUnloadVo) {
        DockUnloadWork unloadWork = dockUnloadWorkMapper.selectDockUnloadWorkByDuId(dockUnloadVo.getDuId());
        if (unloadWork == null){
            throw new ServiceException("操作失败：没有查询到卸货单信息，请联系管理员");
        }
        if (StringUtils.equals("0",unloadWork.getWorkType())){
            throw new ServiceException("操作失败，只有暂停状态才可以进行恢复操作");
        }
        DockPlan dockPlan = dockPlanService.selectDockPlanById(unloadWork.getPlanId());
        if (dockPlan == null){
            throw new ServiceException("操作失败：没有查询到计划单信息，请联系管理员");
        }
        DockBerth dockBerth = dockBerthService.selectDockBerthByDbId(dockPlan.getHbId());
        if (dockBerth == null){
            throw new ServiceException("操作失败：没有查询到泊位信息，请联系管理员");
        }
        unloadWork.setWorkType("0"); // 进行中
        dockUnloadWorkMapper.updateDockUnloadWork(unloadWork);
        // 如果是待离泊之后 不执行泊位判断
        // 只有不在 6~9 范围内的状态才修改泊位状态
        // 结束，判断泊位下是否有多个卸货单，如果有多个卸货单在卸货，泊位状态就是进行中 如果只有一个在卸货，并且是待离港之后就是空闲
        DockPlan plan = new DockPlan();
        plan.setHbId(dockPlan.getHbId());
        plan.setId(dockPlan.getId());
        List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanByStatus(plan);
        boolean statusFlag = dockPlanList != null && !dockPlanList.isEmpty();
        if (statusFlag) {
            dockBerth.setBerthStatus("1"); // 进行中
        } else {
            // 如果是待离泊之后 不执行泊位判断 只有不在 5~9 范围内的状态才修改泊位状态
            if (List.of( "6", "7", "8", "9").contains(dockPlan.getStatus())) {
                dockBerth.setBerthStatus("0"); // 恢复
            }else {
                dockBerth.setBerthStatus("1"); // 空闲
            }
        }
        dockBerthService.updateDockBerthStatus(dockBerth);
        // 根据主键降序查询最新一条信息
        DockUnloadDetail unloadDetail = dockUnloadDetailMapper.selectDetailDescByDuId(dockUnloadVo.getDuId());
        if (unloadDetail != null) {
            if (dockUnloadVo.getEndTime().isBefore(unloadDetail.getStartTime())) {
                throw new ServiceException("结束时间不能早于开始时间！");
            }
            // 根据主键查询子表
            unloadDetail.setEndTime(dockUnloadVo.getEndTime());
            LocalDateTime startTime = unloadDetail.getStartTime();
            String pauseInterval = DateUtils.timeDiff(startTime, dockUnloadVo.getEndTime());
            unloadDetail.setPauseInterval(pauseInterval); // 暂停间隔
            unloadDetail.setUpdateTime(LocalDateTime.now());
            unloadDetail.setUpdateBy(dockUnloadVo.getOperateBy());
            if (StringUtils.isNotBlank(dockUnloadVo.getRemark())) {
                unloadDetail.setRemark(unloadDetail.getRemark() + "\n 恢复备注" + dockUnloadVo.getRemark() + "；");
            }
            dockUnloadDetailMapper.updateDockUnloadDetail(unloadDetail);
        }
        return 1;
    }




    /**
     * 新-结束功能
     * @param dockUnloadVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int jobOver(DockUnloadVo dockUnloadVo) {
        // 查询卸货单
        DockUnloadWork unloadWork = dockUnloadWorkMapper.selectDockUnloadWorkByDuId(dockUnloadVo.getDuId());
        if (unloadWork == null) {
            throw new ServiceException("操作失败：没有查询到卸货单信息，请联系管理员");
        }
        if (StringUtils.equals("2", unloadWork.getWorkType())){
            throw new ServiceException("操作失败，已结束的单据不可以再次结束");
        }
        boolean flag = validateTime(unloadWork.getStartTime(), dockUnloadVo.getEndTime());
        if (flag){
            throw new ServiceException("结束时间不能早于开始时间");
        }
        // 查询计划单
        DockPlan dockPlan = dockPlanService.selectDockPlanById(unloadWork.getPlanId());
        if (dockPlan == null) {
            throw new ServiceException("未找到对应的计划单信息！");
        }
        DockBerth dockBerth = dockBerthService.selectDockBerthByDbId(dockPlan.getHbId());
        if (dockBerth == null){
            throw new ServiceException("操作失败：没有查询到泊位信息，请联系管理员");
        }
        // 查询日志
        List<DockUnloadDetail> unloadDetailList = dockUnloadDetailMapper.selectDockUnloadDetailByDuId(dockUnloadVo.getDuId());
        // 处理暂停状态的结束逻辑
        if ("1".equals(unloadWork.getWorkType())) {
            for (DockUnloadDetail detail : unloadDetailList) {
                if (detail.getEndTime() == null) {
                    detail.setEndTime(dockUnloadVo.getEndTime());
                    detail.setPauseInterval(DateUtils.timeDiff(detail.getStartTime(), dockUnloadVo.getEndTime()));
                    dockUnloadDetailMapper.updateDockUnloadDetail(detail);
                }
            }
            // 卸货单在暂停状态下直接结束时添加标识
            unloadWork.setPausedAtEnd(Constants.YES);
        }

        // 不是最后一班
        // 处理自动生成的新卸货单
        if (StringUtils.isNotBlank(dockUnloadVo.getIsOver()) && "0".equals(dockUnloadVo.getIsOver())) {

            // APP端如果不是最后一班不会生成新的卸货单
            String osHeader = ServletUtils.getRequest().getHeader("Os");
            if (!Objects.equals(osHeader, "APP")) {
                DockUnloadWork autoUnloadWork = new DockUnloadWork();
                autoUnloadWork.setStartTime(dockUnloadVo.getEndTime());
                autoUnloadWork.setWorkType("1".equals(unloadWork.getWorkType()) ? "1" : "0");
                autoUnloadWork.setPlanId(dockPlan.getId());
                autoUnloadWork.setCreateTime(LocalDateTime.now());
                autoUnloadWork.setDelFlag("0");
                // 生成新单据
                createUnloadWork(autoUnloadWork);
                // 新增暂停日志
                if (StringUtils.equals("1",unloadWork.getWorkType())){
                    DockUnloadDetail unloadDetail = new DockUnloadDetail();
                    unloadDetail.setDuId(autoUnloadWork.getDuId());
                    unloadDetail.setStartTime(dockUnloadVo.getEndTime().withSecond(0));
                    unloadDetail.setReason(dockUnloadVo.getPauseReason());
                    unloadDetail.setCreateTime(LocalDateTime.now());
                    dockUnloadDetailMapper.insertDockUnloadDetail(unloadDetail);
                    dockBerth.setBerthStatus("3"); // 暂停
                }else {
                    dockBerth.setBerthStatus("1"); // 进行中
                }
            }

            // 最后一班
        } else if(StringUtils.isNotBlank(dockUnloadVo.getIsOver()) && "1".equals(dockUnloadVo.getIsOver())){
            // 结束，判断泊位下是否有多个卸货单，如果有多个卸货单在卸货，泊位状态就是进行中 如果只有一个在卸货，并且是待离港之后就是空闲
            DockPlan plan = new DockPlan();
            plan.setHbId(dockPlan.getHbId());
            plan.setId(dockPlan.getId());
            List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanByStatus(plan);
            boolean statusFlag = dockPlanList != null && !dockPlanList.isEmpty();
            if (statusFlag) {
                dockBerth.setBerthStatus("1"); // 进行中
            } else {
                // 如果是待离泊之后 不执行泊位判断
                if (List.of("6", "7", "8", "9").contains(dockPlan.getStatus())) {
                    // 只有不在 6~9 范围内的状态才修改泊位状态
                    dockBerth.setBerthStatus("0"); // 空闲
                }else {
                    dockBerth.setBerthStatus("1"); // 占用
                }
            }
            dockPlan.setUpdateBy(SecurityUtils.getNickName());
            dockPlan.setUpdateTime(LocalDateTime.now());
            dockPlan.setStatus("5");
            dockPlan.setEndTime(dockUnloadVo.getEndTime());
            dockPlanMapper.updateStatus(dockPlan);
        }

        dockBerthService.updateDockBerthStatus(dockBerth);

        // 更新卸货单信息
        unloadWork.setWorkType("2");
        unloadWork.setEndTime(dockUnloadVo.getEndTime());
        unloadWork.setUnloadNum(dockUnloadVo.getUnloadNum());
        unloadWork.setTotalUnloadWeight(dockUnloadVo.getTotalUnloadWeight());
        unloadWork.setRemark(dockUnloadVo.getRemark());
        unloadWork.setWorkEquipment(dockUnloadVo.getWorkEquipment());

        // 计算有效工作时间
        Duration workDuration = Duration.between(unloadWork.getStartTime(), unloadWork.getEndTime());
        Duration pauseDuration = unloadDetailList.stream()
                .map(detail -> parsePauseInterval(detail.getPauseInterval()))
                .reduce(Duration.ZERO, Duration::plus);
        Duration effectiveWorkDuration = workDuration.minus(pauseDuration);
        unloadWork.setEffectiveTime(formatDuration(effectiveWorkDuration));

//        // 计划单下所有的卸货单
//        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectUnloadWorkListByPlanId(unloadWork.getPlanId());
//
//        // 校验同一计划单的卸货单只能填写件数或吨数
//        validateUnloadWorkDataConsistency(unloadWorkList, dockUnloadVo);

        // 计算卸率
        if (dockUnloadVo.getTotalUnloadWeight() != null && dockUnloadVo.getTotalUnloadWeight().compareTo(BigDecimal.ZERO) > 0) {
            calculateUnloadRate(unloadWork, workDuration, effectiveWorkDuration, dockUnloadVo.getTotalUnloadWeight());
        }

        return dockUnloadWorkMapper.updateDockUnloadWork(unloadWork);
    }

    /**
     * 计算卸率
     */
    private void calculateUnloadRate(DockUnloadWork unloadWork, Duration workDuration, Duration effectiveWorkDuration, BigDecimal totalUnloadWeight) {
        // 转换时间为小时
        BigDecimal totalHours = BigDecimal.valueOf(workDuration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal effectiveHours = BigDecimal.valueOf(effectiveWorkDuration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        // 避免除零错误
        if (effectiveHours.compareTo(BigDecimal.ZERO) > 0) {
            unloadWork.setEffectiveRate(totalUnloadWeight.divide(effectiveHours, 2, RoundingMode.HALF_UP));
        } else {
            unloadWork.setEffectiveRate(BigDecimal.ZERO);
        }

        if (totalHours.compareTo(BigDecimal.ZERO) > 0) {
            unloadWork.setAvgDischargeRate(totalUnloadWeight.divide(totalHours, 2, RoundingMode.HALF_UP));
        } else {
            unloadWork.setAvgDischargeRate(BigDecimal.ZERO);
        }
    }


    /**
     * 校验同一计划单下的卸货单填写规则
     */
    private void validateUnloadWorkDataConsistency(List<DockUnloadWork> unloadWorkList , DockUnloadVo dockUnloadVo) {
        if (!unloadWorkList.isEmpty()) {
            boolean hasUnloadNum = unloadWorkList.stream().anyMatch(x -> x.getUnloadNum() != null);
            boolean hasTotalUnloadWeight = unloadWorkList.stream().anyMatch(x -> x.getTotalUnloadWeight() != null);

            if (dockUnloadVo.getTotalUnloadWeight() != null && hasUnloadNum) {
                throw new ServiceException("很抱歉，同一计划单下的所有卸货单只能填写件数或吨数，不能同时填写两者。");
            }

            if (dockUnloadVo.getUnloadNum() != null && hasTotalUnloadWeight) {
                throw new ServiceException("很抱歉，同一计划单下的所有卸货单只能填写件数或吨数，不能同时填写两者。");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int jobComplete(Long planId) {
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(planId);
        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectUnloadWorkListByPlanId(planId);

        if (dockPlan.getTonnage() == null) {
            throw new ServiceException("计划单没有填写总吨数，不可操作此功能！");
        }
        if (unloadWorkList.isEmpty()) {
            throw new ServiceException("没有卸货单，不可操作此功能！");
        }

        List<DockUnloadWork> unfinishedWorkList = unloadWorkList.stream()
                .filter(x -> !"2".equals(x.getWorkType()))
                .toList();
        if (!unfinishedWorkList.isEmpty()) {
            throw new ServiceException("卸货单有未结束的单据，请结束之后再操作此功能！");
        }

        BigDecimal totalUnloadNum = BigDecimal.ZERO;
        for (DockUnloadWork unloadWork : unloadWorkList) {
            List<DockUnloadDetail> unloadDetailList =
                    dockUnloadDetailMapper.selectUnloadDetailByRecordByDuId(unloadWork.getDuId());

            if (!unloadDetailList.isEmpty()) {
                Duration workDuration = Duration.between(unloadWork.getStartTime(), unloadWork.getEndTime());
                Duration pauseDuration = unloadDetailList.stream()
                        .map(detail -> parsePauseInterval(detail.getPauseInterval()))
                        .reduce(Duration.ZERO, Duration::plus);
                Duration effectiveWorkDuration = workDuration.minus(pauseDuration);
                unloadWork.setEffectiveTime(formatDuration(effectiveWorkDuration));
            }

            if (unloadWork.getUnloadNum() != null) {
                totalUnloadNum = totalUnloadNum.add(new BigDecimal(unloadWork.getUnloadNum()));
            }
        }

        BigDecimal tonnage = new BigDecimal(dockPlan.getTonnage());
        if (totalUnloadNum.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tonnagePerUnit = tonnage.divide(totalUnloadNum, 2, RoundingMode.HALF_UP);

            for (DockUnloadWork unloadWork : unloadWorkList) {
                BigDecimal unloadNum = new BigDecimal(unloadWork.getUnloadNum());
                BigDecimal tonnageClass = tonnagePerUnit.multiply(unloadNum);

                BigDecimal totalHours = durationToHours(unloadWork.getStartTime(), unloadWork.getEndTime());
                BigDecimal effectiveHours = durationToHours(parsePauseInterval(unloadWork.getEffectiveTime()));

                unloadWork.setEffectiveRate(effectiveHours.compareTo(BigDecimal.ZERO) > 0
                        ? tonnageClass.divide(effectiveHours, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);

                unloadWork.setAvgDischargeRate(totalHours.compareTo(BigDecimal.ZERO) > 0
                        ? tonnageClass.divide(totalHours, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);

                dockUnloadWorkMapper.updateDockUnloadWork(unloadWork);
            }
        } else {
            for (DockUnloadWork unloadWork : unloadWorkList) {
                Duration workDuration = Duration.between(unloadWork.getStartTime(), unloadWork.getEndTime());
                Duration effectiveWorkDuration = parsePauseInterval(unloadWork.getEffectiveTime());
                if (unloadWork.getTotalUnloadWeight() != null && unloadWork.getTotalUnloadWeight().compareTo(BigDecimal.ZERO) > 0) {
                    calculateUnloadRate(unloadWork, workDuration, effectiveWorkDuration, unloadWork.getTotalUnloadWeight());
                }
                dockUnloadWorkMapper.updateDockUnloadWork(unloadWork);
            }
        }

        Duration totalEffectiveTime = unloadWorkList.stream()
                .map(work -> parsePauseInterval(work.getEffectiveTime()))
                .reduce(Duration.ZERO, Duration::plus);

        BigDecimal totalEffectiveHours = durationToHours(totalEffectiveTime);
        BigDecimal totalEffectiveRate = totalEffectiveHours.compareTo(BigDecimal.ZERO) > 0
                ? tonnage.divide(totalEffectiveHours, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        dockPlan.setEffectiveTime(formatDuration(totalEffectiveTime));
        dockPlan.setEffectiveRate(totalEffectiveRate);
        dockPlan.setUnloadStatus("2");

        return dockPlanService.updateDockPlanRate(dockPlan);
    }


    @Override
    public int cancelComplete(Long planId) {
        // 计划单
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(planId);

        if (!StringUtils.equals("2", dockPlan.getUnloadStatus())){
            throw new ServiceException("计划单卸货状态不是已完成，不可操作");
        }
        dockPlan.setUnloadStatus("3"); // 完成又取消
        dockPlan.setEffectiveRate(null);
        dockPlan.setAvgDischargeRate(null);
        // 修改主表状态
        return dockPlanService.updateDockPlan(dockPlan);
    }

    @Override
    public void createUnloadWork(DockUnloadWork unloadWork) {
        int i = dockUnloadWorkMapper.insertDockUnloadWork(unloadWork);
    }

    /**
     * app端修改卸货单
     * @param dockUnloadVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int appUpdateUnload(DockUnloadVo dockUnloadVo) {

        DockUnloadWork unloadWork = dockUnloadWorkMapper.selectDockUnloadWorkByDuId(dockUnloadVo.getDuId());

        if (!StringUtils.equals("2",unloadWork.getWorkType())){
            throw new ServiceException("操作失败，只有已结束的卸货单才可以修改");
        }

        // 查询计划单
        DockPlan dockPlan = dockPlanService.selectDockPlanById(unloadWork.getPlanId());
        if (dockPlan == null) {
            throw new ServiceException("未找到对应的计划单信息！");
        }
//        // 计划单下所有的卸货单
//        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectUnloadWorkListByPlanId(unloadWork.getPlanId());
//        // 校验同一计划单的卸货单只能填写件数或吨数
//        validateUnloadWorkDataConsistency(unloadWorkList, dockUnloadVo);


        unloadWork.setUnloadNum(dockUnloadVo.getUnloadNum());
        unloadWork.setWorkEquipment(dockUnloadVo.getWorkEquipment());
        unloadWork.setTotalUnloadWeight(dockUnloadVo.getTotalUnloadWeight());
        unloadWork.setRemark(dockUnloadVo.getRemark());

        // 有效时间
        Duration effectiveWorkDuration = parsePauseInterval(unloadWork.getEffectiveTime());
        // 总时间
        Duration workDuration = Duration.between(unloadWork.getStartTime(), unloadWork.getEndTime());
        // 计算卸率
        if (dockUnloadVo.getTotalUnloadWeight() != null && dockUnloadVo.getTotalUnloadWeight().compareTo(BigDecimal.ZERO) > 0) {
            calculateUnloadRate(unloadWork, workDuration, effectiveWorkDuration, dockUnloadVo.getTotalUnloadWeight());
        }
        return dockUnloadWorkMapper.updateDockUnloadWork(unloadWork);
    }


    /**
     * 解析暂停间隔字符串 (HH:mm:ss) 转换为 Duration
     */
    public static Duration parsePauseInterval(String pauseInterval) {
        String[] parts = pauseInterval.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    /**
     * 格式化 Duration 为 HH:mm:ss
     */
    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public List<DockUnloadWork> selectUnloadWorkListByPlanId(Long planId) {
        return dockUnloadWorkMapper.selectUnloadWorkListByPlanId(planId);
    }


    /**
     * 导出
     * @param dockUnloadWork
     * @return
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<DockUnloadExcel> exportDockUnloadWorkList(DockUnloadWork dockUnloadWork) {

        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectDockUnloadWorkList(dockUnloadWork);
        DockPlan dockPlan = dockPlanService.selectDockPlanById(unloadWorkList.get(0).getPlanId());

        if (!StringUtils.equals("2",dockPlan.getUnloadStatus())){
            throw new ServiceException("卸货单导出失败，请检查是否有未完成的卸货单");
        }
        String tonnage = dockPlan.getTonnage();
        // 吨数
        BigDecimal tonnageDecimal = new BigDecimal(tonnage);

        // 总件数
        BigDecimal totalUnloadNum = unloadWorkList.stream()
                .map(DockUnloadWork::getUnloadNum)
                .filter(Objects::nonNull)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUnloadWeightSum = BigDecimal.ZERO;
        BigDecimal totalUnloadNumTonSum = BigDecimal.ZERO;
        BigDecimal totalUnloadRatioSum = BigDecimal.ZERO;
        BigDecimal totalunloadNumSum = BigDecimal.ZERO;


        List<DockUnloadExcel> excelUnloadList = new ArrayList<>();

        int count = 0;
        for (DockUnloadWork unloadWork : unloadWorkList) {

            DockUnloadExcel unloadExcel = new DockUnloadExcel();

            BeanUtils.copyProperties(unloadWork, unloadExcel);

            unloadExcel.setSort(++count);                           // 序号
            unloadExcel.setMaterialName(dockPlan.getMaterialName());    // 品名
            unloadExcel.setBerthName(dockPlan.getHbName());             // 卸货泊位
            unloadExcel.setTonnage(dockPlan.getTonnage());              // 吨位
            unloadExcel.setArrivalTime(dockPlan.getArrivalTime());      // 到港时间
            unloadExcel.setDockingTime(dockPlan.getDockingTime());      // 靠泊时间
            unloadExcel.setOperationTime(dockPlan.getOperationTime());  // 作业时间
            unloadExcel.setEndTime(unloadWork.getEndTime());              // 结束时间
            unloadExcel.setOutBerthTime(dockPlan.getOutBerthTime());    // 离泊时间

            // 有效时间
            Duration duration = parsePauseInterval(unloadWork.getEffectiveTime());
            BigDecimal effectiveHours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, HALF_UP);

            // 全部时间
            String totalTime = DateUtils.timeDiff(unloadWork.getStartTime(), unloadWork.getEndTime());
            Duration totalDuration = parsePauseInterval(totalTime);
            BigDecimal totalHours = BigDecimal.valueOf(totalDuration.toMinutes()).divide(BigDecimal.valueOf(60), 2, HALF_UP);

            BigDecimal unloadRatio;
            BigDecimal effectiveRate;
            BigDecimal avgDischargeRate;
            // 件数的情况
            if (totalUnloadNum.compareTo(BigDecimal.ZERO) > 0) {

                // 件数折算吨数：总吨数 / 总件数 * 卸货数量
                BigDecimal unloadNumTon = tonnageDecimal.divide(totalUnloadNum, 2, HALF_UP)
                        .multiply(BigDecimal.valueOf(unloadWork.getUnloadNum()));

                BigDecimal unloadNum = new BigDecimal(unloadWork.getUnloadNum());

                // 当天卸货比重 = 当天卸货件数 / 总件数
                unloadRatio = unloadNum.divide(totalUnloadNum, 2, HALF_UP).multiply(BigDecimal.valueOf(100));

                // 有效卸率 = 当前卸货数量（吨） / 有效时间
                effectiveRate = unloadNum.divide(effectiveHours, 2, HALF_UP);
                avgDischargeRate = unloadNum.divide(totalHours, 2, HALF_UP);

                totalunloadNumSum = totalunloadNumSum.add(unloadNum);
                totalUnloadNumTonSum = totalUnloadNumTonSum.add(unloadNumTon);

                unloadExcel.setUnloadNumTonDay(unloadNumTon);                // 当天卸货件数（吨）
                unloadExcel.setUnloadNum(unloadNum);                // 当天卸货件数（吨）
                unloadExcel.setPackageNum(totalUnloadNum);          // 总件数
            } else {

                if (unloadWork.getTotalUnloadWeight() == null || unloadWork.getTotalUnloadWeight().compareTo(BigDecimal.ZERO) == 0){
                    continue;
                }
                // 吨数的情况
                // 当天卸货比重 = 当天卸货量 / 总吨量
                unloadRatio = unloadWork.getTotalUnloadWeight().divide(tonnageDecimal, 2, HALF_UP).multiply(BigDecimal.valueOf(100));

                // 有效卸率 = 当前卸货数量（吨） / 有效时间
                effectiveRate = unloadWork.getTotalUnloadWeight().divide(effectiveHours, 2, HALF_UP);
                avgDischargeRate = unloadWork.getTotalUnloadWeight().divide(totalHours, 2, HALF_UP);

                totalUnloadWeightSum = totalUnloadWeightSum.add(unloadWork.getTotalUnloadWeight());
            }
            unloadExcel.setUnloadRatio(unloadRatio+"%");                // 当天卸货比重
            unloadExcel.setEffectiveRate(effectiveRate);                // 有效卸率（吨）
            unloadExcel.setAvgDischargeRate(avgDischargeRate);                // 平均卸率（吨）

            totalUnloadRatioSum = totalUnloadRatioSum.add(unloadRatio);     // 卸货比重

            excelUnloadList.add(unloadExcel);
        }

        long totalMinutes = unloadWorkList.stream()
                .mapToLong(work -> Duration.between(work.getStartTime(), work.getEndTime()).toMinutes())
                .sum();

        // 累加有效时间
        Duration totalEffectiveDuration = unloadWorkList.stream()
                .filter(x -> x.getEffectiveTime() != null)
                .map(work -> parsePauseInterval(work.getEffectiveTime())) // 用 lambda 代替 :: 解决爆红
                .reduce(Duration.ZERO, Duration::plus);
        String totalEffectiveTime = formatDuration(totalEffectiveDuration);

        // 有效时间
        BigDecimal totalEffectiveHours = BigDecimal.valueOf(totalEffectiveDuration.toMinutes()).divide(BigDecimal.valueOf(60), 2, HALF_UP);
        // 总小时
        BigDecimal totalHours = BigDecimal.valueOf(totalMinutes).divide(BigDecimal.valueOf(60), 2, HALF_UP);

        BigDecimal totalEffectiveRate = tonnageDecimal.divide(totalEffectiveHours, 2, HALF_UP);
        BigDecimal totalAvgDischargeRate = tonnageDecimal.divide(totalHours, 2, HALF_UP);

        DockUnloadExcel unloadExcel = new DockUnloadExcel();
        unloadExcel.setClasses("汇总");
        unloadExcel.setTonnage(tonnage);
        unloadExcel.setPackageNum(totalUnloadNum);
        unloadExcel.setTotalUnloadWeight(totalUnloadWeightSum);
        unloadExcel.setUnloadNum(totalunloadNumSum);
        unloadExcel.setUnloadNumTonDay(totalUnloadNumTonSum);
        unloadExcel.setUnloadRatio(totalUnloadRatioSum+"%");
        unloadExcel.setEffectiveRate(totalEffectiveRate);
        unloadExcel.setAvgDischargeRate(totalAvgDischargeRate);
        unloadExcel.setEffectiveTime(totalEffectiveTime);
        excelUnloadList.add(unloadExcel);

        return excelUnloadList;
    }

    @Override
    public List<ScreenUnloadVo> screenUnloadList() {
        return dockUnloadWorkMapper.screenUnloadList();
    }

    @Override
    public DockUnloadWork selectDockUnloadWorkDesc(Long id) {
        return dockUnloadWorkMapper.selectDockUnloadWorkDesc(id);
    }

}
