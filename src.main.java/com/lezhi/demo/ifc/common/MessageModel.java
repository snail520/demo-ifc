package com.lezhi.demo.ifc.common;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * 	此为消息组件，此组件可设置基本的返回值success、消息msg，如果不设置，则默认该请求返回为成功的请求返回
	此对象可使用Spring注入至Action、Service层，以便进行日志记录
	日志记录在extLogs（扩展日志），在程序执行过程中，对需要进行记录的日志，Add至此集合，由组件自动管理并写入
	如果程序中捕获了未知异常，推荐记录至exceptionLog，并设置错误编码
 */
//@Component
public class MessageModel {
	
	public MessageModel(){
		result.put("success", 1);
		result.put("msg", "成功");
	}
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	//用于返回值的存储
	private TreeMap<String,Object> result = new TreeMap<String, Object>();
	
	private ModelAndView modelAndView = new ModelAndView();
	
	public ModelAndView getModelAndView() {
		return modelAndView;
	}

	public void setModelAndView(ModelAndView modelAndView) {
		this.modelAndView = modelAndView;
	}
    private String resultJson;
    
	public String getResultJson() {
		return resultJson;
	}

	public void setResultJson(String resultJson) {
		this.resultJson = resultJson;
	}

	/*
	 * 扩展日志，主要用来记录自定义的日志信息
	 */
	private List<String> extLogs;
	
	/*
	 * 异常消息
	 */
	private String exceptionLog;
	
	/*
	 * 请求的Url地址
	 */
	private String requestUrl;
	
	/*
	 * 开始时间，主要是为了记录接口调用花费的时间
	 */
	private Date startTime;
	
	/*
	 * 结束时间，主要是为了记录接口调用花费的时间
	 */
	private Date endTime;
	
	protected TreeMap<String, Object> getResult() {
		return result;
	}

//	public void setResult(TreeMap<String, Object> result) {
//		this.result = result;
//	}
	
	/*
	 * 消息的返回状态，1为成功，2为失败
	 */
	public int getSuccess() {
		if(result.containsKey("success")){
			return  Integer.parseInt(result.get("success").toString());
		}
		return 0;
	}
	
	public void setSuccess(int code) {
		result.put("success", code);
	}
	
	/*
	 * 消息的返回说明，如：调用成功
	 */
	public String getMsg() {
		if(result.containsKey("msg")){
			return (String) result.get("msg");
		}
		return null;
	}

	public void setMsg(String msg) {
		result.put("msg", msg);
	}

	public List<String> getExtLogs() {
		if(extLogs==null){
			extLogs = new ArrayList<String>();
		}
		return extLogs;
	}

	public String getExceptionLog() {
		return exceptionLog;
	}

	
	public void setExceptionLog(String exceptionLog) {
		this.exceptionLog = exceptionLog;
	}
	
	public void setExceptionLog(String exceptionLog,int errorCode) {
		this.exceptionLog = exceptionLog;
	}
	
	public void setExceptionLog(Exception ex,int errorCode){
		setSuccess(errorCode);
		StringWriter sw = new StringWriter();  
        PrintWriter pw = new PrintWriter(sw);  
        ex.printStackTrace(pw);  
		setExceptionLog(sw.toString());		
	}

	protected String getRequestUrl() {
		return requestUrl;
	}


	protected void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	protected Date getStartTime() {
		return startTime;
	}


	protected void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	protected Date getEndTime() {
		return endTime;
	}


	protected void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/*
	 * 此方法只限于LogAspect类调用，主要用于在向SpringMvc返回最终结果时使用。
	 * 由于MessageModel由Spring代理，生命周期只限于request，所以在最终返回时，jackson无法序列化数据了，所以需要进行一次浅拷贝后返回
	 */
	protected Map<String,Object> getRetMap(){
		Map<String,Object> retmap = new TreeMap<String, Object>();
		retmap.put("result", getResult().descendingMap());
		return retmap;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n");
		sb.append("调用地址："+requestUrl+"\r\n");
		if(endTime!=null && startTime!=null){
			sb.append("耗费时间：");
			sb.append(endTime.getTime()-startTime.getTime());
			sb.append("\r\n");
		}
		sb.append("处理结果：");
		sb.append(getSuccess());
		sb.append("\r\n");
		if(StringUtils.isNotBlank(getMsg())){
			sb.append("处理消息：");
			sb.append(getMsg());
			sb.append("\r\n");
		}
		if(extLogs!=null && extLogs.size()>0){
			sb.append("扩展消息：");
			sb.append("\r\n");
			for(int i = 0;i<extLogs.size();i++){
				sb.append("        消息");
				sb.append(i+1);
				sb.append(":");
				sb.append(extLogs.get(i));
				sb.append("\r\n");
			}
		}
		if(StringUtils.isNotBlank(exceptionLog)){
			sb.append("异常消息：");
			sb.append(exceptionLog);
			sb.append("\r\n");
		}
		
		if(result!=null && result.size()>0){
			try {
				sb.append("返回json：");
				sb.append(jsonMapper.writeValueAsString(getResultJson()));
				sb.append("\r\n");
			} catch (Exception e) {
				sb.append("--解析异常--异常消息：");
				sb.append(e.getMessage());
				sb.append("\r\n");
			}
		}
		return sb.toString();
	}
}
