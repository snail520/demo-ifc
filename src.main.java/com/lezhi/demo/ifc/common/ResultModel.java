package com.lezhi.demo.ifc.common;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultModel {

	//private String request
	/*
	 * 处理状态
	 */
	private String status;

	/*
	 * 消息说明
	 */
	private String msg;

	/*
	 * 扩展说明，主要用来记录自定义的日志信息
	 */
	private String expandDesc;

	/*
	 * 异常消息
	 */
	private String exceptionDesc;

	/*
	 * 请求的Rest地址格式
	 */
	private String requestPatter;

	/*
	 * 请求的Url地址
	 */
	private String requestUrl;

	/*
	 * 返回的数据集
	 */
	private Object extraInfo;

	/*
	 * 开始时间，主要是为了记录接口调用花费的时间
	 */
	private Date startTime;

	/*
	 * 结束时间，主要是为了记录接口调用花费的时间
	 */
	private Date endTime;

	/*
	 * 请求参数，主要是为了记录接口调用时传入参数值
	 */
	private String inPara;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonInclude(Include.NON_NULL)
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setExceptionDesc(String exceptionDesc) {
		this.exceptionDesc = exceptionDesc;
	}
	@JsonIgnore
	public String getExceptionDesc() {
		return exceptionDesc;
	}

	public void setExpandDesc(String expandDesc) {
		this.expandDesc = expandDesc;
	}
	@JsonIgnore
	public String getExpandDesc() {
		return expandDesc;
	}

	@JsonInclude(Include.NON_NULL)
	public Object getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(Object extraInfo) {
		this.extraInfo = extraInfo;
	}
	@JsonIgnore
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonIgnore
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setRequestPatter(String requestPatter) {
		this.requestPatter = requestPatter;
	}

	@JsonIgnore
	public String getRequestPatter() {
		return requestPatter;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@JsonIgnore
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setInPara(String inPara) {
		this.inPara = inPara;
	}

	@JsonIgnore
	public String getInPara() {
		return inPara;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("\r\n");
		sb.append("测试调用地址："+requestUrl+"\r\n");
		if(endTime!=null && startTime!=null){
			sb.append("耗费时间："+(endTime.getTime()-startTime.getTime())+"\r\n");
		}
		sb.append("处理结果："+status+"\r\n");
		if(StringUtils.isNotBlank(msg)){
			sb.append("处理消息："+msg+"\r\n");
		}
		if(null!=extraInfo){
			sb.append("返回消息："+extraInfo+"\r\n");
		}
		if(StringUtils.isNotBlank(expandDesc)){
			sb.append("扩展消息："+expandDesc+"\r\n");
		}
		if(status == "1" && StringUtils.isNotBlank(exceptionDesc)){
			sb.append("异常追踪："+exceptionDesc+"\r\n");
		}
		if(StringUtils.isNotBlank(inPara)){
			sb.append("请求参数："+inPara+"\r\n");
		}

		if(!"0".equals(status)){
			try {
				sb.append("返回json："+new ObjectMapper().writeValueAsString(this)+"\r\n");
			} catch (Exception e) {
				sb.append("返回json：--解析异常--\r\n");
			}
		}
		return sb.toString();
	}
}
