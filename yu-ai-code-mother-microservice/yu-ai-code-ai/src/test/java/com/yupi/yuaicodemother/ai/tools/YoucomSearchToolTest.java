package com.yupi.yuaicodemother.ai.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YoucomSearchTool 集成测试
 * 需要配置 youcom.api-key 才能正常运行（可设置为空字符串测试错误提示）
 */
@SpringBootTest
class YoucomSearchToolTest {

    @Resource
    private YoucomSearchTool youcomSearchTool;

    @Test
    void testSearchWithConfiguredApiKey() {
        String result = youcomSearchTool.search("人工智能最新进展");
        assertNotNull(result);
        // 有 API Key 时应返回搜索结果或错误信息（不会是配置缺失提示）
        assertFalse(result.contains("未配置 YOUCOM_API_KEY"),
                "API Key 已配置但返回了未配置错误");
        System.out.println("搜索结果: " + result);
    }

    @Test
    void testSearchEmptyQuery() {
        String result = youcomSearchTool.search("");
        assertEquals("搜索关键词不能为空", result);
    }

    @Test
    void testSearchBlankQuery() {
        String result = youcomSearchTool.search("   ");
        assertEquals("搜索关键词不能为空", result);
    }

    @Test
    void testToolName() {
        assertEquals("youcomSearch", youcomSearchTool.getToolName());
    }

    @Test
    void testDisplayName() {
        assertEquals("联网搜索", youcomSearchTool.getDisplayName());
    }
}
