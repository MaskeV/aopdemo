package com.netraa.aopdemo.service;

import org.springframework.stereotype.Service;

@Service
public class AggregateService {


    public double avgRestimeperMin(int totalRestime, int count){

        double avgRestimeperMin = totalRestime/(count*0.1);
        System.out.println(avgRestimeperMin);
        return avgRestimeperMin;


    }

}
