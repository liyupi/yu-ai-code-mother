package com.yupi.yuaicodemother.model.vo;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码问题VO
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 14:56
 */

@Data
public class CodeIssueVO {
    /**
     * 问题类型
     */
    private String issueType; // SECURITY, PERFORMANCE, READABILITY, MAINTAINABILITY, STYLE

    /**
     * 问题严重程度
     */
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW, INFO

    /**
     * 问题描述
     */
    private String description;

    /**
     * 问题位置
     */
    private Integer line;
    private Integer column;

    /**
     * 代码片段
     */
    private String codeSnippet;

    /**
     * 修复建议
     */
    private String suggestion;

}
