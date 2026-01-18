package com.yupi.yuaicodemother.ai;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yupi.yuaicodemother.ai.guardrail.PromptSafetyInputGuardrail;
import com.yupi.yuaicodemother.ai.tools.ToolManager;
import com.yupi.yuaicodemother.exception.BusinessException;
import com.yupi.yuaicodemother.exception.ErrorCode;
import com.yupi.yuaicodemother.exception.ThrowUtils;
import com.yupi.yuaicodemother.service.ChatHistoryService;
import com.yupi.yuaicodemother.utils.SpringContextUtil;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: ai代码质量分析服务工厂
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 15:55
 */

@Slf4j
@Configuration
public class AiCodeQualityAnalysisServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;
//
//    @Resource
//    private RedisChatMemoryStore redisChatMemoryStore;
//
//    @Resource
//    private ChatHistoryService chatHistoryService;

    @Resource
    private ToolManager toolManager;

    /**
     * AI 服务缓存示例
     * 缓存策略
     * - 最大1000个实例
     * - 写入后30分钟过期
     * - 访问后10分钟过期
     */
    private final Cache<String, AiCodeQualityAnalysisService> serviceCache = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(Duration.ofMinutes(30))
        .expireAfterAccess(Duration.ofMinutes(10))
        .removalListener((key, value, cause) -> {
            log.debug("AI 服务实例被移除，缓存键: {}, 移除原因: {}", key, cause);
        })
        .build();

    /**
     * 根据appid获取服务
     */
    public AiCodeQualityAnalysisService getAiCodeQualityAnalysisService(Long appId) {
        String cacheKey = buildCacheKey(appId);
        return serviceCache.get(cacheKey, key -> createAiCodeQualityAnalysisService(appId));
    }

    /**
     * 创建新的AI代码质量分析服务实例
     */
    private AiCodeQualityAnalysisService createAiCodeQualityAnalysisService(Long appId) {
        StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
        try {
            return AiServices.builder(AiCodeQualityAnalysisService.class)
                .chatModel(chatModel)
                .streamingChatModel(openAiStreamingChatModel)
                .tools(toolManager.getAllTools())
                .hallucinatedToolNameStrategy(toolExecutionRequest ->
                    ToolExecutionResultMessage.from(toolExecutionRequest,
                        "Error: there is no tool called " + toolExecutionRequest.name()))
                .maxSequentialToolsInvocations(20)
                .inputGuardrails(new PromptSafetyInputGuardrail())
                .build();
        } catch (Exception e) {
            log.error("创建AI代码质量分析服务实例失败, appId:{}", appId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建AI代码质量分析服务实例失败");
        }
    }

    @Bean
    public AiCodeQualityAnalysisService aiCodeQualityAnalysisService() {
        return getAiCodeQualityAnalysisService(0L);
    }

    /**
     * 构造缓存键
     */
    private String buildCacheKey(Long appId) {
        return "quality-analysis-" + appId;
    }

}
