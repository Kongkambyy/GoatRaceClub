package com.example.goatraceclub;

import com.example.goatraceclub.utils.HashUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoatRaceClubApplication {

    public static void main(String[] args) {
        System.out.println(HashUtil.hashSHA256("bøsserøv"));
    }

}
