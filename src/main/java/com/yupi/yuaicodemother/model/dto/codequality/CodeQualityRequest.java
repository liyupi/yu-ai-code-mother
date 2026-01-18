package com.yupi.yuaicodemother.model.dto.codequality;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量检测请求
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 14:39
 */

@Data
public class CodeQualityRequest {

    /**
     * 待分析的代码内容
     */
    private String codeContent;

    /**
     * 代码语言类型
     */
    private String language;

    /**
     * 代码文件路径
     */
    private String filePath;

    /**
     * 关联的应用ID
     */
    private Long appId;

    /**
     * 分析类型（全量/增量）
     */
    private String analysisType;


}
