package com.sg.user;

import com.mongodb.BasicDBList;

public interface IAuthorityResponse {

	public static String MESSAGE_USER_NOT_BELONG_ANY_ORG = "很抱歉，您的账户还没有加入到任何一个组织，但是您当前的操作需要得到有关组织的授权。\n如果您需要继续操作，请与您所在站点的组织管理员联系，将您的账号加入到组织。";

	public static String MESSAGE_USER_AND_HIS_ROLE_HAVENOT_PERMISSION = "很抱歉，您当前的操作需要得到有关组织的授权。\n如果您需要继续操作，请与您所在站点的组织管理员联系。";

	public void setPermission(boolean b);

	public void setPermissionContextList(BasicDBList result);

	public void setPermission(boolean b, String message);
	
	public boolean isPermission() ;

	public BasicDBList getContextList() ;

	public String getMessage() ;

}
