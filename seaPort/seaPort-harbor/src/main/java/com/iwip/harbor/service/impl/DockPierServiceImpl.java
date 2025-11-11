package com.iwip.harbor.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.iwip.common.annotation.DataScope;
import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.screen.ScreenPierVo;
import com.iwip.harbor.domain.vo.DockPierAndBerthVo;
import com.iwip.harbor.mapper.DockBerthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iwip.harbor.mapper.DockPierMapper;
import com.iwip.harbor.domain.DockPier;
import com.iwip.harbor.service.IDockPierService;

/**
 * 码头信息Service业务层处理
 * 
 * @author Fei
 * @date 2025-01-28
 */
@Service
public class DockPierServiceImpl implements IDockPierService 
{
    @Autowired
    private DockPierMapper dockPierMapper;

    @Autowired
    private DockBerthMapper dockBerthMapper;



    /**
     * 查询码头信息
     * 
     * @param dpId 码头信息主键
     * @return 码头信息
     */
    @Override
    public DockPier selectDockPierByDpId(Long dpId)
    {
        return dockPierMapper.selectDockPierByDpId(dpId);
    }

    /**
     * 查询码头信息列表
     * 
     * @param dockPier 码头信息
     * @return 码头信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<DockPier> selectDockPierList(DockPier dockPier)
    {
        List<DockPier> pierList = dockPierMapper.selectDockPierList(dockPier);
        for (DockPier pier : pierList) {
            pier.setType("dock");
            List<DockBerth> dockBerthList = pier.getChildren();
            for (DockBerth dockBerth : dockBerthList) {
                // 码头名称
//                dockBerth.setRemark01(pier.getPierName());
                dockBerth.setType("berth");
            }
        }
        return pierList;
    }

    /**
     * 新增码头信息
     * 
     * @param dockPierAndBerthVo 码头信息
     * @return 结果
     */
    @Override
    public int insertDockPier(DockPierAndBerthVo dockPierAndBerthVo)
    {

        DockPier isPierNameFlag = dockPierMapper.selectDockPierByDpName(dockPierAndBerthVo.getPierName());
        if (isPierNameFlag != null){
            throw new ServiceException("码头名称"+isPierNameFlag.getPierName()+"已存在，不可新增！");
        }

        DockPier isPierCodeFlag = dockPierMapper.selectDockPierByDpCode(dockPierAndBerthVo.getPierCode());
        if (isPierCodeFlag != null){
            throw new ServiceException("码头编码"+isPierCodeFlag.getPierCode()+"已存在，不可新增！");
        }

        if (StringUtils.equals("[]",dockPierAndBerthVo.getPierGeoJson())){
            throw new ServiceException("区域没有划分，请划分区域之后再操作新增");
        }
        if (StringUtils.isBlank(dockPierAndBerthVo.getPierGeoJson())){
            throw new ServiceException("区域没有划分，请划分区域之后再操作新增");
        }

        DockPier dockPier = new DockPier();
        dockPier.setPierName(dockPierAndBerthVo.getPierName());
        dockPier.setPierGeoJson(dockPierAndBerthVo.getPierGeoJson());
        dockPier.setPierType(dockPierAndBerthVo.getPierType());
        dockPier.setPierCode(dockPierAndBerthVo.getPierCode());
        dockPier.setDelFlag("0");
        dockPier.setUserId(SecurityUtils.getUserId());
        dockPier.setCreateTime(LocalDateTime.now());
        dockPier.setRemark(dockPierAndBerthVo.getRemark());
        dockPier.setCreateBy(SecurityUtils.getNickName());
        dockPier.setDeptId(SecurityUtils.getDeptId());
        return dockPierMapper.insertDockPier(dockPier);
    }

