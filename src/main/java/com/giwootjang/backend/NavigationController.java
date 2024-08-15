package com.giwootjang.backend;

import jakarta.websocket.server.PathParam;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class NavigationController {

    @RequestMapping("/index-page")
    public String index(@PathParam("name") String name, ModelMap modelMap) {
        System.out.println("param name=" + name);
        modelMap.put("name",name);
        return "test";
    }

    @RequestMapping("/redirect")
    public String redirect() {
        return "redirect:index-page";
    }

    @RequestMapping("/forward")
    public String forward() {
        return "forward:index-page";
    }
}