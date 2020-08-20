package com.reimu.shiro;

import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Use Shiro to create password hash
 */
@Component
public class PasswordService {

    @Autowired
    private static   ConstantPublicGenerator constantPublicGenerator;

    @Autowired
    public  void setConstantPublicGenerator(ConstantPublicGenerator constantPublicGenerator) {
        PasswordService.constantPublicGenerator = constantPublicGenerator;
    }

    public static  String encrypt(char[] originalPassword, String privateSalt){
        DefaultHashService hashService = new DefaultHashService(); //默认算法SHA-512
        hashService.setHashAlgorithmName("SHA-512");
        hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false
        hashService.setRandomNumberGenerator(constantPublicGenerator);//用于生成公盐。默认就这个
        hashService.setPrivateSalt(new SimpleByteSource(privateSalt));
        HashRequest request = new HashRequest.Builder().setSource(originalPassword)
                .setSalt(ByteSource.Util.bytes(privateSalt)).setIterations(2).build();
        return hashService.computeHash(request).toString();
    }
}
