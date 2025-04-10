package net.lab1024.sa.base.module.support.apiencrypt.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class KeyUpdateDTO {
    @NotBlank(message = "算法类型不能为空")
    private String algorithmType;

    @NotBlank(message = "新密钥不能为空")
    @Size(min = 16, max = 32, message = "密钥长度需在16-32位之间")
    private String newKey;
}