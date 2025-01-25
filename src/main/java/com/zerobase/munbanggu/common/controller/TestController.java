package com.zerobase.munbanggu.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class TestController {

    @GetMapping("/")
    public String test() {
        return "문방구 프로젝트";
    }
}
