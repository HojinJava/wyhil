package com.hnix.sd.rad;


import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import com.hnix.sd.core.dto.ComResponseDto;
import com.hnix.sd.core.utils.ComResponseUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("rad")
public class RadController {

	private final ComResponseUtil comResponseUtil;
	private final SqlSessionTemplate session;



	@PostMapping("/{m_namespace}/{m_sqlid}")
	public ComResponseDto<?> getRadSelectList (
		@RequestBody Object params,
		@PathVariable("m_namespace") String sNameSpace,
		@PathVariable("m_sqlid") String sSqlId
	){
		System.out.println("# getRadSelectList() start");

		System.out.println("# params : " + params.toString());

		String statement = sNameSpace + "." + sSqlId;
		List<Map<String, Object>> result = session.selectList(statement, params);

		System.out.println("# getRadSelectList() end");
		return comResponseUtil.setResponse200ok(result);

	}


	/*
	//@RequestParam Map<String, Object> params
	//-> ?params=1234
	//파라미터를 url 에서 받아야해서 json 방식으로 못받음. post 방식으로 해야됨
	@GetMapping("/{m_namespace}/{m_sqlid}")
	public ComResponseDto<?> getRadSelectList (
		@RequestParam Map<String, Object> params,
		@PathVariable("m_namespace") String sNameSpace,
		@PathVariable("m_sqlid") String sSqlId
	) {

		System.out.println("# getRadSelectList() start");

		System.out.println("# params : " + params.toString());

		String statement = sNameSpace + "." + sSqlId;
		List<Map<String, Object>> result = session.selectList(statement, params);

		System.out.println("# getRadSelectList() end");
		return comResponseUtil.setResponse200ok(result);
	}
	*/



}
