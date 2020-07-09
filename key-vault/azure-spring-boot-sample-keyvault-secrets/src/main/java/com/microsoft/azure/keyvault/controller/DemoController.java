package com.microsoft.azure.keyvault.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class DemoController {

    static List<String> ROLE_LIST = new ArrayList<>();

    static {
        Random r = new Random();
        for (int i=0; i < 10; i++) {
            ROLE_LIST.add("ROLE-TEST-" + r.nextLong());
        }
    }

    @Value("${keyVaultSecretName}")
    private String keyVaultSecretName;

    @PostMapping(value= "/gzip", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SecurityProperties.User gzip(){
        SecurityProperties.User user = new SecurityProperties.User();
        user.setName("test");
        user.setPassword(keyVaultSecretName);
        user.setRoles(ROLE_LIST);
        return user;
    }
}
