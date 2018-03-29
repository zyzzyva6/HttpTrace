package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/trace")
public class TraceController {

	@RequestMapping(value = "/callback")
	public void callback(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.getWriter().println(request.getMethod() + " " + request.getScheme() + " "
				+ request.getRequestURI());
		response.getWriter().println("");

		response.getWriter().println("headers:");
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String key = headers.nextElement();
			response.getWriter().println(key + " = " + request.getHeader(key));
		}
		response.getWriter().println("");

		response.getWriter().println("query string: " + request.getQueryString());

		response.getWriter().println("");

		response.getWriter().println("params:");
		request.getParameterMap().forEach(new BiConsumer<String, String[]>() {

			@Override
			public void accept(String t, String[] u) {
				try {
					response.getWriter().println(t + " = " + StringUtils.join(u));
				}
				catch (IOException e) {
				}
			}
		});
		response.getWriter().println("");

		response.getWriter().println("body:");
		StringBuffer sb = new StringBuffer();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(request.getInputStream(), "utf-8"));) {
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			response.getWriter().println(sb);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}
