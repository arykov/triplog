package com.ryaltech.ww.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("whitewater")
public class TripLogService {
	@Autowired
	private RiverDao dao;
	
	@RequestMapping("test/{value}")
	public @ResponseBody String test(@PathVariable("value") String value){
		dao.getRiversByName("Hello");
		return value;
	}
	

}
