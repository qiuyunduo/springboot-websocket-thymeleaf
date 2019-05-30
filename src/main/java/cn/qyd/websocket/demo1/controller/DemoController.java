package cn.qyd.websocket.demo1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author qyd
 * @Date 19-5-30 上午11:54
 **/
@Controller
@RequestMapping("/")
public class DemoController {

    @GetMapping("/index")
    public ModelAndView hello(ModelAndView modelAndView) {
        modelAndView.setViewName("/index");
        modelAndView.addObject("city","北京朝阳区");
        modelAndView.addObject("name","邱运铎");
        ArrayList<String> list = new ArrayList<>(Arrays.asList("apple","orange", "banner"));
        modelAndView.addObject("list",list);
        return modelAndView;
    }
}
