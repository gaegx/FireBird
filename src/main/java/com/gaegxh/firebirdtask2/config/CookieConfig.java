package com.gaegxh.firebirdtask2.config;

import kong.unirest.Header;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Getter;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
public class CookieConfig {

    private volatile String cookies = "";

    @PostConstruct
    public void init() {
        refreshCookies();
    }

    public void refreshCookies() {
        try {
            HttpResponse<String> response = Unirest.get("https://rail.ninja")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                    .header("sec-ch-ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"Windows\"")
                    .asString();

            if (response.getStatus() == 200) {
                List<Header> allHeaders = response.getHeaders().all();

                List<String> setCookieHeaders = allHeaders.stream()
                        .filter(h -> h.getName().equalsIgnoreCase("Set-Cookie"))
                        .map(Header::getValue)
                        .collect(Collectors.toList());

                if (!setCookieHeaders.isEmpty()) {
                    cookies = setCookieHeaders.stream()
                            .map(cookie -> cookie.split(";", 2)[0].trim()) // только "key=value"
                            .collect(Collectors.joining("; "));
                    System.out.println("Updated cookies: " + cookies);
                } else {
                    System.err.println("No Set-Cookie headers found");
                    cookies = "";
                }
            } else {
                System.err.println("Request failed with status: " + response.getStatus());
                cookies = "";
            }
        } catch (Exception e) {
            System.err.println("Error refreshing cookies: " + e.getMessage());
            cookies = "";
        }
    }

    public void setSessionCookie(String setCookieHeader) {
        this.cookies = setCookieHeader;

    }


    public String getCookie() {
        return cookies;
    }
}