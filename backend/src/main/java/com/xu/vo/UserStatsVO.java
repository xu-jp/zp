package com.xu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsVO {

    private Long applications;

    private Long interviews;

    private Long favorites;

    private Long resumes;
}
