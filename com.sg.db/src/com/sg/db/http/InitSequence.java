package com.sg.db.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.sg.db.DBActivator;

public class InitSequence extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8860887086057716769L;

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
			String start = req.getParameterMap().get("startwith")[0];
			int iStart = Integer.parseInt(start);

			// 1. 初始化用户id为四位
			DBCollection idsCol = DBActivator.getDefaultDBCollection("ids");
			idsCol.findAndModify(new BasicDBObject().append("name", collectionName), null, null, false,new BasicDBObject().append("id", iStart).append("name", collectionName) , false, true);
			resp.getWriter().write("set " + collectionName + " id to " + start + ", next id will be " + (iStart + 1));

		} catch (Exception e) {
			resp.getWriter().write(e.getLocalizedMessage());
		}
	}

}
