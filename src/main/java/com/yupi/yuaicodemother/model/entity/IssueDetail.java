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
import java.util.List;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量问题详情实体类
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 15:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("issue_detail")
public class IssueDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    @Column("codeQualityId")
    private Long codeQualityId;

    @Column("issueType")
    private String issueType;

    @Column("severity")
    private String severity;

    @Column("description")
    private String description;

    @Column("location")
    private String location;

    @Column("lineNumber")
    private Integer lineNumber;

    @Column("category")
    private String category;

    @Column("level")
    private Integer level;

    @Column("createTime")
    private LocalDateTime createTime;

    @Column("updateTime")
    private LocalDateTime updateTime;

    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

    // 关联关系：一个IssueDetail对应多个FixSuggestion
    private List<FixSuggestion> fixSuggestions;
}