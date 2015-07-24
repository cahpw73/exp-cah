package ch.swissbytes.fqmes.util;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alvaro on 9/2/14.
 */
public class Encode {

    private static final Logger log = Logger.getLogger(Encode.class.getSimpleName());

    public static String encode(String text){

        BASE64Encoder encoder = new BASE64Encoder();
        String encodedBytes = null;
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((text.getBytes("UTF-8")));
            byte[] digest = messageDigest.digest();
            encodedBytes = encoder.encode(digest);

        }catch (NoSuchAlgorithmException | UnsupportedEncodingException nsa){
            log.log(Level.SEVERE, nsa.getMessage());
        }
        return encodedBytes;
    }

    public static void main(String[] args) {
        BigDecimal amountIni = new BigDecimal("1500");
        BigDecimal exchangeRateIni = new BigDecimal("6.89965");
        BigDecimal exchangeRateEnd = new BigDecimal("1.37389");
        Util util = new Util();
        System.out.println("Currency convert BigDecimal: "  + util.currencyToCurrency(amountIni, exchangeRateIni, exchangeRateEnd).setScale(5, RoundingMode.CEILING));

        Double amountIni2 = 1500D;
        Double exchangeRateIni2 = 6.89965D;
        Double exchangeRateEnd2 = 1.37389D;

        System.out.println("Currency convert Double: "  + util.currencyToCurrency2(amountIni2, exchangeRateIni2, exchangeRateEnd2));
    }

}
