package com.hiwoo.entity.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MicroApplicationResourceBO {

    private String resourceTag;

    private String zhCn;

    private String en;

    private List<OperateBO> operates;

    private List<MicroApplicationResourceBO> resources;
}
