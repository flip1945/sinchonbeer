package com.bitcamp.sc.order.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.bitcamp.sc.member.domain.Member;
import com.bitcamp.sc.member.repository.MemberDao;
import com.bitcamp.sc.order.domain.OrderInfo;

@SpringBootTest
class MybatisOrderDaoTest {

	@Autowired
	MybatisOrderDao dao;
	@Autowired
	SqlSessionTemplate template;
	MemberDao memberDao;

	@Test
	@Transactional
	@Rollback(true)
	void 주문등록() {
		// given
		Member member = new Member(0, "test123123@naver.com", "q`12q`12", "TestName", "01012345678", "12345");
		
		memberDao = template.getMapper(MemberDao.class);
		memberDao.insertMember(member);
		
		int memberIdx = memberDao.selectByEmailPw("test123123@naver.com", "q`12q`12").getIdx();
		
		OrderInfo orderInfo = OrderInfo.builder()
									   .category("tour")
									   .price(3000)
									   .tourIdx(60)
									   .tourPeople(3)
									   .memberIdx(memberIdx)
									   .addressIdx(1)
									   .build();

		// when
		dao.save(orderInfo);

		// then
		int idx = orderInfo.getIdx();

		assertThat(dao.findByIdx(idx).getCategory()).isEqualTo(orderInfo.getCategory());
		assertThat(dao.findByIdx(idx).getPrice()).isEqualTo(orderInfo.getPrice());
		assertThat(dao.findByIdx(idx).getTourIdx()).isEqualTo(orderInfo.getTourIdx());
		assertThat(dao.findByIdx(idx).getTourPeople()).isEqualTo(orderInfo.getTourPeople());
		assertThat(dao.findByIdx(idx).getMemberIdx()).isEqualTo(orderInfo.getMemberIdx());
		assertThat(dao.findByIdx(idx).getAddressIdx()).isEqualTo(orderInfo.getAddressIdx());
	}

	@Test
	@Transactional
	@Rollback(true)
	void 주문조회() {
		// given
		Member member = new Member(0, "test123123@naver.com", "q`12q`12", "TestName", "01012345678", "12345");
		
		memberDao = template.getMapper(MemberDao.class);
		memberDao.insertMember(member);
		
		int memberIdx = memberDao.selectByEmailPw("test123123@naver.com", "q`12q`12").getIdx();
		
		OrderInfo orderInfo = OrderInfo.builder()
									   .category("shop")
									   .price(24000)
									   .tourIdx(60)
									   .tourPeople(3)
									   .memberIdx(memberIdx)
									   .addressIdx(1)
									   .build();
		dao.save(orderInfo);

		// when
		OrderInfo findOrder;
		findOrder = dao.findByIdx(orderInfo.getIdx());

		// then
		assertThat(findOrder.getCategory()).isEqualTo(orderInfo.getCategory());
		assertThat(findOrder.getPrice()).isEqualTo(orderInfo.getPrice());
	}

	@Test
	@Transactional
	@Rollback(true)
	void 주문조회_실패() {
		// given
		OrderInfo findOrder;

		// when
		findOrder = dao.findByIdx(-1);

		// then
		assertThrows(NullPointerException.class, () -> {
			findOrder.getCategory();
		});
	}

	@Test
	@Transactional
	@Rollback(true)
	void 멤버인덱스로_주문조회() {
		// given
		Member member = new Member(0, "test123123@naver.com", "q`12q`12", "TestName", "01012345678", "12345");
		
		memberDao = template.getMapper(MemberDao.class);
		memberDao.insertMember(member);
		
		int memberIdx = memberDao.selectByEmailPw("test123123@naver.com", "q`12q`12").getIdx();
		
		OrderInfo orderInfo1 = OrderInfo.builder()
									    .category("shop")
									    .price(22000)
									    .tourIdx(60)
									    .tourPeople(1)
									    .memberIdx(memberIdx)
									    .addressIdx(1)
									    .build();
		
		OrderInfo orderInfo2 = OrderInfo.builder()
									    .category("shop")
									    .price(22000)
									    .tourIdx(60)
									    .tourPeople(3)
									    .memberIdx(memberIdx)
									    .addressIdx(1)
									    .build();
		dao.save(orderInfo1);
		dao.save(orderInfo2);

		// when
		List<OrderInfo> findOrders = new ArrayList<>();
		findOrders = dao.findByMemberIdx(memberIdx);

		// then
		assertThat(findOrders.size()).isEqualTo(2);
	}

	@Test
	@Transactional
	@Rollback(true)
	void 카테고리와_멤버인덱스로_주문조회() {
		// given
		Member member = new Member(0, "test123123@naver.com", "q`12q`12", "TestName", "01012345678", "12345");
		
		memberDao = template.getMapper(MemberDao.class);
		memberDao.insertMember(member);
		
		int memberIdx = memberDao.selectByEmailPw("test123123@naver.com", "q`12q`12").getIdx();
		
		OrderInfo orderInfo1 = OrderInfo.builder()
									    .category("tour")
									    .price(22000)
									    .tourIdx(60)
									    .tourPeople(1)
									    .memberIdx(memberIdx)
									    .addressIdx(1)
									    .build();
		
		OrderInfo orderInfo2 = OrderInfo.builder()
									    .category("shop")
									    .price(22000)
									    .tourIdx(60)
									    .tourPeople(3)
									    .memberIdx(memberIdx)
									    .addressIdx(1)
									    .build();
		orderInfo1 = dao.save(orderInfo1);
		orderInfo2 = dao.save(orderInfo2);
		
		dao.updateStatus("confirmed", orderInfo1.getIdx());
		dao.updateStatus("confirmed", orderInfo2.getIdx());

		// when
		List<OrderInfo> findOrders = new ArrayList<>();
		findOrders = dao.findByCategoryAndMemberIdx("shop", memberIdx);

		// then
		assertThat(findOrders.size()).isEqualTo(1);
	}

}
