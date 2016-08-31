package com.lezhi.demo.ifc.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.BindingResult;


public class BaseAction {
	public Map<String,String> validateParameter(BindingResult result){
		Map<String, String> retval = new HashMap<String, String>();
		retval.put("status","1");
		retval.put("msg", "参数错误："
			+ (result.getErrorCount() > 0 ? result.getAllErrors()
			.get(0).getDefaultMessage() : ""));
		return retval;
	}
	
	public Map<String,String> returnResult(String status,String msg){
		Map<String, String> retval = new HashMap<String, String>();
		retval.put("status",status);
		retval.put("msg",msg);
		return retval;
	} 
}
