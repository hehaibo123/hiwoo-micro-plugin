package com.hiwoo.entity.vo;

import com.hiwoo.entity.po.SysMicroApplicationResource;
import com.hiwoo.entity.po.SysResourceOperate;
import lombok.Data;

import java.util.List;

/**
 * @Author Haibo He
 * @Date 2023/9/5 19:48
 * @Version 1.0
 */
@Data
public class MicroApplicationResourceVO extends SysMicroApplicationResource {

    private List<SysResourceOperate> operates;
}
