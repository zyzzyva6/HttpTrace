package com.example.demo.controller;

/**
 * Created by zhouyang09 on 2018/3/5.
 */

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created by zxl on 17-10-22.
 */
@RestController
@RequestMapping("/api")
public class HttpController {


    /**
     * 健康检查
     *
     * @return
     */
    @RequestMapping("/healthCheck")
    public Object healthCheck() {
        System.out.println("healthcheck");
        return "success";
    }

    /**
     * 服务接口
     *
     * @return
     */
    @RequestMapping("/hello")
    @ResponseBody
    public Object service() {
        return "hello";
    }

    @RequestMapping("/router")
    @ResponseBody
    public void get(HttpServletRequest request, HttpServletResponse response) {
        try {

            //返回的obj
            JSONObject obj = new JSONObject();

            //获取头信息
            Enumeration headerNames = request.getHeaderNames();
            JSONObject headerJson = new JSONObject();
            while (headerNames.hasMoreElements()) {
                String key = (String) headerNames.nextElement();
                headerJson.put(key, request.getHeader(key));
            }
            obj.put("headers", headerJson);

            //获取请求方法
            String method = request.getMethod();
            obj.put("method", request.getMethod());

            //get请求
            String query = request.getQueryString();
            obj.put("query", query);
            obj.put("body", null);
            //post请求
            if (method.equals("POST")) {
//                obj.put("content-type",)
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                obj.put("body", sb.toString());
            }

            //获取所有参数
            Enumeration paramNames = request.getParameterNames();
            JSONObject paramJson = new JSONObject();
            while (paramNames.hasMoreElements()) {
                String key = (String) paramNames.nextElement();
                paramJson.put(key, request.getParameterValues(key));
            }
            obj.put("params", paramJson);

            //参数化配置
            if (paramJson.containsKey("zyscode")) {
                String respCode = paramJson.getJSONArray("zyscode").getString(0);
                if (respCode != null && !"".equals(respCode))
                    response.setStatus(Integer.parseInt(respCode));
                paramJson.remove("zyscode");
            }
            if (paramJson.containsKey("zystimeout")) {
                String sleepTime = paramJson.getJSONArray("zystimeout").getString(0);
                if (sleepTime != null && !"".equals(sleepTime))
                    Thread.sleep(Long.parseLong(sleepTime));
                paramJson.remove("zystimeout");
            }
            //组装返回，返回格式默认为json
            response.setContentType("application/json;charset=utf-8");

            PrintWriter out = response.getWriter();

            if (!paramJson.containsKey("zysnull") || !("yes").equals(paramJson.getJSONArray("zysnull").getString(0)))
            {
                paramJson.remove("zysnull");
                out.write(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue));
            }

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


}