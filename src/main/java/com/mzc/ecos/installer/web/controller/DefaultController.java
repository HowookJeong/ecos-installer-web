package com.mzc.ecos.installer.web.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Log4j2
@RequestMapping("/")
public class DefaultController {

  @GetMapping("index")
  public ModelAndView index() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("index");

    return mav;
  }
}
