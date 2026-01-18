package com.yupi.yuaicodemother.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量评分实体类
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 14:29
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("code_quality")
public class CodeQuality implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    @Column("appId")
    private Long appId;

    @Column("completeness")
    private Integer completeness;

    @Column("completenessComment")
    private String completenessComment;

    @Column("codeQuality")
    private Integer codeQuality;

    @Column("codeQualityComment")
    private String codeQualityComment;

    @Column("overallScore")
    private Integer overallScore;

    @Column("overallComment")
    private String overallComment;

    @Column("createTime")
    private LocalDateTime createTime;

    @Column("updateTime")
    private LocalDateTime updateTime;

    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;
}