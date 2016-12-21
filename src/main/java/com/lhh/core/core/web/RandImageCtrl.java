package com.lhh.core.core.web;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lhh.LhhUtilsErrorMsg;
import com.lhh.core.base.LhhCoreBaseCtrl;
import com.lhh.core.util.LhhCoreUtilsCtrl;
import com.lhh.image.LhhUtilsValidateImage;


/**
 * @TODO 验证码
 * @author hwaggLee
 * @createDate 2016年12月19日
 */
@Controller
@RequestMapping("/randimage")
public class RandImageCtrl extends LhhCoreBaseCtrl {
	
	/**
	 * 生成验证码图片jpeg
	 * @return
	 */
    @RequestMapping("jpeg")
    public void jpeg(HttpServletRequest req, HttpServletResponse res) {
    	String imageKey = req.getParameter("imageKey");
    	if( StringUtils.isNotBlank(imageKey)){
    		// 设置页面不缓存
    		res.setHeader("Pragma", "No-cache");
    		res.setHeader("Cache-Control", "no-cache");
    		res.setDateHeader("Expires", 0);
    		// 生成图片对象
    		LhhUtilsValidateImage validateImageUtils = LhhUtilsValidateImage.getInstance();
    		
    		// 将认证码存入SESSION
    		req.getSession().setAttribute(imageKey, validateImageUtils.getRandomNum());
    		try {
    			ImageIO.write(validateImageUtils.getImage(), "JPEG", res.getOutputStream());
    		} catch (IOException ioe) {
    		}
    	}else{
    		LhhCoreUtilsCtrl.putErrorJSON(res, LhhUtilsErrorMsg.e_0001);
    	}
    }
}
