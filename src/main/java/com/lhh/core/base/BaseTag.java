package com.lhh.core.base;

import java.io.IOException;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * server tag
 * @author hwaggLee
 * @createDate 2016年12月19日
 */
public abstract class BaseTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	/**日志对象*/
    protected final Logger logger = Logger.getLogger(getClass());
    
    private static WebApplicationContext webApplicationContext;
	
    protected static void pageContextWrite(PageContext pageContext, String str) {
        try {
            pageContext.getOut().write(str);
        } catch (IOException ex) {
            ReflectionUtils.handleReflectionException(ex);
        }
    }

    protected WebApplicationContext getWebApplicationContext() {
        if (webApplicationContext == null) {
            webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(super.pageContext.getServletContext());
        }
        return webApplicationContext;
    }
}
