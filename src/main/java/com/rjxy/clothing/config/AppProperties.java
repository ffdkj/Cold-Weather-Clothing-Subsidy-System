package com.rjxy.clothing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private List<String> adminWhitelist = new ArrayList<>();
    public List<String> getAdminWhitelist() {
        return adminWhitelist;
    }
    public void setAdminWhitelist(List<String> adminWhitelist) {
        this.adminWhitelist = adminWhitelist;
    }
}
