package net.lab1024.sa.base.module.support.apiencrypt.controller;

import lombok.Data;
import net.lab1024.sa.base.common.annoation.NoNeedLogin;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.apiencrypt.domain.EncryptionConfig;
import net.lab1024.sa.base.module.support.apiencrypt.domain.KeyUpdateDTO;
import net.lab1024.sa.base.module.support.apiencrypt.service.EncryptionConfigService;
import net.lab1024.sa.base.module.support.apiencrypt.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

import static cn.dev33.satoken.SaManager.log;

@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {

    @Autowired
    private EncryptionConfigService encryptionService;

    @PostMapping("/updateKey")
    public ResponseDTO<String> updateKey(@RequestBody @Valid KeyUpdateDTO keyUpdateDTO) {
        return encryptionService.updateKey(keyUpdateDTO.getAlgorithmType(), keyUpdateDTO.getNewKey());
    }

    @PostMapping("/currentKey")
    @NoNeedLogin
    public ResponseDTO<String> getCurrentKey(@RequestBody @Valid KeyRequestDTO request) {
        // 增加算法类型校验
        if (!"SM4".equals(request.getAlgorithm()) && !"AES".equals(request.getAlgorithm())) {
            return ResponseDTO.userErrorParam("不支持的算法类型");
        }

        EncryptionConfig config = encryptionService.getActiveConfig(request.getAlgorithm());
        if (config == null) {
            return ResponseDTO.userErrorParam("算法未激活");
        }

        try {
            // 增加密钥长度校验
            if ("SM4".equals(request.getAlgorithm()) && config.getCurrentKey().length() != 32) {
                return ResponseDTO.userErrorParam("SM4密钥长度必须为32位");
            }

            String encryptedKey = RSAUtil.encryptWithPublicKey(config.getCurrentKey(), request.getPublicKey());
            return ResponseDTO.ok(encryptedKey);
        } catch (Exception e) {
            log.error("密钥加密失败：", e);
            return ResponseDTO.userErrorParam("加密失败：" + e.getMessage());
        }
    }

    @Data
    public static class KeyRequestDTO {
        private String algorithm;
        private String publicKey;
    }
}