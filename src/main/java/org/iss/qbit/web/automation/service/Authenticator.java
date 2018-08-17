
package org.iss.qbit.web.automation.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authenticator")
public class Authenticator
{

	public boolean result(String bot)
	{
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//		boolean hasRole = false;
//		for (GrantedAuthority authority : authorities)
//		{
//			hasRole = authority.getAuthority().equals(role);
//			if (hasRole)
//			{
//				break;
//			}
//		}
//		return hasRole;

		System.err.println("in check for ---------------> " + bot+" ["+new SimpleGrantedAuthority(bot+"-R")+"] ----------> "+authorities);
		for (GrantedAuthority authority : authorities) {
			System.err.println(authority.getAuthority());
	        if (("ROLE_"+bot+"-R").equals(authority.getAuthority())) {
	          return true;
	        }
	      }
		return true;
	}

	public boolean configuration(String bot)
	{
		System.err.println("in check for -----------------------------------> " + bot);

		return false;
	}

	public boolean execution(String bot)
	{
		System.err.println("in check for -----------------------------------> " + bot);

		return false;
	}

	public boolean owner(String bot)
	{
		System.err.println("in check for -----------------------------------> " + bot);

		return false;
	}

	public boolean check(String bot)
	{
		System.err.println("in check for -----------------------------------> " + bot);

		return false;
	}
}