    /**
     * 修改码头信息
     * 
     * @param dockPierAndBerthVo 码头信息
     * @return 结果
     */
    @Override
    public int updateDockPier(DockPierAndBerthVo dockPierAndBerthVo)
    {
        DockPier isPierNameFlag = dockPierMapper.selectDockPierByDpName(dockPierAndBerthVo.getPierName());
        if (isPierNameFlag != null){
            if (!isPierNameFlag.getPierId().equals(dockPierAndBerthVo.getPierId())) {
                throw new ServiceException("码头名称" + isPierNameFlag.getPierName() + "已存在，不可新增！");
            }
        }

        DockPier isPierCodeFlag = dockPierMapper.selectDockPierByDpCode(dockPierAndBerthVo.getPierCode());
        if (isPierCodeFlag != null){
            if (!isPierCodeFlag.getPierId().equals(dockPierAndBerthVo.getPierId())){
                throw new ServiceException("码头编码"+isPierCodeFlag.getPierCode()+"已存在，不可新增！");
            }
        }
        if (StringUtils.equals("[]",dockPierAndBerthVo.getPierGeoJson())){
            throw new ServiceException("区域没有划分，请划分区域之后再操作新增");
        }
        if (StringUtils.isBlank(dockPierAndBerthVo.getPierGeoJson())){
            throw new ServiceException("区域没有划分，请划分区域之后再操作新增");
        }

        DockPier dockPier = new DockPier();
        dockPier.setPierId(dockPierAndBerthVo.getPierId());
        dockPier.setPierName(dockPierAndBerthVo.getPierName());
        dockPier.setPierGeoJson(dockPierAndBerthVo.getPierGeoJson());
        dockPier.setPierCode(dockPierAndBerthVo.getPierCode());
        dockPier.setPierType(dockPierAndBerthVo.getPierType());
        dockPier.setUpdateBy(SecurityUtils.getUsername());
        dockPier.setUpdateTime(LocalDateTime.now());
        dockPier.setRemark(dockPierAndBerthVo.getRemark());
        dockPier.setPierId(dockPierAndBerthVo.getPierId());
        return dockPierMapper.updateDockPier(dockPier);
    }

    /**
     * 批量删除码头信息
     * 
     * @param dpIds 需要删除的码头信息主键
     * @return 结果
     */
    @Override
    public int deleteDockPierByDpIds(Long[] dpIds)
    {
        if (dpIds.length == 0){
            throw new ServiceException("参数为空，请选择参数之后进行删除操作！");
        }
        int row = 0;
        for (Long dpId : dpIds) {
            DockPier dockPier =  dockPierMapper.selectDockPierByDpId(dpId);
            if (StringUtils.equals(dockPier.getDelFlag(), "1")) {
                throw new ServiceException("删除失败，该条数据已被删除！");
            }
            dockPier.setDelFlag("1");
            row += dockPierMapper.removeDockPierByDpId(dockPier);
        }
        return row;
    }

    /**
     * 删除码头信息信息
     * 
     * @param dpId 码头信息主键
     * @return 结果
     */
    @Override
    public int deleteDockPierByDpId(Long dpId)
    {

        DockPier dockPier =  dockPierMapper.selectDockPierByDpId(dpId);
        if (StringUtils.equals(dockPier.getDelFlag(), "1")) {
            throw new ServiceException("删除失败，该条数据已被删除！");
        }
        List<DockBerth> dockBerthList = dockBerthMapper.selectDockBerthByBerthHpIdId(dockPier.getPierId());
        if (!dockBerthList.isEmpty()){
            throw new ServiceException("该码头下还有未删除的泊位，请先删除泊位再删除码头");
        }

        dockPier.setDelFlag("1");
        return dockPierMapper.removeDockPierByDpId(dockPier);
    }

    @Override
    public List<DockPier> selectScreenPierList(DockPier dockPier) {

        return dockPierMapper.selectScreenPierList(dockPier);
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<DockPier> selectList(DockPier dockPier) {
        return dockPierMapper.selectList(dockPier);
    }

    @Override
    public List<ScreenPierVo> selectScreenPierLeftBerthList(DockBerth dockBerth) {
        return dockPierMapper.selectScreenPierLeftBerthList(dockBerth);
    }
}
