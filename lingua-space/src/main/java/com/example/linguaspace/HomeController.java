
package com.example.linguaspace;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/vocabulary")
    public String vocabulary() {
        return "vocabulary";
    }

    @GetMapping("/korean")
    public String korean() {
        return "korean";
    }
}
