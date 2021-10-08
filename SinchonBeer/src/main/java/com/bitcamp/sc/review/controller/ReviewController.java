package com.bitcamp.sc.review.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bitcamp.sc.member.domain.LoginInfo;
import com.bitcamp.sc.review.domain.ReviewVO;
import com.bitcamp.sc.review.service.ReviewService;

@Controller
@RequestMapping("/review")
public class ReviewController {

	@Autowired
	ReviewService reviewService;

	// 01-1. 메인 화면
	@RequestMapping("")
	public String reviewMain(Model model, HttpServletRequest request) {
		try {
			model.addAttribute("reviewMain", reviewService.listAllReview());
			
			HttpSession session = request.getSession();
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			System.out.println("로그인정보 : " + loginInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "review/reviewMain";
	}	
	// 01-2. 메인 화면
	@RequestMapping("/")
	public String reviewMain2(Model model, HttpServletRequest request) {
		try {
			model.addAttribute("reviewMain", reviewService.listAllReview());
			
			HttpSession session = request.getSession();
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			System.out.println("로그인정보 : " + loginInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "review/reviewMain";
	}
	
	
	// 02-1. 쓰기 화면
	@RequestMapping(value = "/write", method = RequestMethod.GET)
	public String goToWrite(HttpServletRequest request, Model model) throws Exception {
		
		model.addAttribute("order_idx", request.getParameter("order_idx"));
		return "review/writing";
  }

	// 02-2. 쓰기 실행 컨트롤러
	@ResponseBody
	@RequestMapping(value = "/write.do", method = RequestMethod.POST)
	public int ajaxinsert_Review(HttpServletRequest request) throws Exception {
		
		System.out.println(request.getParameter("title"));
		
		int check  = 0;
		
		ReviewVO vo = new ReviewVO();
		vo.setTitle(request.getParameter("title"));
		vo.setName(request.getParameter("author"));
		vo.setContents(request.getParameter("content"));
		vo.setOidx(Integer.parseInt(request.getParameter("order_idx")));
		
		try {
			check = reviewService.insertReview(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return check;
	}
	
	
	// 03. 상세 보기 화면
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String goToView(HttpServletRequest request, Model model) throws Exception {
		
		/* System.out.println(request.getParameter("idx")); */
		int idx = 0;
		ReviewVO vo = new ReviewVO();
		
		if( request.getParameter("idx") != null) {
			idx = Integer.parseInt(request.getParameter("idx"));
		}
		try {
			vo = reviewService.readReview(idx);
			model.addAttribute("view", vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	return "review/view";
  }
	
    
	// 04. 게시글 삭제
	@ResponseBody
	@RequestMapping("delete.do") 
	public int delete(HttpServletRequest request, Model model) throws Exception { 
		System.out.println("idx: " + request.getParameter("idx"));
		int idx = 0;
		int check = 0;
		
		if( request.getParameter("idx") != null) {
			idx = Integer.parseInt(request.getParameter("idx"));
		}
		try {
			reviewService.deleteReview(idx);
			check = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return check; 
	}

	
	// 05-1. 수정화면
	@RequestMapping(value = "/modify", method = RequestMethod.GET)
	public String goToModify(HttpServletRequest request, Model model) throws Exception {
		
		int idx;
		ReviewVO vo = new ReviewVO();
		
		if(!"".equals(request.getParameter("idx")) && request.getParameterMap().containsKey("idx")) {
			idx = Integer.parseInt(request.getParameter("idx"));
			try {
				vo = reviewService.readReview(idx);
				System.out.println("write : " + vo.toString());
				model.addAttribute("view", vo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	return "review/modify";
  }
	
	// 05-2. 수정 실행 컨트롤러
		@ResponseBody
		@RequestMapping(value = "/modify.do", method = RequestMethod.POST)
		public int ajaxmodify_Review(HttpServletRequest request) throws Exception {
			
			int check  = 0;
			
			ReviewVO vo = new ReviewVO();
			vo.setIdx(Integer.parseInt(request.getParameter("idx")));
			vo.setTitle(request.getParameter("title"));
			vo.setName(request.getParameter("author"));
			vo.setContents(request.getParameter("content"));
			
			try {
				check = reviewService.updateReview(vo);
			} catch (Exception e) {
			}
			return check;
		}
		
		
	// 06. 게시글 좋아요
	@ResponseBody
	@RequestMapping("like.do") 
	public int like(HttpServletRequest request, Model model) throws Exception { 
		System.out.println("idx: " + request.getParameter("idx"));
		int idx = 0;
		int check = 0;
		
		ReviewVO vo = new ReviewVO();
		vo.setLikes(Integer.parseInt(request.getParameter("likes")));
		
		if( request.getParameter("idx") != null) { 
			idx = Integer.parseInt(request.getParameter("idx")); 
		} try {
			reviewService.likeReview(idx);
			check = 1; 
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		 
		return check; 
	}
}
