package com.lhh.core.core.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lhh.core.base.LhhCoreBaseCtrl;
import com.lhh.core.util.LhhCoreUtilsCtrl;

@Controller
@RequestMapping("/coretest")
public class CoreTestCtrl extends LhhCoreBaseCtrl {

    @RequestMapping("list")
    public void list(HttpServletRequest req, HttpServletResponse res) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success", "成功");
        LhhCoreUtilsCtrl.putMapToJson(map, res);
    }
}