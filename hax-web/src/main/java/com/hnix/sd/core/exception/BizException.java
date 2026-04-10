package com.hnix.sd.core.exception;

import java.util.List;

public class BizException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String msgKey;
	private List<String> arrayReplace;
	private Throwable throwable = null;


	public BizException(String msgKey) {
		super(msgKey);
		this.msgKey = msgKey;
	}

	public BizException(String msgKey, String sReplace) {
		super(msgKey);
		this.msgKey = msgKey;
		this.arrayReplace = List.of(sReplace);
	}
	public BizException(String msgKey, List<String> arrayReplace) {
		super(msgKey);
		this.msgKey = msgKey;
		this.arrayReplace = arrayReplace;
	}	
	
	public BizException(Throwable throwable, String msgKey) {
		super(msgKey);
		this.throwable = throwable;
		this.msgKey = msgKey;
	}
	public BizException(Throwable throwable, String msgKey, String sReplace) {
		super(msgKey);
		this.throwable = throwable;
		this.msgKey = msgKey;
		this.arrayReplace = List.of(sReplace);
	}



	public BizException(Throwable throwable, String msgKey, List<String> arrayReplace) {
		super(msgKey);
		this.throwable = throwable;
		this.msgKey = msgKey;
		this.arrayReplace = arrayReplace;
	}


	public String getMsgKey() {
		return msgKey;
	}

	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	public List<String> getArrayReplace() {
		return arrayReplace;
	}

	public void setArrayReplace(List<String> arrayReplace) {
		this.arrayReplace = arrayReplace;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
