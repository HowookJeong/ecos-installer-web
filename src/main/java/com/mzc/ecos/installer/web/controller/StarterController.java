package com.mzc.ecos.installer.web.controller;

import com.mzc.ecos.installer.web.config.model.ApplicationConfigModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Log4j2
@RequestMapping("/starter")
@RequiredArgsConstructor
public class StarterController {

  private final ApplicationConfigModel appConfigModel;

  @GetMapping("")
  public ModelAndView index() {
    ModelAndView mav = new ModelAndView();

    mav.addObject("config", appConfigModel);
    mav.setViewName("starter/elasticsearch.html");

    return mav;
  }

  @GetMapping("/elasticsearch")
  public ModelAndView elasticsearch() {
    return index();
  }

  @GetMapping("/kibana")
  public ModelAndView kibana() {
    ModelAndView mav = new ModelAndView();

    mav.addObject("config", appConfigModel);
    mav.setViewName("starter/kibana.html");

    return mav;
  }
}
