package com.sg.db.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.mongodb.util.JSON;

public class GetObjectId extends BasicDBService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3061724461862074269L;

	private final static String type_param = "type";

	private final static String json_type = "json";

	private final static String oct_string = "detail";

	@Override
	protected String getJSONResponse(HttpServletRequest req,HttpServletResponse resp) {
		Map<String, String> parameters = getInputParameters(req);
		String returnType = parameters.get(type_param);

		ObjectId oid = new ObjectId();

		if (json_type.equalsIgnoreCase(returnType)) {
			return JSON.serialize(oid);
		} else {
			String result = oid.toString();

			if (oct_string.equalsIgnoreCase(returnType)) {
				String time = result.substring(0, 8);
				String mac = result.substring(8, 14);
				String pid = result.substring(14, 18);
				String inc = result.substring(18, 24);

				return "{\"time\" : " + Long.parseLong(time, 16) + ",\"mac\" : " + Long.parseLong(mac, 16) + ",\"pid\" : " + Long.parseLong(pid, 16) + ",\"inc\" : "
						+ Long.parseLong(inc, 16)+"}";
			}
			return result;
		}

	}

}
