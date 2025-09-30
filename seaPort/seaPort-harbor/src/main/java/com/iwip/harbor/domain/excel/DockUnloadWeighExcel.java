package com.iwip.harbor.domain.excel;

import com.iwip.common.annotation.Excel;
import lombok.Data;

@Data
public class DockUnloadWeighExcel {


    @Excel(name = "系统唯一标识，勿动")
    private Long id;

    @Excel(name = "船名为参考数据，不可修改")
    private String shipName;

    @Excel(name = "矿号为参考数据，不可修改")
    private String mineNumber;

    @Excel(name = "泊位为参考数据，不可修改")
    private String hbName;

    @Excel(name = "作业类型为参考数据，不可修改")
    private String remark01;

    @Excel(name = "已卸货量")
    private String unloadWeight;

}
