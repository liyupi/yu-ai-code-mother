package com.yupi.yuaicodemother.core.parser;

/**
 * 代码解析器策略接口
 * 
 * @author yupi
 */
public interface CodeParser<T> {

    /**
     * 解析代码内容
     * 
     * @param codeContent 原始代码内容
     * @return 解析后的结果对象
     */
    T parseCode(String codeContent);
}
