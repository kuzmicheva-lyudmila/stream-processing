package ru.kl.auth.service.implement;

import org.springframework.stereotype.Service;
import ru.kl.auth.service.PasswordEncoderService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
public class DefaultPasswordEncoderService implements PasswordEncoderService {

    private static final String SECRET = "mySecret";

    private static final Integer ITERATION = 33;

    private static final Integer KEY_LENGTH = 256;

    @Override
    public String encode(CharSequence password) {
        try {
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                    .generateSecret(
                            new PBEKeySpec(password.toString().toCharArray(), SECRET.getBytes(), ITERATION, KEY_LENGTH)
                    )
                    .getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean verify(CharSequence actual, String expected) {
        return encode(actual).equals(expected);
    }
}
