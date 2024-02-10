package com.sc.hcv;

import com.sc.hcv.utils.RestApiUtils;

import java.io.IOException;

public class AllAcctsExcelReportClient {
    public static void main(String[] args) {
        String endpoint = "https://example.com/api/data";
        String jwtToken = "your_jwt_token_here";

        try {
            YourResponseObject responseObject = RestApiUtils.invokeApiWithJWT(endpoint, jwtToken, YourResponseObject.class);
            System.out.println("Account: " + responseObject.getAccount());
            System.out.println("Hostname: " + responseObject.getHostname());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class YourResponseObject {
        private String account;
        private String hostname;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
    }
}

