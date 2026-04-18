package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_favorite")
public class Favorite extends BaseEntity {

    private Long userId;

    private Long jobId;

    private String remark;
}
