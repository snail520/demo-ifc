package com.lezhi.demo.ifc.action;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lezhi.demo.biz.service.LoginService;
import com.lezhi.demo.model.ifc.LoginPojo;
import com.lezhi.demo.model.validation.User;


@RestController
@RequestMapping(value = "", produces = { "application/json;charset=UTF-8" })
public class UserAction extends BaseAction{
	@Autowired
	private LoginService loginService;
	
	@RequestMapping(value = "/login")
	public Object login(@Valid LoginPojo login, BindingResult result) throws Exception{
		if(result.hasErrors()){
			return validateParameter(result);
		}
		Map retval = new HashMap();
		try{
			User user = loginService.findUser(login.getLoginName());
			if(user !=null){
				if(login.getPassword().equals(user.getPassword())){
					return returnResult("0","登录成功");
				}else{
					return returnResult("1","密码错误");
				}
			}else{
				return returnResult("1","用户不存在");
			}
		}catch(Exception e){
			e.printStackTrace();
			return returnResult("1","系统异常");
		}
	}
}
