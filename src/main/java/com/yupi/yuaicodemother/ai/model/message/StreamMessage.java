package com.yupi.yuaicodemother.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式消息响应基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {

    /**
     * 消息类型
     */
    private String type; // 注意，这里是String类型，所以赋值需要使用 getValue() 方法
}