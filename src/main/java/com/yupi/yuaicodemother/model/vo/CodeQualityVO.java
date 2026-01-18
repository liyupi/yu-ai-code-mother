package com.yupi.yuaicodemother.model.vo;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 脱敏的代码质量信息
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 14:46
 */

@Data
public class CodeQualityVO {

    private Long id;

    private AppVO appInfo;

    private Integer completeness;
    private String completenessComment;

    private Integer codeQuality;
    private String codeQualityComment;

    private Integer overallScore;
    private String overallComment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
