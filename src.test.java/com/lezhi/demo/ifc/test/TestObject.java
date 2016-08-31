package com.lezhi.demo.ifc.test;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

/**
 * 公共的单元测试的父类，提供了一些共有的测试方法
 * @author loeveol
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({  
    @ContextConfiguration(name = "parent", locations = {
    		"classpath:springDao.xml",
    		"classpath:springBiz.xml"}),  
    @ContextConfiguration(name = "child", locations = "classpath:springMVC.xml")  
})

@TransactionConfiguration(defaultRollback = true)
@Transactional
public class TestObject extends TestCase {

	@Autowired
	public WebApplicationContext wac;

	public MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	/**
	 * 进行Mock模拟请求，并返回ResultActions，此方法已对返回进行了http状态的验证，必须为200，否则测试失败
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected ResultActions requestResultActions(String url,
			Map<String, String> params) throws Exception{
		final MockHttpServletRequestBuilder builder = get(url).accept(MediaType.APPLICATION_JSON);;
		params.forEach((a,b)->{
			builder.param(a, b);
		});

		ResultActions ra = this.mockMvc.perform(builder).andExpect(status().isOk()).andDo(print());
		return ra;
	}
	
	/**
	 * 进行Mock模拟请求，并返回getModelAndView，此方法为结果的细化，根据实际情况使用，如果只需要返回结果的ModelAndView对象则使用此方法
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected ModelAndView requestModelView(String url,
			Map<String, String> params) throws Exception {
		
		return requestResultActions(url,params).andReturn().getModelAndView();
	}
	
	/**
	 * 进行Mock模拟请求，并返回结果Response的String形式，此方法主要用于测试只返回一句字符串
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected String requestReturnString(String url,
			Map<String, String> params) throws Exception {
		
		String result = requestResultActions(url,params).andReturn().getResponse().getContentAsString();
		return result;
	}
	
	/**
	 * 进行Mock模拟请求，并将结果Response的内容的Json格式转换为对象，对象可以为自定义Bean或Map等，但必须符合Json解析要求
	 * @param url
	 * @param params
	 * @param tt
	 * @return
	 * @throws Exception
	 */
	protected <T> T requestReturn(String url,
			Map<String, String> params,Class<T> tt) throws Exception {
		String result = requestReturnString(url,params);
		
		ObjectMapper om = new ObjectMapper();
		return om.readValue(result, tt);
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, Object> requestReturnMap(String url,
			Map<String, String> params) throws Exception{
		return requestReturn(url,params,Map.class);
	}
}
