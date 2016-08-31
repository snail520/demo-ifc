package com.lezhi.demo.ifc.common;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Aspect
public class LogAspect {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ObjectMapper jsonMapper;
	
	@SuppressWarnings("unchecked")
	@Around("within(com.lezhi.demo.ifc.*.*Action)")
	public Object doExecute(ProceedingJoinPoint  joinPoint) throws Throwable {
	 ResultModel rm = null;
	 try{
		Object[] args = joinPoint.getArgs();
		for(int i = 0;i<args.length;i++){
			if(args[i].getClass().equals(ResultModel.class)){
				rm = (ResultModel)args[i];
				break;
			}
		}
		if(rm==null){
			rm = new ResultModel();
			request.setAttribute("resultModel", rm);
		}
		
		rm.setStartTime(new Date());
		String queryString = jsonMapper.writeValueAsString(request.getParameterMap());
		rm.setRequestUrl(request.getRequestURL().toString()+(StringUtils.isNotBlank(queryString) ? "|" + request.getMethod() + "|" + queryString : ""));
		
		Object retval = null;
		try{
			retval = joinPoint.proceed();
		}catch(Exception e){
			exceptioncl(rm,e,joinPoint);
			return rm;
		}
		rm.setEndTime(new Date());

		Signature signature = joinPoint.getSignature();  
		MethodSignature methodSignature = (MethodSignature) signature;  
		Method method = methodSignature.getMethod();
		
		String returnObjectType = retval.getClass().getName();
		String realType = returnObjectType.substring(returnObjectType.lastIndexOf(".")+1, returnObjectType.length());

		//订购关系返回信息
		if((ObjectUtils.toString(method.getDeclaringClass().getPackage()).indexOf("subscribe") != -1)){
			Log.Info(method.getDeclaringClass(),method.getDeclaringClass().getSimpleName()+"\\"+method.getName(), retval.toString());
			return retval;
		}else{
			if(realType.equalsIgnoreCase("HashMap") || realType.equalsIgnoreCase("LinkedMap")){//方法中  获取返回信息
				String methodReturnStatus = "",methodReturnMsg = "",methodReturnResult = "";
				//返回状态
				Map<String,Object> map = (Map<String,Object>)retval;
				methodReturnStatus = ObjectUtils.toString(map.get("status"));
				methodReturnMsg = ObjectUtils.toString(map.get("msg"));
				
				//用户认证返回信息
				if(methodReturnStatus.equals("0") && method.getDeclaringClass().getSimpleName().indexOf("Certification")!=-1){
					map.remove("status");
					map.remove("msg");
					rm.setExtraInfo(map);
				}else if(ObjectUtils.toString(method.getDeclaringClass().getPackage()).indexOf("info") != -1  || ObjectUtils.toString(method.getDeclaringClass().getPackage()).indexOf("statistics") != -1){
					if(map.get("flag") == null){
						StringBuilder sb = new StringBuilder();
						sb.append("\r\n");
						sb.append("调用地址："+rm.getRequestUrl()+"\r\n");
						if(rm.getEndTime()!=null && rm.getStartTime()!=null){
							sb.append("耗费时间：");
							sb.append(rm.getEndTime().getTime()-rm.getStartTime().getTime());
							sb.append("\r\n");
							sb.append("处理结果：");
							sb.append(ObjectUtils.toString(map.get("status")));
							sb.append("\r\n");
							sb.append("处理消息：");
							sb.append(map.toString());
							sb.append("\r\n");
						}
						Log.Info(method.getDeclaringClass(),method.getDeclaringClass().getSimpleName()+"\\"+method.getName(),sb.toString());
						map.put("flag","1");
						retval = map;
						return retval;
					}else{
						map.remove("flag");
						retval = map;
						return retval;
					}
				}
				//其他接口返回信息
				if(StringUtils.isNotBlank(ObjectUtils.toString(map.get("extraInfo")))){
					methodReturnResult = ObjectUtils.toString(map.get("extraInfo"));
					rm.setExtraInfo(methodReturnResult);
				}
				
				rm.setStatus(methodReturnStatus);
				rm.setMsg(methodReturnMsg);
			}

			if(realType.equalsIgnoreCase("ResultModel")){//方法后
				rm = (ResultModel)retval;
				if(rm.getStatus() == "1"){//异常信息
					Log.Info(method.getDeclaringClass(),method.getDeclaringClass().getSimpleName()+"\\"+method.getName(), rm.toString());
					return rm;
				}else{//正常信息
					Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
					logger.info(rm.toString());
//					Log.Info(method.getDeclaringClass(),method.getDeclaringClass().getSimpleName()+"\\"+method.getName(), rm.toString());
				}
			}
			return rm;
		}
	 }catch(Exception e){
		 exceptioncl(rm,e,joinPoint);
		 return rm;
	 }
	}

	private void exceptioncl(ResultModel rm,Exception e ,JoinPoint joinPoint){
		rm.setStatus("1");
		rm.setMsg("系统错误");
		rm.setEndTime(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage()+"\r\n");
		for(StackTraceElement ste : e.getStackTrace()){
			sb.append(ste+"\r\n");
		}
		rm.setExceptionDesc(sb.toString());
		Signature signature = joinPoint.getSignature();  
		MethodSignature methodSignature = (MethodSignature) signature;  
		Method method = methodSignature.getMethod(); 
		Log.Error(method.getDeclaringClass(),method.getDeclaringClass().getSimpleName()+"\\"+method.getName(), rm.toString());
	}
}
