package com.hiwoo.controller.system;

import com.hiwoo.api.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/demo/systems")
public class SystemController {

    @GetMapping("/get-by-account/{account}")
    public Result<?> getUserInfoByAccount(@PathVariable String account) {
        return null;
    }
}

