package com.yupi.yuaicodemother.core;


import com.yupi.yuaicodemother.ai.AiCodeQualityAnalysisService;
import com.yupi.yuaicodemother.ai.AiCodeQualityAnalysisServiceFactory;
import com.yupi.yuaicodemother.exception.BusinessException;
import com.yupi.yuaicodemother.exception.ErrorCode;
import com.yupi.yuaicodemother.model.vo.CodeQualityVO;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量分析门面
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 19:25
 */

@Slf4j
@Service
public class CodeQualityAnalysisFacade {

    @Resource
    private AiCodeQualityAnalysisServiceFactory aiCodeQualityAnalysisServiceFactory;

    /**
     * 代码质量分析 同步
     */
    public CodeQualityVO analyzeCodeQuality(String code, Long appId) {
        // 获取AI服务实例
        AiCodeQualityAnalysisService aiService = aiCodeQualityAnalysisServiceFactory.getAiCodeQualityAnalysisService(appId);
        // 调用分析方法
        return aiService.analyzeCodeQuality(code);
    }

    // 分析代码质量（流式）
    public Flux<String> analyzeCodeQualityStream(String code, Long appId) {
        // 获取AI服务实例
        AiCodeQualityAnalysisService aiService = aiCodeQualityAnalysisServiceFactory.getAiCodeQualityAnalysisService(appId);
        // 调用流式分析方法
        TokenStream tokenStream = aiService.analyzeCodeQualityStream(code);
        // 转换为Flux<String>格式
        return processTokenStream(tokenStream);
    }

    // 处理tokenStream，转换为Flux<String>
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            try {
                tokenStream.onPartialResponse((String partialResponse) -> {
                    // 处理部分响应
                    log.info("Partial response: {}", partialResponse);
                    sink.next(partialResponse);
                })
                    .onCompleteResponse((ChatResponse response) -> {
                        // 处理完整响应
                        log.info("Complete response: {}", response);
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        // 处理错误
                        log.error("Error processing token stream", error);
                        sink.error(error);
                    })
                    .start();
            } catch (Exception e) {
                log.error("创建流式输出时失败", e);
                sink.error(new BusinessException(ErrorCode.SYSTEM_ERROR, "创建流式输出时失败"));
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建流式输出时失败");
            }
        });
    }
}
