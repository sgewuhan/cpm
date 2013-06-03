package com.sg.db.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sg.db.DBActivator;
import com.sg.db.IServiceError;
import com.sg.db.Util;

public class IDGenerator extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2652413149957895439L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		try {
			String collectionName = req.getParameterMap().get("seq")[0];
			if (collectionName != null) {
				int id = Util.getIncreasedID(DBActivator.getDefaultDBCollection("ids"), collectionName);
				resp.getWriter().write(""+id);
			}
		} catch (Exception e) {
			resp.getWriter().write(IServiceError.ERR_UNKNOWN +e.getMessage());
		}
	}

}
