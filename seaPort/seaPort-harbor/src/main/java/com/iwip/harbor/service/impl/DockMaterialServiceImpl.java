package com.iwip.harbor.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iwip.harbor.mapper.DockMaterialMapper;
import com.iwip.harbor.domain.DockMaterial;
import com.iwip.harbor.service.IDockMaterialService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 物资信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-02-25
 */
@Service
public class DockMaterialServiceImpl implements IDockMaterialService
{
    @Autowired
    private DockMaterialMapper dockMaterialMapper;

    /**
     * 查询物资信息
     *
     * @param id 物资信息主键
     * @return 物资信息
     */
    @Override
    public DockMaterial selectDockMaterialById(Long id)
    {
        return dockMaterialMapper.selectDockMaterialById(id);
    }

    @Override
    public DockMaterial selectDockMaterialByName(String materialName) {
        return dockMaterialMapper.selectDockMaterialByName(materialName);
    }

    /**
     * 查询物资信息列表
     *
     * @param dockMaterial 物资信息
     * @return 物资信息
     */
    @Override
    public List<DockMaterial> selectDockMaterialList(DockMaterial dockMaterial)
    {
        if("0".equals(dockMaterial.getRemark())){
            return dockMaterialMapper.selectDockMaterialList1(dockMaterial);
        }
        return dockMaterialMapper.selectDockMaterialList(dockMaterial);
    }

    /**
     * 新增物资信息
     *
     * @param dockMaterial 物资信息
     * @return 结果
     */
    @Override
    public int insertDockMaterial(DockMaterial dockMaterial)
    {

        DockMaterial material = dockMaterialMapper.selectDockMaterialByName(dockMaterial.getMaterialName());
        if (material != null){
            throw new ServiceException("物资：【"+dockMaterial.getMaterialName() + "】已存在！不可再次新增");
        }
        dockMaterial.setCreateBy(SecurityUtils.getNickName());
        dockMaterial.setCreateTime(LocalDateTime.now());
        return dockMaterialMapper.insertDockMaterial(dockMaterial);
    }

    /**
     * 修改物资信息
     *
     * @param dockMaterial 物资信息
     * @return 结果
     */
    @Override
    public int updateDockMaterial(DockMaterial dockMaterial)
    {
        DockMaterial material = dockMaterialMapper.selectDockMaterialByName(dockMaterial.getMaterialName());
        if (material != null && !Objects.equals(material.getId(), dockMaterial.getId())){
            throw new ServiceException("物资：【"+dockMaterial.getMaterialName() + "】已存在！不可再次新增");
        }
        return dockMaterialMapper.updateDockMaterial(dockMaterial);
    }

    /**
     * 批量删除物资信息
     *
     * @param ids 需要删除的物资信息主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDockMaterialByIds(Long[] ids)
    {
        if (ids == null ||ids.length == 0){
            throw new ServiceException("删除失败，没有查询到参数");
        }
        int row = 0;
        for (Long id : ids) {
            DockMaterial dockMaterial = dockMaterialMapper.selectDockMaterialById(id);
            if (StringUtils.equals("0",dockMaterial.getMaterialStatus())){
                throw new ServiceException("删除失败，物资是已启用状态，不可删除！");
            }
            dockMaterial.setDelFlag("1");
            row += dockMaterialMapper.updateDockMaterial(dockMaterial);
        }
        return row;
    }

    /**
     * 删除物资信息信息
     *
     * @param id 物资信息主键
     * @return 结果
     */
    @Override
    public int deleteDockMaterialById(Long id)
    {
        return dockMaterialMapper.deleteDockMaterialById(id);
    }

    @Override
    public int controls(Long id,String materialStatus) {
        DockMaterial dockMaterial = dockMaterialMapper.selectDockMaterialById(id);

        dockMaterial.setMaterialStatus(materialStatus); // 禁用
        return dockMaterialMapper.updateDockMaterial(dockMaterial);

    }

    @Override
    public List<DockMaterial> enableList(DockMaterial dockMaterial) {
        return dockMaterialMapper.enableList(dockMaterial);
    }


}
