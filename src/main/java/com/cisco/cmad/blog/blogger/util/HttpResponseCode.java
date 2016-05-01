package com.cisco.cmad.blog.blogger.util;

public enum HttpResponseCode {
	OK(200), 
	CREATED(201),
	NO_CONTENT(204),
	REDIRECT(302),
	TEMPORARY_REDIRECT(307),
	BAD_REQUEST(400), 
	UNAUTHORIZED(401), 
	NOT_FOUND (404), 
	NOT_ALLOWED(405), 
	INTERNAL_ERROR(500);
	
	private int statusCode;
	
	HttpResponseCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public int get() {
		return statusCode;
	}
}
