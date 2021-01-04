package ru.kl.auth.service;

public interface PasswordEncoderService {

    String encode(CharSequence password);
    boolean verify(CharSequence actual, String expected);
}
