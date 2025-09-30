package com.iwip.harbor.mapper;

import java.util.List;
import com.iwip.harbor.domain.DockUnloadDetail;

/**
 * 卸货子Mapper接口
 * 
 * @author Fei
 * @date 2025-01-28
 */
public interface DockUnloadDetailMapper 
{
    /**
     * 查询卸货子
     * 
     * @param dudId 卸货子主键
     * @return 卸货子
     */
    public DockUnloadDetail selectDockUnloadDetailByDudId(Long dudId);
    public List<DockUnloadDetail> selectUnloadDetailByRecordByDuId(Long dudId);

    public List<DockUnloadDetail> selectDockUnloadDetailByDuId(Long duId);

    /**
     * 查询卸货子列表
     * 
     * @param dockUnloadDetail 卸货子
     * @return 卸货子集合
     */
    public List<DockUnloadDetail> selectDockUnloadDetailList(DockUnloadDetail dockUnloadDetail);

    /**
     * 新增卸货子
     * 
     * @param dockUnloadDetail 卸货子
     * @return 结果
     */
    public int insertDockUnloadDetail(DockUnloadDetail dockUnloadDetail);

    /**
     * 修改卸货子
     * 
     * @param dockUnloadDetail 卸货子
     * @return 结果
     */
    public int updateDockUnloadDetail(DockUnloadDetail dockUnloadDetail);

    /**
     * 删除卸货子
     * 
     * @param dudId 卸货子主键
     * @return 结果
     */
    public int deleteDockUnloadDetailByDudId(Long dudId);

    /**
     * 批量删除卸货子
     * 
     * @param dudIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockUnloadDetailByDudIds(Long[] dudIds);

    DockUnloadDetail selectDetailDescByDuId(Long duId);

    List<DockUnloadDetail> selectDetailDescList(Long duId);

    List<DockUnloadDetail> selectByDuIds(List<Long> duIds);
}
