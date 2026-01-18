package com.yupi.yuaicodemother.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 修复建议实体类
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 15:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("fix_suggestion")
public class FixSuggestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 关联的代码质量评分ID
     */
    @Column("codeQualityId")
    private Long codeQualityId;

    /**
     * 关联的问题ID
     */
    @Column("issueId")
    private Long issueId;

    /**
     * 修复建议内容
     */
    @Column("suggestion")
    private String suggestion;

    /**
     * 修复优先级
     */
    @Column("priority")
    private String priority;

    /**
     * 修复难度
     */
    @Column("difficulty")
    private String difficulty;

    /**
     * 修复类型
     */
    @Column("fixType")
    private String fixType;

    /**
     * 预估修复时间（分钟）
     */
    @Column("estimatedTime")
    private Integer estimatedTime;

    /**
     * 代码示例
     */
    @Column("codeExample")
    private String codeExample;

    /**
     * 相关文档链接
     */
    @Column("docLink")
    private String docLink;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;
}