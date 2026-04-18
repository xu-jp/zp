package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class Message extends BaseEntity {

    private Long userId;

    private String title;

    private String content;

    private Integer type;

    private Long relatedId;

    private Integer isRead;
}
