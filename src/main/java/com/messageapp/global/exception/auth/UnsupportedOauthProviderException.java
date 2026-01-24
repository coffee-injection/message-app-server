package com.messageapp.global.exception.auth;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class UnsupportedOauthProviderException extends AppException {

    public UnsupportedOauthProviderException() {
        super(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
    }

    public UnsupportedOauthProviderException(String additionalMessage) {
        super(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER, additionalMessage);
    }
}
