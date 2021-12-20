package net.dkt.dktsearch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

//タイムアウト時(csrfトークンが存在しなかった場合)の画面遷移を指定
public class MyAccessDeniedHandler implements AccessDeniedHandler{
	
	public void handle(HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException
			) throws IOException,
			ServletException {
		
		if (accessDeniedException instanceof MissingCsrfTokenException) {
			
			DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, "/login");
		}
	}
}
