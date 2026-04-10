package com.hnix.sd.spring.xss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.springframework.util.StreamUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Slf4j
public class XSSRequestWrapper extends HttpServletRequestWrapper {
    private ByteArrayInputStream bis;

    private static String[] scriptPattern = new String[]{"onstop", "layer", "eval", "onactivate", "onfocusin", "document", "onclick", "onkeydown", "xml", "onbeforecut", "onkeyup", "link", "binding", "ondeactivate", "onload", "script", "msgbox", "ondragend", "onbounce", "object", "ondragleave", "frame", "applet", "ondragstart", "onmouseout", "ilayer", "onerror", "onmouseup", "bgsound", "href", "embed", "onabort", "base", "onstart", "onfocus", "onmovestart", "onmove", "onrowexit", "onunload", "onsubmit", "innerHTML", "onpaste", "ondblclick", "charset", "onresize", "ondrag", "expression", "string", "onselect", "ondragenter", "onchange", "append", "onscroll", "ondragover", "meta", "alert",  "ondrop", "void", "refresh", "iframe", "oncopy", "oncut", "ilayer", "blink", "onfinish", "frameset", "cookie", "style", "onreset", "onselectstart", "confirm", "onmouseover"};

    private static Pattern[] patterns = new Pattern[]{
            // Script fragments
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            // src='...'
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // lonely script tags
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // eval(...)
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // expression(...)
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // javascript:...
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // vbscript:...
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // onload(...)=...
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };
	public XSSRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);

		/* Front 로부터 전달받은 File 를 가져오기위해 반드시 필요 */
		request.getParameter("files");

        String requestBodyString = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("requestBodyString : {}", requestBodyString);
        String xss = stripXSS(requestBodyString);
        bis = new ByteArrayInputStream(xss.getBytes());
	}
	
	
	   @Override
	    public ServletInputStream getInputStream() throws IOException {
	        return new ServletInputStream() {
	            @Override
	            public boolean isFinished() {
	                return bis.available() == 0;
	            }

	            @Override
	            public boolean isReady() {
	                return true;
	            }

	            @Override
	            public void setReadListener(ReadListener listener) {
	                return;
	            }

	            @Override
	            public int read() throws IOException {
	                return bis.read();
	            }
	        };
	    }

	    @Override
	    public String[] getParameterValues(String name) {
	        String[] values = super.getParameterValues(name);

	        if (values == null) {
	            return null;
	        }

	        int count = values.length;
	        String[] encodedValues = new String[count];
	        for (int i = 0; i < count; i++) {
	            encodedValues[i] = stripXSS(values[i]);
	        }

	        return encodedValues;
	    }

	    @Override
	    public String getParameter(String name) {
	        String value = super.getParameter(name);
	        return stripXSS(value);
	    }

	    @Override
	    public String getHeader(String name) {
	        String value = super.getHeader(name);
	        return stripXSS(value);
	    }

	    private String stripXSS(String value) {
	        if (value != null) {

				//2024.07.15
				//사용자 입력 문자열이 대부분 textarea 안에서 출력되므로 그대로 출력하도록
				//아래 코드 주석처리함
	            //value = value.replaceAll("<", "&lt;");
	            //value = value.replaceAll(">", "&gt;");

	            for (String text : scriptPattern) {
	                if (value.indexOf(text) >= 0) {

						//2024.07.11
						//이메일에 link 문구가 들어가는 경우 _link 로 변경되어서 넘어오는 장애 발생
						//아래 코드 주석처리함
						//value = value.replaceAll(text, "_" + text);
	                }
	            }


	            // Remove all sections that match a pattern
//	            for (Pattern scriptPattern : patterns){
//	                value = scriptPattern.matcher(value).replaceAll("");
//	            }
	        }
	        return value;
	    }
}
