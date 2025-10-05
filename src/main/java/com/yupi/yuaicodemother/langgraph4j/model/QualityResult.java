package com.yupi.yuaicodemother.langgraph4j.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 代码质量检查结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityResult implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 是否通过质检
     */
    private Boolean isValid;
    
    /**
     * 错误列表
     */
    private List<String> errors;
    
    /**
     * 改进建议
     */
    private List<String> suggestions;
}