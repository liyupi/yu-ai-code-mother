package com.yupi.yuaicodemother.model.vo;


import java.util.List;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量分析VO
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 14:57
 */

@Data
public class CodeQualityAnalysisVO {
    /**
     * 代码质量总览
     */
    private CodeQualitySummaryVO summary;

    /**
     * 详细评分
     */
    private CodeQualityVO details;

    /**
     * 问题列表
     */
    private List<CodeIssueVO> issues;

    /**
     * 分析耗时
     */
    private Long analysisTime;
}

