package com.hiwoo.controller.system;

import com.hiwoo.api.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/system/users")
@Slf4j
public class UserController {



    @Value("${RSA.privateKey}")
    private String privateKey;

    @Value("${RSA.publicKey}")
    private String publicKey;


    @GetMapping("/get-by-account/{account}")
    public Result<?> getUserInfoByAccount(@PathVariable String account) {
        return null;
    }
}

