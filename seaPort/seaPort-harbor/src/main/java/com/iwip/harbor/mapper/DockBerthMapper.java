package com.iwip.harbor.mapper;

import java.util.List;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.screen.ScreenPlanStatusVo;
import org.apache.ibatis.annotations.Param;

/**
 * 泊位信息Mapper接口
 *
 * @author Fei
 * @date 2025-01-28
 */
public interface DockBerthMapper
{
    /**
     * 查询泊位信息
     *
     * @param dbId 泊位信息主键
     * @return 泊位信息
     */
    public DockBerth selectDockBerthByDbId(Long dbId);

    public List<DockBerth> selectDockBerthByBerthHpIdId(Long dbId);


    /**
     * 查询泊位信息列表
     *
     * @param dockBerth 泊位信息
     * @return 泊位信息集合
     */
    public List<DockBerth> selectDockBerthList(DockBerth dockBerth);

    /**
     * 新增泊位信息
     *
     * @param dockBerth 泊位信息
     * @return 结果
     */
    public int insertDockBerth(DockBerth dockBerth);

    /**
     * 修改泊位信息
     *
     * @param dockBerth 泊位信息
     * @return 结果
     */
    public int updateDockBerth(DockBerth dockBerth);

    /**
     * 删除泊位信息
     *
     * @param dbId 泊位信息主键
     * @return 结果
     */
    public int deleteDockBerthByDbId(Long dbId);

    /**
     * 批量删除泊位信息
     *
     * @param dbIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockBerthByDbIds(Long[] dbIds);

    int removeDockBerthByDbId(DockBerth dockBerth);

    DockBerth selectDockBerthByName(String berthName);

    DockBerth selectDockBerthByNameOrCode(String hbName);

    DockBerth selectDockBerthByCode(String berthCode);

    List<ScreenPlanStatusVo> screenBerthStatusList();

    int updateDockBerthStatus(DockBerth dockBerth);

    List<DockBerth> selectDockBerthByStatus(@Param("berthStatusList") List<String> berthStatusList);

    /*👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇新大屏方法👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇*/
    List<DockBerth> newScreen_SelectListByDeptIds(List<Long> deptIds);

    DockBerth getOneByCode(@Param("berthCode") String berthCode);
}
