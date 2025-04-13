package net.lab1024.sa.base.module.support.apiencrypt.service;

import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.constant.StringConst;
import net.lab1024.sa.base.module.support.apiencrypt.dao.EncryptionConfigMapper;
import net.lab1024.sa.base.module.support.apiencrypt.domain.EncryptionConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Base64;

@Slf4j
@Service
public class ApiEncryptServiceAesImpl implements ApiEncryptService {

    private static final String CHARSET = "UTF-8";

    // 注入配置服务
    @Autowired
    private EncryptionConfigService encryptionConfigService;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 获取当前AES密钥
    private String getAesKey() {
        EncryptionConfig config = encryptionConfigService.getActiveConfig("AES");
        if (config == null) {
            throw new RuntimeException("AES encryption config not found");
        }
        return config.getCurrentKey();
    }

    @Override
    public String encrypt(String data) {
        try {
            String aesKey = getAesKey();
            AES aes = new AES(aesKey.getBytes(CHARSET));
            return aes.encryptBase64(data);
        } catch (Exception e) {
            log.error("AES encrypt error: {}", e.getMessage(), e);
            return StringConst.EMPTY;
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            String aesKey = getAesKey();
            byte[] base64Decode = Base64.getDecoder().decode(data);
            AES aes = new AES(aesKey.getBytes(CHARSET));
            byte[] decryptedBytes = aes.decrypt(base64Decode);
            return new String(decryptedBytes, CHARSET);
        } catch (Exception e) {
            log.error("AES decrypt error: {}", e.getMessage(), e);
            return StringConst.EMPTY;
        }
    }
}