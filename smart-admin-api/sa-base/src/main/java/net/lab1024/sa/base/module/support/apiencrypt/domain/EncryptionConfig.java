package net.lab1024.sa.base.module.support.apiencrypt.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("encryption_config")
public class EncryptionConfig {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer id;
    private String algorithmType;
    private String currentKey;
    private Boolean isActive;
    private LocalDateTime updateTime;
}