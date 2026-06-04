package com.jwt.demo;

import java.util.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtDecoder {

	private final JwtUtil jwtUtil;

	public JwtDecoder(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	// ============================================
	// DECODE — Without Secret only see raw part
	// (jwt.io example — No verification!)
	// ============================================
	public void decodeWithoutVerification(String token) {
		System.out.println("\n=== DECODE (No Verification) ===");

		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			System.out.println("Invalid JWT format!");
			return;
		}

		// Header decode
		String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
		System.out.println("Header  : " + headerJson);

		// Payload decode
		String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
		System.out.println("Payload : " + payloadJson);

		// can do Signature — decode, but can't verify
		System.out.println("Signature (raw): " + parts[2]);
		System.out.println("NOTE: Can't verify Signature!");
	}

	// ============================================
	// DECODE WITH VERIFY — Secret with decode
	// ============================================
	public Claims decodeWithVerification(String token) {
		System.out.println("\n=== DECODE + VERIFY ===");

		Claims claims = Jwts.parser().verifyWith(jwtUtil.getSecretKey()).build().parseSignedClaims(token).getPayload();

		System.out.println("Subject  : " + claims.getSubject());
		System.out.println("Issuer   : " + claims.getIssuer());
		System.out.println("IssuedAt : " + claims.getIssuedAt());
		System.out.println("Expires  : " + claims.getExpiration());
		System.out.println("Role     : " + claims.get("role", String.class));
		System.out.println("UserId   : " + claims.get("userId", String.class));

		return claims;
	}
}