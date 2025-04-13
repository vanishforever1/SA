package net.lab1024.sa.base.module.support.apiencrypt.service;

import cn.hutool.crypto.symmetric.SM4;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.constant.StringConst;
import net.lab1024.sa.base.module.support.apiencrypt.dao.EncryptionConfigMapper;
import net.lab1024.sa.base.module.support.apiencrypt.domain.EncryptionConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.Base64;

import static cn.hutool.core.convert.Convert.hexToBytes;

@Slf4j
//@Service
public class ApiEncryptServiceSmImpl implements ApiEncryptService {

    private static final String CHARSET = "UTF-8";

    @Autowired
    private EncryptionConfigService encryptionConfigService;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 获取当前SM4密钥
    private String getSm4Key() {
        EncryptionConfig config = encryptionConfigService.getActiveConfig("SM4");
        if (config == null) {
            throw new RuntimeException("SM4 encryption config not found");
        }
        return config.getCurrentKey();
    }



    @Override
    public String encrypt(String data) {
        try {
            String sm4Key = getSm4Key();
            SM4 sm4 = new SM4(hexToBytes(sm4Key));
            String encryptHex = sm4.encryptHex(data);
            return Base64.getEncoder().encodeToString(encryptHex.getBytes(CHARSET));
        } catch (Exception e) {
            log.error("SM4 encrypt error: {}", e.getMessage(), e);
            return StringConst.EMPTY;
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            String sm4Key = getSm4Key();
            byte[] base64Decode = Base64.getDecoder().decode(data);
            SM4 sm4 = new SM4(hexToBytes(sm4Key));
            return sm4.decryptStr(new String(base64Decode));
        } catch (Exception e) {
            log.error("SM4 decrypt error: {}", e.getMessage(), e);
            return StringConst.EMPTY;
        }
    }
}