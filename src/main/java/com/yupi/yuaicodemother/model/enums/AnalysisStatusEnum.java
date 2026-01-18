package com.yupi.yuaicodemother.model.enums;


import lombok.Getter;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量分析状态枚举
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 15:16
 */

@Getter
public enum AnalysisStatusEnum {

    ANALYZING("analyzing", "分析中"),
    COMPLETED("completed", "已完成"),
    FAILED("failed", "失败");

    private final String value;
    private final String text;

    AnalysisStatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static AnalysisStatusEnum getByValue(String value) {
        for (AnalysisStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

}
