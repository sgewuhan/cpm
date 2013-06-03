package com.sg.cpm.admin;

import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;

public class AdminFunction {
	public static final int work_template = 41;
	public static final int workflow_reposite = 42;
	public static final int workflow_console = 43;
	public static final int org_obs = 32;
	public static final int org_user = 31;
	public static final int auth_query = 21;
	public static final int auth_setting = 20;
	public static final int project_workflow = 12;
	public static final int project_workdelivery = 11;
	public static final int project_template = 10;
	public static final int server_setting = 51;
	
	public static final String ID = "id";

	public static int getFunctionId(ISingleObject element) {
		return ((Number) ((CascadeObject) element).getValue(AdminFunction.ID)).intValue();
	}

}
