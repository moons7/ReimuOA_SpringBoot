package com.reimu.shiro;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPublicGenerator implements RandomNumberGenerator {

    @Value("${project.publicSalt}")
    private String publicSalt;

    @Override
    public ByteSource nextBytes() {
        return ByteSource.Util.bytes(publicSalt);
    }

    @Override
    public ByteSource nextBytes(int i) {
        return  ByteSource.Util.bytes(publicSalt);
    }
}
