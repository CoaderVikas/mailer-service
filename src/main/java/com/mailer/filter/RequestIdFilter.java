package com.mailer.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Class      : RequestIdFilter
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 13, 2026
 * Version    : 1.0
 */

@Component
public class RequestIdFilter extends OncePerRequestFilter {

	public static final String REQUEST_ID_HEADER = "X-Request-Id";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestId = request.getHeader(REQUEST_ID_HEADER);
		if (requestId == null || requestId.isEmpty()) {
			requestId = UUID.randomUUID().toString();
		}

		MDC.put("requestId", requestId);
		MDC.put("clientIp", getClientIp(request));

		try {
			response.setHeader(REQUEST_ID_HEADER, requestId);
			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty()) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}