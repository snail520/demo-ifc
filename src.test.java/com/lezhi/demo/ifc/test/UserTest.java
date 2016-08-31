package com.lezhi.demo.ifc.test;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

public class UserTest extends TestObject{
	
	@Test
	@Rollback(true)
	public void testUserLoginSuccess() throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		String url = "/login";
		param.put("appId","lezhiwang");
		param.put("source","pc");
		param.put("loginName","gaox");
		param.put("password","888888");
		Map<String,Object> retval = requestReturn(url, param, Map.class);
		System.out.println(retval);
		Assert.assertThat(retval, Matchers.hasKey("status"));
		Assert.assertThat(retval.get("status").toString(), Matchers.containsString("0"));
	}
}
