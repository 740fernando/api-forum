package br.com.alura.forum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    //@RequestMapping - Denomina a URL @ResponseBody- Responsavel por retornar a frase "Hello World"


        @RequestMapping("/")
        @ResponseBody
        public String hello(){
            return "Hello World";
        }
    }

