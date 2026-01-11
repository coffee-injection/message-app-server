package com.messageapp.domain.auth.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("verified_email")
    private Boolean verifiedEmail;

    @JsonProperty("name")
    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("picture")
    private String picture;

    /**
     * 가상 이메일 생성
     * 형식: google_{구글_ID}@messageapp.com
     */
    public String generateVirtualEmail() {
        return "google_" + this.id + "@messageapp.com";
    }
}
