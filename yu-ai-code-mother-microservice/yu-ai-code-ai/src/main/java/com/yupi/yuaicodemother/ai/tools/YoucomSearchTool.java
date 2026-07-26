package com.yupi.yuaicodemother.ai.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * You.com 联网搜索工具
 * 支持 AI 在生成应用时进行实时网页搜索，获取技术文档和最新资料
 */
@Slf4j
@Component
public class YoucomSearchTool extends BaseTool {

    @Value("${youcom.api-key:}")
    private String apiKey;

    @Value("${youcom.endpoint:https://ydc-index.io/v1/search}")
    private String endpoint;

    @Tool("通过联网搜索获取与问题相关的最新信息")
    public String search(@P("搜索关键词") String query) {
        if (query == null || query.isBlank()) {
            return "搜索关键词不能为空";
        }

        if (apiKey == null || apiKey.isBlank()) {
            return "You.com Search API 未配置 YOUCOM_API_KEY，请在配置文件中设置 youcom.api-key";
        }

        JSONObject payload = new JSONObject();
        payload.set("query", query);
        payload.set("count", 10);

        try (HttpResponse response = HttpRequest.post(endpoint)
                .header("Accept", "application/json")
                .header("X-API-Key", apiKey)
                .body(payload.toString())
                .timeout(30000)
                .execute()) {
            int status = response.getStatus();
            if (status == 429) {
                return "You.com Search 请求频率超限 (429)";
            }
            if (status == 401) {
                return "You.com API Key 无效或已过期";
            }
            if (status == 403) {
                return "You.com API Key 权限不足";
            }
            if (status != 200) {
                return "You.com Search 请求失败 (Status " + status + ")";
            }

            return parseSearchResults(response.body());
        } catch (Exception e) {
            log.error("You.com Search 调用失败: {}", e.getMessage(), e);
            return "You.com Search 请求失败: " + e.getMessage();
        }
    }

    private String parseSearchResults(String jsonBody) {
        try {
            JSONObject result = new JSONObject(jsonBody);
            List<?> webResults = result.getJSONArray("results");
            if (webResults == null || webResults.isEmpty()) {
                return "未找到相关结果";
            }

            List<String> lines = new ArrayList<>();
            int count = Math.min(webResults.size(), 10);
            for (int i = 0; i < count; i++) {
                JSONObject item = (JSONObject) webResults.get(i);
                String title = item.getStr("title");
                if (title == null) {
                    title = "结果 " + (i + 1);
                }
                String url = item.getStr("url");
                cn.hutool.json.JSONArray snippets = item.getJSONArray("snippets");
                String snippet = (snippets != null && !snippets.isEmpty())
                        ? snippets.getStr(0)
                        : item.getStr("description", "");
                lines.add(String.format("[%d] %s\n   网址: %s\n   摘要: %s", i + 1, title, url, snippet));
            }
            return String.join("\n\n", lines);
        } catch (Exception e) {
            log.error("解析 You.com 搜索结果失败: {}", e.getMessage(), e);
            return "解析搜索结果失败: " + e.getMessage();
        }
    }

    @Override
    public String getToolName() {
        return "youcomSearch";
    }

    @Override
    public String getDisplayName() {
        return "联网搜索";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String query = arguments.getStr("query");
        return String.format("[工具调用] %s 搜索关键词: %s", getDisplayName(), query);
    }
}
