package com.bitcamp.sc.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.bitcamp.sc.member.domain.Member;
import com.bitcamp.sc.member.repository.MemberDao;
import com.bitcamp.sc.order.domain.OrderInfo;
import com.bitcamp.sc.order.service.impl.OrderServiceImpl;

@SpringBootTest
class OrderServiceTest {
	@Autowired
	OrderServiceImpl orderService;
	@Autowired
	SqlSessionTemplate template;
	MemberDao memberDao;
	
	@Test
	@Transactional
	@Rollback(true)
	@DisplayName("주문정보를 넣으면 주문이 생성된다")
	void createOrderTest() {
		// given
		
		Member member = new Member(0, "test123123@naver.com", "q`12q`12", "TestName", "01012345678", "12345");
		
		memberDao = template.getMapper(MemberDao.class);
		int memberIdx = memberDao.insertMember(member);
		
		OrderInfo orderInfo = OrderInfo.builder()
				.category("tour")
				.price(3000)
				.tourIdx(60)
				.tourPeople(3)
				.memberIdx(2)
				.addressIdx(memberIdx)
				.build();
		
		// when
		int orderIdx = orderService.createOrder("tour", orderInfo);
		
		// then
		OrderInfo findOrderInfo = orderService.getOrderInfo(orderIdx);
		
		assertThat(findOrderInfo.getCategory()).isEqualTo(orderInfo.getCategory());
		assertThat(findOrderInfo.getPrice()).isEqualTo(orderInfo.getPrice());
		assertThat(findOrderInfo.getTourIdx()).isEqualTo(orderInfo.getTourIdx());
		assertThat(findOrderInfo.getTourPeople()).isEqualTo(orderInfo.getTourPeople());
		assertThat(findOrderInfo.getMemberIdx()).isEqualTo(orderInfo.getMemberIdx());
		assertThat(findOrderInfo.getAddressIdx()).isEqualTo(orderInfo.getAddressIdx());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	@DisplayName("주문정보를 잘못 넣으면 주문 생성이 실패된다")
	void createOrderTestFail() {
		// given
		// String category, int price, int tourIdx, int tourPeople, int memberIdx, int addressIdx
		OrderInfo orderInfo1 = new OrderInfo("tour", 0, 60, 3, 2, 1);
		OrderInfo orderInfo2 = new OrderInfo("tour", 3000, 0, 3, 2, 1);
		OrderInfo orderInfo3 = new OrderInfo("tour", 3000, 60, 0, 2, 1);
		OrderInfo orderInfo4 = new OrderInfo("tour", 3000, 60, 3, 0, 1);
		
		// when
		// then
		assertThrows(IllegalStateException.class, () -> {
			orderService.createOrder("tour", orderInfo1);
		});
		assertThrows(IllegalStateException.class, () -> {
			orderService.createOrder("tour", orderInfo2);
		});
		assertThrows(IllegalStateException.class, () -> {
			orderService.createOrder("tour", orderInfo3);
		});
		assertThrows(IllegalStateException.class, () -> {
			orderService.createOrder("tour", orderInfo4);
		});
	}
}
