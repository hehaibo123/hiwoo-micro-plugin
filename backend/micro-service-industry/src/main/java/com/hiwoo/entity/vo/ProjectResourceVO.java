package com.hiwoo.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class ProjectResourceVO {

    private String applicationTag;

    private String resourceTag;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long resourceId;

    private String resourceName;

    private String opreation;

    private String projectName;

    List<ProjectResourceVO> children;
}
