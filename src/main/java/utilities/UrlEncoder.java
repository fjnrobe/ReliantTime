package utilities;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UrlEncoder {

    private  static String EncoderKey = "reliant1";

    // Algorithm used
    private final static String ALGORITHM = "DES";

    /**
     * Encrypt data
     * @param data		-	data to encrypt
     * @return	Encrypted data
     * @throws Exception
     */
    public static String cipher(String data) {
        // Key has to be of length 8
//        if (secretKey == null || secretKey.length() != 8)
//            throw new Exception("Invalid key length - 8 bytes key needed!");

        String retValue = "";
        try {
            SecretKey key = new SecretKeySpec(EncoderKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            retValue = toHex(cipher.doFinal(data.getBytes()));
        } catch (Exception e)
        {
           //
        }
        return retValue;
    }

    /**
     * Decrypt data
     * @param data		-	data to decrypt
     * @return	Decrypted data
     * @throws Exception
     */
    public static String decipher(String data) throws Exception {
        // Key has to be of length 8
//        if (secretKey == null || secretKey.length() != 8)
//            throw new Exception("Invalid key length - 8 bytes key needed!");

        SecretKey key = new SecretKeySpec(EncoderKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return new String(cipher.doFinal(toByte(data)));
    }

    // Helper methods

    private static byte[] toByte(String hexString) {
        int len = hexString.length()/2;

        byte[] result = new byte[len];

        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] stringBytes) {
        StringBuffer result = new StringBuffer(2*stringBytes.length);

        for (int i = 0; i < stringBytes.length; i++) {
            result.append(HEX.charAt((stringBytes[i]>>4)&0x0f)).append(HEX.charAt(stringBytes[i]&0x0f));
        }

        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    // Helper methods - end

//    /**
//     * Quick test
//     * @param args
//     */
//    public static void main(String[] args) {
//        try {
//            String data 		= "Secret data string. The password is 'secret'";
//            String secretKey  	= "01234567";
//
//            String encryptedData = cipher(secretKey, data);
//
//            System.out.println("encryptedData: " + encryptedData);
//
//            String decryptedData = decipher(secretKey, encryptedData);
//
//            System.out.println("decryptedData: " + decryptedData);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
