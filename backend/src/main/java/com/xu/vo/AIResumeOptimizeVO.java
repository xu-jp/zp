package com.xu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIResumeOptimizeVO {

    private List<OptimizeSuggestion> suggestions;

    private String overallScore;

    private String summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimizeSuggestion {
        private String dimension;
        private Integer score;
        private String level;
        private String description;
        private List<String> suggestions;
    }
}
