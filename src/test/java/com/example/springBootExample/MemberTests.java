package com.example.springBootExample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.springBootExample.entity.Member;
import com.example.springBootExample.repository.MemberRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberTests {
	
	@Autowired
	private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

	
//	@Autowired
//	private TestRestTemplate restTemplate;
	
	@BeforeEach
	void beforeInit() {
        Member member = new Member();
        member.setUsrName("test1");
        member.setUsrPwd("test1");
        member.seteMail("test@test1.com");;
        member = memberRepository.save(member);
	}
	
	   @Test
	     void testNotNull() {
	        Assertions.assertNotNull(memberRepository);
	    }

	
	@Test
	void test() throws Exception {
	        
	        
	        this.mockMvc.perform(MockMvcRequestBuilders.get("/sayHelloWorld/spring"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Hello spring !!"));
	        
//
//	        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/api/v1/book/{bookid}");
//	        Map<String, Object> uriParams = new HashMap<String, Object>();
//	        uriParams.put("bookid", member.getId());
//
//	        HttpHeaders headers = new HttpHeaders();
//	        headers.setContentType(MediaType.APPLICATION_JSON);
//	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//	        HttpEntity<Object> entity = new HttpEntity<>(headers);
//	        ResponseEntity<String> response = restTemplate.exchange(
//	                builder.buildAndExpand(uriParams).toUri().toString(),
//	                HttpMethod.GET, entity, String.class);
//	        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(), "testGetBook Fail:\n" + response.getBody());
	    }
	

}
