package com.siemens.ssa.communicator.web.utils;

import com.siemens.security.user.UserInfo;

import pt.atos.web.click.utils.link.ILinkBuilder;

public class SSAComLinkBuilder implements ILinkBuilder {

	@Override
	public String buildLink(String url, UserInfo user) {
		if (url == null)
			return url;

		if (url.startsWith("http") || url.startsWith("//"))
			return url;

		String context = getWebContextPath(user);

		if (!url.startsWith("/"))
			url = "/" + url;

		if (!url.contains(context)) {
			return context + url;

		} else {
			return url;
		}
	}

	@Override
	public String getWebContextPath(UserInfo user) {
		return "/SGCCommunicatorWeb";
	}

	@Override
	public String buildDownloadLink(String url, UserInfo user) {

		if (url == null)
			return url;

		if (url.startsWith("http") || url.startsWith("//"))
			return url;

		String context = getWebContextPath(user);

		if (!url.startsWith("/"))
			url = "/" + url;
		if (!url.contains(context))
			return context + url;
		else
			return url;

	}

	@Override
	public String buildPopupLink(String url, UserInfo user) {
		if (url == null)
			return url;

		if (url.startsWith("http") || url.startsWith("//"))
			return url;

		String context = getWebContextPath(user);

		if (!url.startsWith("/"))
			url = "/" + url;
		if (!url.contains(context))
			return context + url;
		else
			return url;
	}

}
