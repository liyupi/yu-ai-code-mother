package com.yupi.yuaicodemother.mapper;


import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.yupi.yuaicodemother.model.entity.CodeQuality;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 查询代码质量分析的结果mapper
 * @email "Zewang0217@outlook.com"
 * @date 2025/12/27 22:31
 */
public interface CodeQualityMapper extends BaseMapper<CodeQuality> {

    CodeQuality selectOneByCondition(QueryWrapper queryWrapper);
}
