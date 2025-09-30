package com.iwip.system.service.impl;

import java.util.List;

import com.iwip.common.core.domain.entity.SysUser;
import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.StringUtils;
import com.iwip.common.utils.ip.IpUtils;
import com.iwip.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iwip.system.mapper.SystemIpMapper;
import com.iwip.system.domain.SystemIp;
import com.iwip.system.service.ISystemIpService;

/**
 * 用户Ip地址Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-30
 */
@Service
public class SystemIpServiceImpl implements ISystemIpService 
{
    @Autowired
    private SystemIpMapper systemIpMapper;

    @Autowired
    private ISysUserService sysUserService;



    /**
     * 查询用户Ip地址
     * 
     * @param id 用户Ip地址主键
     * @return 用户Ip地址
     */
    @Override
    public SystemIp selectSystemIpById(Long id)
    {
        return systemIpMapper.selectSystemIpById(id);
    }

    /**
     * 查询用户Ip地址列表
     * 
     * @param systemIp 用户Ip地址
     * @return 用户Ip地址
     */
    @Override
    public List<SystemIp> selectSystemIpList(SystemIp systemIp)
    {
        return systemIpMapper.selectSystemIpList(systemIp);
    }

    /**
     * 新增用户Ip地址
     * 
     * @param systemIp 用户Ip地址
     * @return 结果
     */
    @Override
    public int insertSystemIp(SystemIp systemIp)
    {
        SysUser sysUser = sysUserService.selectUserById(systemIp.getUserId());

        if (StringUtils.equals("0",sysUser.getBindIpStatus())){
            throw new ServiceException("绑定失败，请先到 用户管理页面 将用户状态设置为【已绑定】");
        }
        return systemIpMapper.insertSystemIp(systemIp);
    }

    /**
     * 修改用户Ip地址
     * 
     * @param systemIp 用户Ip地址
     * @return 结果
     */
    @Override
    public int updateSystemIp(SystemIp systemIp)
    {
        SysUser sysUser = sysUserService.selectUserById(systemIp.getUserId());
        if (StringUtils.equals("0",sysUser.getBindIpStatus())){
            throw new ServiceException("绑定失败，请先到 用户管理页面 将用户状态设置为【已绑定】");
        }
        return systemIpMapper.updateSystemIp(systemIp);
    }

    /**
     * 批量删除用户Ip地址
     * 
     * @param ids 需要删除的用户Ip地址主键
     * @return 结果
     */
    @Override
    public int deleteSystemIpByIds(Long[] ids)
    {
        return systemIpMapper.deleteSystemIpByIds(ids);
    }

    /**
     * 删除用户Ip地址信息
     * 
     * @param id 用户Ip地址主键
     * @return 结果
     */
    @Override
    public int deleteSystemIpById(Long id)
    {
        return systemIpMapper.deleteSystemIpById(id);
    }

    @Override
    public int test() {

        String hostName = IpUtils.getHostName();
        String ipAddr = IpUtils.getIpAddr();
        String hostIp = IpUtils.getHostIp();
        System.out.println("hostName:"+hostName);
        System.out.println("ipAddr:"+ipAddr);
        System.out.println("hostIp:"+hostIp);
        return 1;
    }

    @Override
    public List<SystemIp> selectSystemIpByUserId(Long userId) {
        return systemIpMapper.selectSystemIpByUserId(userId);
    }
}
