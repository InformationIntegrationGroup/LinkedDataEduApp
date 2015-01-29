package edu.isi.serverbackend.filter;

import javax.servlet.*;

import java.io.IOException;

public class CharacterEncodingFilter implements Filter{

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,	FilterChain filterChain) throws IOException, ServletException {
		
		if (request.getCharacterEncoding() == null) {
		    request.setCharacterEncoding("UTF-8");
		}
		response.setCharacterEncoding("UTF-8");
		filterChain.doFilter(request, response);
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
