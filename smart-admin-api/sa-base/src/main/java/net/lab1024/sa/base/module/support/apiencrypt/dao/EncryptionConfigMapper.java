package net.lab1024.sa.base.module.support.apiencrypt.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.base.module.support.apiencrypt.domain.EncryptionConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EncryptionConfigMapper extends BaseMapper<EncryptionConfig> {
}