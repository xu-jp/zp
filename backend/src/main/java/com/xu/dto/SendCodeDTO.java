package com.xu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCodeDTO {

    @NotBlank(message = "目标不能为空")
    private String target;

    private Integer type;
}
