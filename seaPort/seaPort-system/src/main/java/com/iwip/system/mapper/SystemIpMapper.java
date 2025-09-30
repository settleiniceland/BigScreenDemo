package com.iwip.system.mapper;

import java.util.List;
import com.iwip.system.domain.SystemIp;

/**
 * 用户Ip地址Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-30
 */
public interface SystemIpMapper 
{
    /**
     * 查询用户Ip地址
     * 
     * @param id 用户Ip地址主键
     * @return 用户Ip地址
     */
    public SystemIp selectSystemIpById(Long id);

    List<SystemIp> selectSystemIpByUserId(Long userId);

    /**
     * 查询用户Ip地址列表
     * 
     * @param systemIp 用户Ip地址
     * @return 用户Ip地址集合
     */
    public List<SystemIp> selectSystemIpList(SystemIp systemIp);

    /**
     * 新增用户Ip地址
     * 
     * @param systemIp 用户Ip地址
     * @return 结果
     */
    public int insertSystemIp(SystemIp systemIp);

    /**
     * 修改用户Ip地址
     * 
     * @param systemIp 用户Ip地址
     * @return 结果
     */
    public int updateSystemIp(SystemIp systemIp);

    /**
     * 删除用户Ip地址
     * 
     * @param id 用户Ip地址主键
     * @return 结果
     */
    public int deleteSystemIpById(Long id);

    /**
     * 批量删除用户Ip地址
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSystemIpByIds(Long[] ids);


}
