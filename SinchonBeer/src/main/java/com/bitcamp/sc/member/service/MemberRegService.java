 package com.bitcamp.sc.member.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//import com.bitcamp.sc.member.config.WebSecurityConfig;
import com.bitcamp.sc.member.domain.Member;
import com.bitcamp.sc.member.domain.MemberAddress;
import com.bitcamp.sc.member.domain.RegRequest;
import com.bitcamp.sc.member.repository.MemberDao;

@Service
public class MemberRegService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SqlSessionTemplate template;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	private MemberDao memberDao;

	public int regMember(RegRequest regRequest) {

		int resultCnt = 0;
		//System.out.println("MemberRegService 클래스 진입");
		// 주소를 제외한 회원가입 - toMember(): midx, email, pw, name, phone
		memberDao = template.getMapper(MemberDao.class);
		//System.out.println("memberDao와 mapper 연결 성공");
		
			Member member = regRequest.getMemberRegRequest().toMember();

			//암호화
			String securityPw = passwordEncoder.encode(member.getPw());
			logger.info("암호화 테스트 : "+securityPw);
			member.setPw(securityPw);
			logger.info("암호화 결과 : "+passwordEncoder.matches(member.getPw(), securityPw));	
			
			
			
//			System.out.println("[regService에서] 회원가입 한 member 정보 :" + member);
//			System.out.println("[regService에서] email : " + member.getEmail());
		
			resultCnt = memberDao.insertMember(member); // 1또는 0 반환.
			//System.out.println("[regService에서] insert 결과 : " + resultCnt);
			//System.out.println("새롭게 등록된 idx : " + member.getIdx());

			// 사용자가 입력한 주소를 주소테이블에 넣기
			MemberAddress memberAddress = regRequest.getMemberAddressRequest().toMemberAddress();
			//System.out.println("[regService에서] 회원가입 한 주소 정보 :" + memberAddress);
			//System.out.println("주소도 가입했는지 여부 (true or false): "+memberAddress.formValidate());
			if (memberAddress.formValidate()) {
				// 주소를 모두 입력하였다면
				memberAddress.setMidx(member.getIdx());

				resultCnt = memberDao.insertAddress(memberAddress);
			}
			System.out.println("주소까지 회원가입에 입력 resultCnt :" + resultCnt);

		return resultCnt;
	}
}
