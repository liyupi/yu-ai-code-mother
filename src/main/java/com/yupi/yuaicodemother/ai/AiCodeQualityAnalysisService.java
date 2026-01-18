package com.yupi.yuaicodemother.ai;


import com.yupi.yuaicodemother.model.vo.CodeQualitySummaryVO;
import com.yupi.yuaicodemother.model.vo.CodeQualityVO;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: AI代码质量分析服务
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 15:51
 */
public interface AiCodeQualityAnalysisService {

    /**
     * 分析代码质量 quality
     */
    @SystemMessage(fromResource = "prompt/quality-analysis.txt")
    CodeQualityVO analyzeCodeQuality(String code);

    /**
     * 分析代码质量（流式）
     */
    @SystemMessage(fromResource = "prompt/quality-analysis.txt")
    TokenStream analyzeCodeQualityStream(String code);
}
