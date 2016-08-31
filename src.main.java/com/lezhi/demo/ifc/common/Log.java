package com.lezhi.demo.ifc.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.lezhi.demo.model.util.ReadProperties;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class Log {
	public final static Object lock = new Object();
	
	private static Logger getLogger(Class clazz,String fun){
		String logPath = getUploadPath();
		Logger logger = null;
			logger = (Logger) LoggerFactory.getLogger(clazz.getSimpleName());
			
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();  

			//Remove all previously added appenders from this logger instance.  
			logger.detachAndStopAllAppenders();  

			//define appender  
			RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();  
			
			//policy  
			TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<ILoggingEvent>();  
			policy.setContext(loggerContext);  
			policy.setMaxHistory(5);  
			policy.setFileNamePattern(logPath+fun+"\\%d{yyyy-MM-dd}.log");  
			policy.setParent(appender);  
			policy.start();  

			//encoder  
			PatternLayoutEncoder encoder = new PatternLayoutEncoder();  
			encoder.setContext(loggerContext);  
			encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n");  
			encoder.start();  

			//start appender  
			appender.setRollingPolicy(policy);  
			appender.setContext(loggerContext);  
			appender.setEncoder(encoder);  
			appender.setPrudent(true); //support that multiple JVMs can safely write to the same file.  
			appender.start();  

			logger.addAppender(appender);  

			//setup level  
			logger.setLevel(Level.INFO);  
			
			//remove the appenders that inherited 'ROOT'.  
			logger.setAdditive(false);  
		return logger;
	}

	public static void Info(Class clazz,String fun,String str){
		synchronized (lock) {
		Logger logger = getLogger(clazz,fun);
		logger.info(str);
		}
	}

	public static void Debug(Class clazz,String fun,String str){
		synchronized (lock) {
		Logger logger = getLogger(clazz,fun);
		logger.info(str);
		}
	}

	public static void Error(Class clazz,String fun,String str){
		synchronized (lock) {
		Logger logger = getLogger(clazz,fun);
		logger.error(str);
		}
	}
	
	public static String getUploadPath(){
		String uploadPath = "";
		ReadProperties rp = new ReadProperties("/gateway.properties");
		String  lastPlace="";
		if(System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS")!= -1){
			try {
				String ip=InetAddress.getLocalHost().getHostAddress();
				if(StringUtils.isNotBlank(ip)){
					lastPlace=ip.substring(ip.lastIndexOf(".")+1,ip.length());
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			uploadPath = ObjectUtils.toString(rp.getProperty("windows.logPath"))+lastPlace+"/";
			
		}else{
			uploadPath = ObjectUtils.toString(rp.getProperty("linux.logPath"))+lastPlace+"/";
		}
		return uploadPath;
	}
}
