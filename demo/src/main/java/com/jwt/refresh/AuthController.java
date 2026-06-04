package com.jwt.refresh;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

/**
 * REST endpoints- simulate controller.
 *
 * in Spring Boot
 *   POST /auth/login     → login()
 *   POST /auth/refresh   → refresh()
 *   POST /auth/logout    → logout()
 *   GET  /api/data       → protectedEndpoint()
 */
public class AuthController {

	private final TokenService tokenService;

	public AuthController(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	// ============================================
	// POST /auth/login
	// ============================================
	public TokenPair login(String userId, String password) {
		// Demo: real ah verify password in DB
		if ("wrong".equals(password)) {
			throw new RuntimeException("401: Invalid credentials");
		}
		return tokenService.login(userId, "USER");
	}

	// ============================================
	// POST /auth/refresh
	// Body: { "refreshToken": "..." }
	// ============================================
	public TokenPair refresh(String refreshToken) {
		try {
			return tokenService.refresh(refreshToken);
		} catch (RuntimeException e) {
			throw new RuntimeException("401: " + e.getMessage());
		}
	}

	// ============================================
	// POST /auth/logout
	// ============================================
	public String logout(String refreshToken) {
		tokenService.logout(refreshToken);
		return "200: Logged out successfully";
	}

	// ============================================
	// GET /api/data (protected endpoint)
	// Header: Authorization: Bearer <accessToken>
	// ============================================
	public String protectedEndpoint(String accessToken) {
		try {
			Claims claims = tokenService.validateAccessToken(accessToken);
			String userId = claims.getSubject();
			String role = claims.get("role", String.class);

			return "200: Hello " + userId + " [" + role + "]! " + "Token expires: " + claims.getExpiration();

		} catch (ExpiredJwtException e) {
			// Client should use refresh token now!
			return "401: Access token expired. Please refresh.";
		} catch (Exception e) {
			return "401: Invalid token — " + e.getMessage();
		}
	}
}