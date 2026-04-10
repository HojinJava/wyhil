package com.hnix.sd.core.utils;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurtyUtil {
	
	private static String iv = "HNIXASEKEY012345";
	
	public static String sha256(String pw) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update((pw ).getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
		byte[] pwSalt = md.digest();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pwSalt.length; i++) {
			sb.append(String.format("%2X", pwSalt[i])); 
		}
		return sb.toString();
	}
	
	public static String sha512(String pw) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update((pw ).getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
		byte[] pwSalt = md.digest();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pwSalt.length; i++) {
			sb.append(String.format("%2X", pwSalt[i])); 
		}
		return sb.toString();
	}
	
    /**
     * AES 256 방식으로 암호화
     *
     * @param str
     *            : 입력 문자
     * @param key
     *            : 키 16~ 32자리
     * @return
     */
    public static String encodingAes256(String str, String key) {
        String sRet = "";
        
        try {
            byte[] textBytes = str.getBytes("UTF-8");

            Key newKey = getAESKey(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, newKey, new IvParameterSpec(iv.getBytes("UTF-8")));
            sRet = Base64.encodeBase64String(cipher.doFinal(textBytes));
        } catch (Exception e) {
            sRet = "";
        }
        return sRet;
    }

    /**
     *
     * @param str
     * @param key
     * @return
     */
    public static String decodingAes256(String str, String key) {
        String sRet = "";
        
        try {
            byte[] textBytes = Base64.decodeBase64(str.getBytes("UTF-8"));

            Key newKey = getAESKey(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, newKey, new IvParameterSpec(iv.getBytes("UTF-8")));
            sRet = new String(cipher.doFinal(textBytes), "UTF-8");
        } catch (Exception e) {
            
            sRet = "";
        }
        return sRet;
    }
    
    public static Key getAESKey(String key) throws Exception {
        Key keySpec;
        

        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");

        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        System.arraycopy(b, 0, keyBytes, 0, len);
        keySpec = new SecretKeySpec(keyBytes, "AES");

        return keySpec;
    }
}
