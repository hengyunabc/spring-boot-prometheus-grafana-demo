package com.example.exposeprometheusdemo;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author hengyunabc
 *
 */
@Controller
public class HelloWorldController {

    @RequestMapping("/")
    @ResponseBody
    public String hello() {
        return "hello world " + new Date();
    }

}
