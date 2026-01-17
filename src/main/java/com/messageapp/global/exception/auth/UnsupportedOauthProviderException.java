package com.messageapp.global.exception.auth;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class UnsupportedOauthProviderException extends AppException {

    public static final AppException EXCEPTION = new UnsupportedOauthProviderException();

    private UnsupportedOauthProviderException() {
        super(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
    }
}
