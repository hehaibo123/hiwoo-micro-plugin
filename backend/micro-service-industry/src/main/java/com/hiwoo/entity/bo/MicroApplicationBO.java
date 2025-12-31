package com.hiwoo.entity.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MicroApplicationBO {

    private String applicationName;

    private String applicationTag;

    private String applicationType;

    private List<MicroApplicationResourceBO> resources;

    private List<MicroApplicationMenuBO> menus;

}
