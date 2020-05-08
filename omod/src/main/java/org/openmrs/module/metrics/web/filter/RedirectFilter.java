package org.openmrs.module.metrics.web.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectFilter implements Filter {
	
	private static final Logger log = LoggerFactory.getLogger(RedirectFilter.class);
	
	private String sourceUrl = null;
	
	private String targetUrl = null;
	
	@Override
	public void init(FilterConfig filterConfig) {
		sourceUrl = filterConfig.getInitParameter("source-url");
		targetUrl = filterConfig.getInitParameter("target-url");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	        ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse
		        && StringUtils.isNotBlank(sourceUrl) && StringUtils.isNotBlank(targetUrl)) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			request.getRequestDispatcher(httpRequest.getRequestURI().replace(sourceUrl, targetUrl)).forward(request,
			    response);
		} else {
			log.debug("Skipping request {}", request);
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {
		
	}
}
