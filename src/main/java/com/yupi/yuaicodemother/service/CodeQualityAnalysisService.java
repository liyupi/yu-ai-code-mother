package com.yupi.yuaicodemother.service;


import com.mybatisflex.core.service.IService;
import com.yupi.yuaicodemother.model.entity.CodeQuality;
import com.yupi.yuaicodemother.model.vo.CodeQualityVO;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量分析服务接口
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 22:30
 */
public interface CodeQualityAnalysisService extends IService<CodeQuality> {

    /**
     * 分析代码质量
     */
    CodeQualityVO analyzeCodeQuality(String code, Long appId, HttpServletRequest request);

    /**
     * 流式分析代码质量（字符串块级别）
     * 返回字符串块流，适用于需要将AI响应按块处理的场景
     *
     * @param code 待分析的代码内容
     * @return 字符串块流，按块接收AI响应内容
     */
    Flux<String> analyzeCodeQualityFlux(String code, Long appId, HttpServletRequest request);

    /**
     * 根据应用ID获取最新的代码质量分析结果（脱敏返回VO对象）
     *
     * @param appId 应用ID
     * @return 代码质量分析VO对象
     */
    CodeQualityVO getLatestCodeQualityVOByAppId(Long appId);

    /**
     * 分析现有应用的代码质量
     * 从文件系统读取应用代码并进行分析
     *
     * @param appId 应用ID
     * @param request HTTP请求
     * @return 代码质量分析结果
     */
    CodeQualityVO analyzeExistingAppCode(Long appId, HttpServletRequest request);

    /**
     * 流式分析现有应用的代码质量
     * 从文件系统读取应用代码并流式分析
     *
     * @param appId 应用ID
     * @param request HTTP请求
     * @return 流式响应结果
     */
    Flux<String> analyzeExistingAppCodeStream(Long appId, HttpServletRequest request);
}