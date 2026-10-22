package com.netraa.aopdemo.controller;

import com.netraa.aopdemo.service.AopService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AopController {

    private AopService aopService;

    public AopController(AopService aopService) {
        this.aopService = aopService;
    }




}
