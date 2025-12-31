package com.hiwoo.entity.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SyncResourceBO  {


    private String applicationTag;

    private String resourceTag;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long resourceId;

    private String resourceName;

    private String opreation;

    private List<SyncResourceBO> children;
}
