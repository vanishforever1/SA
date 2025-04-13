package net.lab1024.sa.base.module.support.apiencrypt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.apiencrypt.dao.EncryptionConfigMapper;
import net.lab1024.sa.base.module.support.apiencrypt.domain.EncryptionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EncryptionConfigService {

    @Autowired
    private EncryptionConfigMapper encryptionConfigMapper;


    @Cacheable(value = "encryptionConfig", key = "#algorithmType")
    public EncryptionConfig getActiveConfig(String algorithmType) {
        LambdaQueryWrapper<EncryptionConfig> query = new LambdaQueryWrapper<>();
        query.eq(EncryptionConfig::getAlgorithmType, algorithmType)
                .eq(EncryptionConfig::getIsActive, true);

        // 添加调试日志
        log.info("查询加密配置：算法类型={}", algorithmType);
        EncryptionConfig config = encryptionConfigMapper.selectOne(query);
        log.info("查询结果：{}", config);

        return config;
    }

    @CacheEvict(value = "encryptionConfig", key = "#algorithmType")
    public ResponseDTO<String> updateKey(String algorithmType, String newKey) {
        EncryptionConfig config = getActiveConfig(algorithmType);
        if (config == null) {
            return ResponseDTO.userErrorParam("算法未启用");
        }
        config.setCurrentKey(newKey);
        encryptionConfigMapper.updateById(config);
        return ResponseDTO.ok("密钥更新成功");
    }

    // EncryptionConfigService.java（新增方法）
    public ResponseDTO<List<String>> getActiveAlgorithms() {
        LambdaQueryWrapper<EncryptionConfig> query = new LambdaQueryWrapper<>();
        query.select(EncryptionConfig::getAlgorithmType)
                .eq(EncryptionConfig::getIsActive, true);

        try {
            List<EncryptionConfig> configs = encryptionConfigMapper.selectList(query);
            List<String> algorithms = configs.stream()
                    .map(EncryptionConfig::getAlgorithmType)
                    .distinct()
                    .collect(Collectors.toList());
            return ResponseDTO.ok(algorithms);
        } catch (Exception e) {
            log.error("获取激活算法失败", e);
            return ResponseDTO.userErrorParam("获取激活算法失败");
        }
    }
}

