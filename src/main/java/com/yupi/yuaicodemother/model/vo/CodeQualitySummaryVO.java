package com.yupi.yuaicodemother.model.vo;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量总结VO
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 14:55
 */

@Data
public class CodeQualitySummaryVO {
    /**
     * 整体评分
     */
    private Integer overallScore;

    /**
     * 评分等级（A/B/C/D）
     */
    private String grade;

    /**
     * 问题总数
     */
    private Integer totalIssues;

    /**
     * 严重问题数
     */
    private Integer criticalIssues;

    /**
     * 警告问题数
     */
    private Integer warningIssues;

    /**
     * 提示问题数
     */
    private Integer infoIssues;
}
