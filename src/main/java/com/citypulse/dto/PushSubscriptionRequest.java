package com.citypulse.dto;

public class PushSubscriptionRequest {

    private String endpoint;
    private String p256dh;
    private String auth;
    private Long userId;

    public PushSubscriptionRequest() {
    }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getP256dh() { return p256dh; }
    public void setP256dh(String p256dh) { this.p256dh = p256dh; }

    public String getAuth() { return auth; }
    public void setAuth(String auth) { this.auth = auth; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}