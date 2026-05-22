package org.george.aop;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encrypt {

    /**
     * 对源字符串进行MD5加密
     *
     * @param source 需要加密的源字符串
     * @return 加密后的32位十六进制字符串
     */
    public static String encrypt(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                // 将字节转换为无符号整数（0-255），并转为16进制字符串
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
