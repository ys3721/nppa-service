package com.iceicelee.nppaservice.constants;

/**
 * @author yaoshuai
 * @since 2021-四月-06
 */
public class AuthenticationConstants {

    /**
     * 0
     * ：认证成功
     * 1
     * ：认证中
     * 2
     * ：认证失败
     */
    public enum AuthenticationStatus {
        /**
         * 认证成功
         */
        SUCCESS(0),
        /**
         * 认证中
         */
        UNDER_WAY(1),
        /**
         * 认证失败
         */
        FAIL(2),
        ;
        int code;

        AuthenticationStatus(int code) {
            this.code = code;
        }

        ;

        public static AuthenticationStatus codeOf(int authStatus) {
            for (AuthenticationStatus v : AuthenticationStatus.values()) {
                if (v.getCode() == authStatus) {
                    return v;
                }
            }
            return null;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }


}
