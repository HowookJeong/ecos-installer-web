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
@RequestMapping("/management")
@RequiredArgsConstructor
public class ManagementController {

  private final ApplicationConfigModel appConfigModel;

  @GetMapping("/elasticsearch")
  public ModelAndView elasticsearch() {
    ModelAndView mav = new ModelAndView();

    mav.addObject("config", appConfigModel);
    mav.setViewName("management/elasticsearch.html");

    return mav;
  }

  @GetMapping("/kibana")
  public ModelAndView kibana() {
    ModelAndView mav = new ModelAndView();

    mav.addObject("config", appConfigModel);
    mav.setViewName("management/kibana.html");

    return mav;
  }
}
