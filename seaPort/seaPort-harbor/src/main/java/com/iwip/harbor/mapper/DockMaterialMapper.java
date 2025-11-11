package com.iwip.harbor.mapper;

import java.util.List;
import com.iwip.harbor.domain.DockMaterial;

/**
 * 物资信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-25
 */
public interface DockMaterialMapper 
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
    public List<DockMaterial> selectDockMaterialList1(DockMaterial dockMaterial);

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
     * 删除物资信息
     * 
     * @param id 物资信息主键
     * @return 结果
     */
    public int deleteDockMaterialById(Long id);

    /**
     * 批量删除物资信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockMaterialByIds(Long[] ids);

    List<DockMaterial> enableList(DockMaterial dockMaterial);
}
