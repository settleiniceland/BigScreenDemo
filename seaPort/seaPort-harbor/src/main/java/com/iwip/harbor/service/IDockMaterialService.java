package com.iwip.harbor.service;

import java.util.List;
import com.iwip.harbor.domain.DockMaterial;

/**
 * 物资信息Service接口
 * 
 * @author ruoyi
 * @date 2025-02-25
 */
public interface IDockMaterialService 
{
    /**
     * 查询物资信息
     * 
     * @param id 物资信息主键
     * @return 物资信息
     */
    public DockMaterial selectDockMaterialById(Long id);
    public DockMaterial selectDockMaterialByName(String materialName);

    /**
     * 查询物资信息列表
     * 
     * @param dockMaterial 物资信息
     * @return 物资信息集合
     */
    public List<DockMaterial> selectDockMaterialList(DockMaterial dockMaterial);

    /**
     * 新增物资信息
     * 
     * @param dockMaterial 物资信息
     * @return 结果
     */
    public int insertDockMaterial(DockMaterial dockMaterial);

    /**
     * 修改物资信息
     * 
     * @param dockMaterial 物资信息
     * @return 结果
     */
    public int updateDockMaterial(DockMaterial dockMaterial);

    /**
     * 批量删除物资信息
     * 
     * @param ids 需要删除的物资信息主键集合
     * @return 结果
     */
    public int deleteDockMaterialByIds(Long[] ids);

    /**
     * 删除物资信息信息
     * 
     * @param id 物资信息主键
     * @return 结果
     */
    public int deleteDockMaterialById(Long id);

    int controls(Long id,String materialStatus);

    List<DockMaterial> enableList(DockMaterial dockMaterial);
}
