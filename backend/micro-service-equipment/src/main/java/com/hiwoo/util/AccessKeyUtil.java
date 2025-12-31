package com.hiwoo.util;

import java.security.SecureRandom;
import java.util.Base64;

public class AccessKeyUtil {
    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * 生成AccessKey和AccessSecret对
     * @return 包含AccessKey和AccessSecret的KeyPair对象
     */
    public static KeyPair generateKeyPair() {
        return new KeyPair(generateAccessKey(), generateAccessSecret());
    }

    /**
     * 生成AccessKey
     * @return String类型的AccessKey
     */
    public static String generateAccessKey() {
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        return "ak-" + encoder.encodeToString(buffer);
    }

    /**
     * 生成AccessSecret
     * @return String类型的AccessSecret
     */
    public static String generateAccessSecret() {
        byte[] buffer = new byte[40];
        random.nextBytes(buffer);
        return "sk-" + encoder.encodeToString(buffer);
    }

    /**
     * KeyPair数据类
     */
    public static class KeyPair {
        private final String accessKey;
        private final String accessSecret;

        public KeyPair(String accessKey, String accessSecret) {
            this.accessKey = accessKey;
            this.accessSecret = accessSecret;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public String getAccessSecret() {
            return accessSecret;
        }

        @Override
        public String toString() {
            return "KeyPair{" +
                    "accessKey='" + accessKey + '\'' +
                    ", accessSecret='" + accessSecret + '\'' +
                    '}';
        }
    }
}
