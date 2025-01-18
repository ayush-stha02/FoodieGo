package com.example.sbtcsit6th;

public class ValidationError {

	private String msg = null;

	public ValidationError(String msg) {
		this.msg = msg;
	}

	public ValidationError() {
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean getHasError() {
		return msg != null;
	}

}
