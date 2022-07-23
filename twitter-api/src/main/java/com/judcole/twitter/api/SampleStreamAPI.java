package com.judcole.twitter.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class SampleStreamAPI {
    @GetMapping(value="/test")
    @ResponseBody
    public String getMyValue(@RequestParam(required = false) String param, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin","http://localhost:5000");
        response.addHeader("Access-Control-Allow-Methods", "GET");
        return param;
    }
}
