package com.sg.db.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sg.db.Util;

public abstract class BasicDBService extends HttpServlet implements IDBServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9089437452516002267L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		run(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		run(req, resp);
	}

	protected void run(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		String result = getJSONResponse(req,resp);
		
		
		if(result.startsWith("#")){//·µ»Ø·þÎñÆ÷×´Ì¬Âë  "#5004,ADD_FAVOR_FAILURE"
			String[] code = result.substring(1).split(",");
			int statusCode = Integer.parseInt(code[0]);
			if(statusCode>=200&&statusCode<300){
				resp.setStatus(HttpServletResponse.SC_OK);
			}else{
				resp.sendError(statusCode, code[1]);
			}
		}else{
			resp.getWriter().write(result);
		}
		
	}

	protected abstract String getJSONResponse(HttpServletRequest req,HttpServletResponse resp);

	protected Map<String, String> getInputParameters(HttpServletRequest req) {
		Map<String, String[]> reqParaMap = req.getParameterMap();
		Enumeration<String> paraNames = req.getParameterNames();

		Map<String, String> result = new HashMap<String, String>();

		while (paraNames.hasMoreElements()) {
			String paraName = paraNames.nextElement();
			if (Util.inArray(paraName, KEEPS)) {
				continue;
			}

			String[] value = reqParaMap.get(paraName);
			if (value != null && value.length > 0) {
				result.put(paraName, value[0]);
			}
		}

		return result;
	}

}
