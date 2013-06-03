package com.sg.widget.test;

import org.eclipse.core.expressions.PropertyTester;

import com.sg.user.ISessionAuthorityControl;

public class AuthorityTester extends PropertyTester {

	public AuthorityTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ISessionAuthorityControl) {
//			return UserSessionContext.hasAuthority(args,(ISessionAuthorityControl)receiver);
		}
		return false;
	}

}
