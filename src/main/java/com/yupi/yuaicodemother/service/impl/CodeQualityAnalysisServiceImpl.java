package com.yupi.yuaicodemother.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yupi.yuaicodemother.constant.AppConstant;
import com.yupi.yuaicodemother.core.CodeQualityAnalysisFacade;
import com.yupi.yuaicodemother.core.handler.StreamHandlerExecutor;
import com.yupi.yuaicodemother.exception.BusinessException;
import com.yupi.yuaicodemother.exception.ErrorCode;
import com.yupi.yuaicodemother.exception.ThrowUtils;
import com.yupi.yuaicodemother.mapper.AppMapper;
import com.yupi.yuaicodemother.mapper.CodeQualityMapper;
import com.yupi.yuaicodemother.model.entity.App;
import com.yupi.yuaicodemother.model.entity.CodeQuality;
import com.yupi.yuaicodemother.model.vo.CodeQualityVO;
import com.yupi.yuaicodemother.service.CodeQualityAnalysisService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 代码质量分析 服务层实现类
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 22:58
 */

@Slf4j
@Service
public class CodeQualityAnalysisServiceImpl extends ServiceImpl<CodeQualityMapper, CodeQuality> implements
    CodeQualityAnalysisService {

    @Resource
    private CodeQualityAnalysisFacade codeQualityAnalysisFacade;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private AppMapper appMapper;

    public CodeQualityVO analyzeCodeQuality(String code, Long appId, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 错误");
        ThrowUtils.throwIf(StrUtil.isBlank(code), ErrorCode.PARAMS_ERROR, "代码内容不能为空");

        log.info("开始分析代码质量，appId：{}", appId);
        long startTime = System.currentTimeMillis();

        try {
            // 调用AI服务进行分析
            CodeQualityVO result = codeQualityAnalysisFacade.analyzeCodeQuality(code, appId);

            // 保存结果
            CodeQuality codeQuality = convertToEntity(result, appId);
            this.save(codeQuality);

            long endTime = System.currentTimeMillis();
            log.info("代码质量分析结束，耗时：{}ms，appId：{}，整体评分：{} ", endTime - startTime, appId
            , result.getOverallScore());
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("代码质量分析失败，appId：{}, 耗时：{}", appId, endTime - startTime, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码质量分析失败");
        }
    }

    public Flux<String> analyzeCodeQualityFlux(String code, Long appId, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 错误");
        ThrowUtils.throwIf(StrUtil.isBlank(code), ErrorCode.PARAMS_ERROR, "代码内容不能为空");

        log.info("开始流式分析代码质量，appId：{}", appId);

        Flux<String> codeStream = codeQualityAnalysisFacade.analyzeCodeQualityStream(code, appId);

        // 收集AI响应的内容
        return streamHandlerExecutor.doExecute(codeStream, appId);
    }

    // 将VO转换为实体
    private CodeQuality convertToEntity(CodeQualityVO codeQualityVO, Long appId) {
        return CodeQuality.builder()
            .appId(appId)
            .completeness(codeQualityVO.getCompleteness())
            .completenessComment(codeQualityVO.getCompletenessComment())
            .codeQuality(codeQualityVO.getCodeQuality())
            .codeQualityComment(codeQualityVO.getCodeQualityComment())
            .overallScore(codeQualityVO.getOverallScore())
            .overallComment(codeQualityVO.getOverallComment())
            .build();
    }

    @Override
    public CodeQualityVO getLatestCodeQualityVOByAppId(Long appId) {
        // 实现获取最新代码质量分析结果的逻辑
        // 查询数据库中指定应用ID的最新代码质量分析结果
        
        // 构建查询条件
        QueryWrapper queryWrapper = com.mybatisflex.core.query.QueryWrapper.create();
        queryWrapper.eq("appId", appId)  // 等值查询
                  .orderBy("createTime", false);  // 按创建时间降序排列，获取最新的

        // 执行查询
        CodeQuality latestCodeQuality = this.getMapper().selectOneByCondition(queryWrapper);

        if (latestCodeQuality == null) {
            return null; // 或抛出异常，取决于业务需求
        }

        // 将实体转换为VO
        return convertToVO(latestCodeQuality);
    }

    // 将实体转换为VO
    private CodeQualityVO convertToVO(CodeQuality codeQuality) {
        CodeQualityVO codeQualityVO = new CodeQualityVO();
        codeQualityVO.setId(codeQuality.getId());
        codeQualityVO.setCompleteness(codeQuality.getCompleteness());
        codeQualityVO.setCompletenessComment(codeQuality.getCompletenessComment());
        codeQualityVO.setCodeQuality(codeQuality.getCodeQuality());
        codeQualityVO.setCodeQualityComment(codeQuality.getCodeQualityComment());
        codeQualityVO.setOverallScore(codeQuality.getOverallScore());
        codeQualityVO.setOverallComment(codeQuality.getOverallComment());
        codeQualityVO.setCreateTime(codeQuality.getCreateTime());
        codeQualityVO.setUpdateTime(codeQuality.getUpdateTime());

        return codeQualityVO;
    }

    @Override
    public CodeQualityVO analyzeExistingAppCode(Long appId, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 错误");

        log.info("开始分析现有应用代码质量，appId：{}", appId);
        long startTime = System.currentTimeMillis();

        try {
            // 1. 查询应用信息
            App app = appMapper.selectOneById(appId);
            ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

            // 2. 从文件系统读取应用代码
            String code = readAppCodeFromDisk(appId, app.getCodeGenType());
            ThrowUtils.throwIf(StrUtil.isBlank(code), ErrorCode.NOT_FOUND_ERROR, "应用代码不存在或为空");

            // 3. 调用AI服务进行分析
            CodeQualityVO result = codeQualityAnalysisFacade.analyzeCodeQuality(code, appId);

            // 4. 保存结果
            CodeQuality codeQuality = convertToEntity(result, appId);
            this.save(codeQuality);

            long endTime = System.currentTimeMillis();
            log.info("现有应用代码质量分析结束，耗时：{}ms，appId：{}，整体评分：{}", 
                    endTime - startTime, appId, result.getOverallScore());
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("现有应用代码质量分析失败，appId：{}, 耗时：{}", appId, endTime - startTime, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码质量分析失败：" + e.getMessage());
        }
    }

    @Override
    public Flux<String> analyzeExistingAppCodeStream(Long appId, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 错误");

        log.info("开始流式分析现有应用代码质量，appId：{}", appId);

        try {
            // 1. 查询应用信息
            App app = appMapper.selectOneById(appId);
            ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

            // 2. 从文件系统读取应用代码
            String code = readAppCodeFromDisk(appId, app.getCodeGenType());
            ThrowUtils.throwIf(StrUtil.isBlank(code), ErrorCode.NOT_FOUND_ERROR, "应用代码不存在或为空");

            // 3. 调用AI服务进行流式分析
            Flux<String> codeStream = codeQualityAnalysisFacade.analyzeCodeQualityStream(code, appId);

            // 4. 收集AI响应的内容
            return streamHandlerExecutor.doExecute(codeStream, appId);
        } catch (Exception e) {
            log.error("流式分析现有应用代码质量失败，appId：{}", appId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码质量分析失败：" + e.getMessage());
        }
    }

    /**
     * 从文件系统读取应用代码
     *
     * @param appId 应用ID
     * @param codeGenType 代码生成类型
     * @return 代码内容
     */
    private String readAppCodeFromDisk(Long appId, String codeGenType) {
        try {
            // 根据代码生成类型确定目录名称
            String projectDirName = getProjectDirName(appId, codeGenType);
            Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);

            if (!Files.exists(projectRoot) || !Files.isDirectory(projectRoot)) {
                log.warn("应用代码目录不存在：{}", projectRoot);
                return "";
            }

            // 读取所有代码文件
            StringBuilder codeBuilder = new StringBuilder();
            try (Stream<Path> paths = Files.walk(projectRoot)) {
                List<Path> codeFiles = paths
                        .filter(Files::isRegularFile)
                        .filter(this::isCodeFile)
                        .sorted()
                        .collect(Collectors.toList());

                if (codeFiles.isEmpty()) {
                    log.warn("未找到代码文件，目录：{}", projectRoot);
                    return "";
                }

                for (Path filePath : codeFiles) {
                    String relativePath = projectRoot.relativize(filePath).toString();
                    String fileContent = Files.readString(filePath);
                    codeBuilder.append("=== 文件: ").append(relativePath).append(" ===\n");
                    codeBuilder.append(fileContent).append("\n\n");
                }
            }

            return codeBuilder.toString();
        } catch (Exception e) {
            log.error("读取应用代码失败，appId：{}", appId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取应用代码失败：" + e.getMessage());
        }
    }

    /**
     * 判断是否为代码文件
     *
     * @param path 文件路径
     * @return 是否为代码文件
     */
    private boolean isCodeFile(Path path) {
        String fileName = path.getFileName().toString();
        String extension = FileUtil.extName(fileName);
        
        // 支持的代码文件扩展名
        return List.of("java", "js", "ts", "vue", "html", "css", "json", "xml", "sql", "py", "go", "rs", "c", "cpp", "h")
                .contains(extension.toLowerCase());
    }

    /**
     * 根据代码生成类型获取项目目录名称
     *
     * @param appId 应用ID
     * @param codeGenType 代码生成类型
     * @return 项目目录名称
     */
    private String getProjectDirName(Long appId, String codeGenType) {
        if ("VUE_PROJECT".equals(codeGenType)) {
            return "vue_project_" + appId;
        } else if ("HTML".equals(codeGenType)) {
            return "html_" + appId;
        } else if ("MULTI_FILE".equals(codeGenType)) {
            return "multi_file_" + appId;
        } else {
            return "code_" + appId;
        }
    }
}