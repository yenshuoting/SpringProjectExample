package com.example.springBootExample;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@Api(tags = "Hello Wolrd, api類別" )
@RestController
public class Hello {
	
	@GetMapping("/sayHelloWorld")
	public ResponseEntity<String> sayHelloWorld() {
		return new ResponseEntity<String>("Hello World !! ",HttpStatus.BAD_GATEWAY) ;
	}
	
	@ApiOperation(value = "根據name 配置對映物件", notes = "name必填。")
	@ApiImplicitParam(paramType = "query", name = "名字", value = "名字", required = true, dataType = "String")
	@ApiResponses({
		@ApiResponse(responseCode="200", description="成功取得物品"),
		@ApiResponse(responseCode="404", description="找不到物品")
	})
	@GetMapping("/sayHelloWorld/{name}")
	public String sayHelloWorld( @PathVariable("name") String name ) {
		return  new String("Hello "+name+" !!");
	}
}
