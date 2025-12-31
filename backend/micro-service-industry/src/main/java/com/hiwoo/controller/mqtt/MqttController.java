package com.hiwoo.controller.mqtt;

import com.hiwoo.api.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/demo/mqtts")
public class MqttController {

    @GetMapping("/get-by-account/{account}")
    public Result<?> getUserInfoByAccount(@PathVariable String account) {
        return null;
    }
}

