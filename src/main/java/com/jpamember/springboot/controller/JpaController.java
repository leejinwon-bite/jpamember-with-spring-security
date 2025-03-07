package com.jpamember.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpamember.springboot.DTO.MemberDTO;
import com.jpamember.springboot.entity.Member;
import com.jpamember.springboot.repository.MemberRepository;

@Controller
public class JpaController {

//   DI
	@Autowired
	private MemberRepository memberRepository;

	/*
	 * @PreAuthorize("hasRole('USER')")는 USER라는 권한을 가진 로그인 정보를 인가함. USER가 아니면,
	 * password를 검증 하지 않는다.
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/jpa/memberWriteForm")
	public String memberWriteForm(@RequestParam(value = "num", required = false) Integer num, // CRUD U를 하기 위해서 Model쪽에서
																								// num이라는 애를 1 증가한게 아닌
																								// 등록된 num
			// 그 자체를 컨트롤러로 몰래 안보이게 넘겨줘야 시퀀스가 1 증가한게 아닌게 되어서 신규 등록이 아닌 수정이 되어진다. 그걸 위해서
			// RequestParam을 사용한것.
			Model model) {

		if (num != null) {
			System.out.println(num);

			// 기존 회원 정보 수정
			Member member = memberRepository.findById(num).orElse(null);

			model.addAttribute("memberDTO", member);
			model.addAttribute("formTitle", "Modification");
		} else {
			System.out.println("null 이네요.....!!");

			// 신규 회원 등록
			model.addAttribute("memberDTO", new MemberDTO()); // new MemberDTO() 는 강사가 "빈 객체"라고 하였음.
//			메서드 매개에 MemberDTO가 없음에도 {}안에 사용됨.
			model.addAttribute("formTitle", "Registration");
		}

		return "jpa/memberWriteForm";
	}

	@PostMapping("/jpa/memberWriteOk")
	public String insertMember(MemberDTO memberDTO, Model model, @RequestParam(required = false) int num,
			@RequestParam(required = false) String name, @RequestParam(required = false) int age,
			@RequestParam(required = false) String phone) {

		try {
			// 등록 처리
			System.out.println(memberDTO.getName());
			System.out.println(memberDTO.getId());
			System.out.println(memberDTO.getPhone());
			System.out.println(memberDTO.toString());

			// 1 단계 DTO ------> Entity로 변환

			Member member = memberDTO.toEntity();
			System.out.println(member.toString());

			// 2 Repository로 ---> Entity를 -----> DB에 저장
			memberRepository.save(member);
//			int saved = memberRepository.updateMemberQuery( 1, "테스트", 99);    //등록, 수정 처리하는 부분
			int saved = memberRepository.updateMemberQuery(num, name, age, phone);
//			System.out.println( saved.toString() );
//			saved는 수정 처리된 레코드의 갯수를 반환한다. 
			System.out.println(saved);

		} catch (Exception e) {

		}

		return "redirect:/";
	}

	/*
	 * // 회원 리스트
	 * 
	 * @GetMapping(value = "/jpa/memberList") public String memberList( Model model,
	 * 
	 * @PageableDefault( size=3) Pageable pageable,
	 * 
	 * @RequestParam(value = "searchKeyword", required = false, defaultValue = "")
	 * String searchKeyword,
	 * 
	 * @RequestParam(value = "searchCategory", required = false, defaultValue = "")
	 * String searchCategory ) {
	 * 
	 * System.out.println("--------------------------------");
	 * System.out.println("searchCategory: "+searchCategory);
	 * System.out.println("searchKeyword: "+searchKeyword);
	 * System.out.println("--------------------------------");
	 * 
	 * // JPA 방식이 아닌 경우에는 아래처럼 처리 (mybatis 방식) // List<MemberDTO> memberList =
	 * memberService.getMemberList();
	 * 
	 * // JPA 방식 // List<Member> members = memberRepository.findAll();
	 * 
	 * // 바로 위 하고 똑같은 JPA 방식 + 페이징 --> Page(위에 List 대체), Pageable (제공되는 객체들) -->
	 * pom.xml // --> Spring Data JPA // 페이징 관련해서 필요한 임포트 (임포트 하려다 보면 여러가지가 떠서 외워
	 * 두는게 좋음) // import org.springframework.data.domain.Page; // import
	 * org.springframework.data.domain.Pageable; // 정렬 관련해서 필요한 임포트 // import
	 * org.springframework.data.domain.Sort; // Pageable 기본적인 사용법 --> 파라메터로 url로
	 * page(몇 페이지냐? 필수), size(한 페이지당 글 갯수 몇개냐? 필수), sort(정렬은? // 선택옵션)등의 정보를 넘겨줌! //
	 * 페이지 번호는 0부터 시작하므로 뷰페이지 에서 Previous / Next 구현시 적절히 +1, -1(Previous) 해준다. // 자주
	 * 사용하는 필수적인 페이징 관련 메서드들은 암기. // 뷰페이지단에서 어떻게 페이징 관련 값들을 조건 처리하고 뿌려주는지에 대해서 반복,
	 * 숙달, 연습하면 아주 쉬워지게 된다. // 뷰페이지에서 적용할 값을 messages.properties(기존
	 * application.properties말고 또하나 만드는 거임) 파일에 정의하고 사용. // 예) 한 페이지에 보여질 size 값
	 * 등등....
	 * 
	 * // 기존 리스트 출력 // Page<Member> members = memberRepository.findAll(pageable);
	 * 
	 * // 검색 기능이 있는 리스트 출력 // Page<Member> members =
	 * memberRepository.findByNameContaining(searchKeyword, pageable);
	 * 
	 * // 검색 리스트 출력 --> name, id, phone (검색창 value값들) Page<Member> members = null;
	 * //초기값을 준것 // 검색 카테고리별로 비교하여 해당하는 검색 메서드 호출 --> 문자열 비교는 == 이 아니라 equals 사용.
	 * if(searchCategory.equals("name")) { members =
	 * memberRepository.findByNameContaining(searchKeyword, pageable);
	 * System.out.println("이름 검색"); } else if(searchCategory.equals("id")) {
	 * System.out.println("아이디 검색"); members =
	 * memberRepository.findByIdContaining(searchKeyword, pageable); } else
	 * if(searchCategory.equals("phone")) { System.out.println("휴대폰 번호 검색"); //
	 * members = memberRepository.findByPhoneContaining(searchKeyword, pageable); //
	 * members = memberRepository.findByPhoneLike(searchKeyword + "%", pageable); //
	 * members = memberRepository.findByPhoneStartsWith(searchKeyword, pageable); //
	 * members = memberRepository.findByPhoneEndsWith(searchKeyword, pageable);
	 * members = memberRepository.findByIdContains(searchKeyword, pageable); } else
	 * { System.out.println("전체 검색"); // members =
	 * memberRepository.findAll(pageable); // and, or 쿼리문 사용 name 속성엔 사용자가 적는 값이 담기게
	 * 됨 // members = memberRepository.findByNameAndId(searchCategory,
	 * searchKeyword, pageable);
	 * 
	 * // or 쿼리문 사용 // members =
	 * memberRepository.findByNameContainsOrIdContains(searchKeyword, searchKeyword,
	 * pageable); // members =
	 * memberRepository.findByNumGreaterThanEqualOrderByNameAsc(10, pageable);
	 * 
	 * }
	 * 
	 * // 페이징 관련 유용한 메서드들 System.out.println( "getTotalPages = " +
	 * members.getTotalPages()); // getTotalPages() 총 페이지 수 System.out.println(
	 * "getTotalElements = " + members.getTotalElements()); // getTotalElements() //
	 * 총 글의 갯수(열값 행의 갯수) System.out.println( "getNumber = " + members.getNumber());
	 * // V에서 현재의 몇 페이지 번호에 있는지 System.out.println( "getSize = " +
	 * members.getSize()); // 한 페이지당 최대 몇개의 열값 행의 갯수를 설정하는지 System.out.println(
	 * "getSort = " + members.getSort()); // 정렬(선택옵션)
	 * System.out.println(members.getPageable()); // number(page 몇페이지? 0부터 시작),
	 * size, sort 이 3가지 값을 모두 가짐.
	 * 
	 * 
	 * 
	 * // 객체 리스트 전달 model.addAttribute("members", members);
	 * 
	 * return "jpa/memberList"; }
	 */

	/*
	 * @GetMapping("/jpa/memberList") public String memberList2(Model model,
	 * 
	 * @RequestParam(value="searchKeyword", required = false, defaultValue="")
	 * String searchKeyword, Pageable pageable) {
	 * 
	 * Page<Member> members = null;
	 * 
	 * //전체출력 if(searchKeyword.isEmpty()) { System.out.println("전체출력"); members =
	 * memberRepository.findAll(pageable); }else { System.out.println("검색출력"); //
	 * members = memberRepository.findByNumGreaterThanEqualOrderByNameAsc(6,
	 * pageable); members =
	 * memberRepository.findByAgeLessThanEqualOrderByNameDesc(20, pageable); }
	 * 
	 * // 객체 리스트 전달 - 모델에 담아서 model.addAttribute("members",members);
	 * 
	 * return "jpa/memberList"; }
	 */

//	회원 리스트 3 JPA 쿼리 메서드 만들기 3
//	@GetMapping("/jpa/memberList")
//	public String memberList3(Model model, Pageable pageable,
//			@RequestParam(value="searchKeyword", required = false, defaultValue="") String searchKeyword) {

//		Page<Member> members = null;

//		//전체출력
//		if(searchKeyword.isEmpty()) {
//			System.out.println("전체출력");

//			PageRequest.of() 사용법
//			1. Page 타입을 사용하여 페이징 구현시 여러 유용한 기능을 제공 --> PageRequest.of() 역시 매우 유용하다고 한다.
//			2. PageRequest.of() 사용하면 --> 여러 설정 값들을 파라미터로 전달하여 페이징에 필요한 설정을 하는 것이 가능.
//			3. SpringBoot 2.0 버전으로 넘어오면서 PageRequest.of() 를 사용.
//		4. 크게 4가지 파라미터 값을 기본적으로 암기 해야함. --> PageRequest.of(Number(현재 페이지 0부터 시작),
//			size, sort, Column 이름)
//			5. 사용방법 --> 5-1 PageRequest.of(int page, int size) --> 파라미터 2개도 가능함.
//			             5-2 PageRequest.of(int page, int size, Sort sort) --> 파라미터 3개도 가능 
//			             5-2 PageRequest.of(int page, int size, Sort.Direction.Asc[Desc], String column) --> 요걸기억

//			등록 날짜순 정렬
//			pageable = PageRequest.of(2, 3, Sort.Direction.DESC, "num"); // num 기준 정렬

//         따로 지정 sort 지정 안하고 4번째 파라메터 설정하고 싶을때
//			pageable = PageRequest.of( pageable.getPageNumber(), 10, Sort.by("name"));

//			members = memberRepository.findAll(pageable);

//		}else {
//			System.out.println("검색출력");
//			members = memberRepository.findByNumGreaterThanEqualOrderByNameAsc(6, pageable);
//			members = memberRepository.findByAgeLessThanEqualOrderByNameDesc(20, pageable);
//			members = memberRepository.findByNameContainsOrderByNameDesc(searchKeyword, pageable);			
//			pageable = PageRequest.of(pageable.getPageNumber(), 3, Sort.Direction.ASC ,"name");

//			다중정렬 --> 2개 조건을 적용하여 정렬하기 --> 회원 검색시 나이순(Desc) + 이름순(Asc) --> 나이순 먼저 정렬 후 이름순으로 정렬
//			PageableDefault --> 하나의 컬럼만 기준으로 정렬을 적용, PageRequest.of() --> 여러 개의 조건을 and 사용하여 정렬 적용
//			pageable = PageRequest.of( pageable.getPageNumber(), 10, Sort.by("age").descending()
//					   .and(Sort.by("name").descending()) );

//			members = memberRepository.findByNameContains(searchKeyword, pageable);
//		}

//		객체 리스트 전달 - 모델에 담아서
//		model.addAttribute("members",members);
//		model.addAttribute("searchKeyword", searchKeyword);

//		return "jpa/memberList";
//	}

//	회원 리스트 5 - @Query Annotation 활용한 편
//	@GetMapping(value="/jpa/memberList")
//	public String memberList5( Model model, Pageable pageable, @RequestParam(value= "searchKeyword", required=false
//	, defaultValue="") String searchKeyword) {
//		
//		변수 초기화
//		Page<Member> members = null;
//		
//		if(searchKeyword.isEmpty()) {
//			System.out.println("전체 출력");
//			
//			members = memberRepository.findAll(pageable);
//		}else {
//			System.out.println("검색 출력");
//			pageable = PageRequest.of(pageable.getPageNumber(), 2, Sort.Direction.ASC, "name");
//			members = memberRepository.nameSearch(searchKeyword, pageable);
//			members = memberRepository.findByNameBetween(searchKeyword, pageable);
//			members = memberRepository.findByNameContainingAndAgeBetweenOrderByNameDesc(searchKeyword, 10, 20,
//					pageable);
//		}
//		model.addAttribute("members", members);
//		model.addAttribute("searchKeyword", searchKeyword);
//		
//		return "jpa/memberList";
//	}

//	@Query 파라미터 바인딩 ---> : 콜론 사용
//	@GetMapping(value="/jpa/memberList")
//	public String memberList6( Model model, Pageable pageable, 
//			@RequestParam(required=false, defaultValue="0") int from,
//			@RequestParam(required=false, defaultValue="0") int to,
//			@RequestParam(required=false, defaultValue="") String searchKeyword) {
//		
//		
//		Page<Member> members = null;
//		
//		if(searchKeyword.isEmpty()) {
//			System.out.println("전체 출력");
//			
//			members = memberRepository.findAll(pageable);
//		}else {
//			System.out.println("검색 출력");
//			members = memberRepository.findMembers(searchKeyword, to, from, pageable);
//		}
//		model.addAttribute("members", members);
//		model.addAttribute("searchKeyword", searchKeyword);
//		
//		return "jpa/memberList";
//	}

//	회원 리스트 7 ->회원 존재 여부 - existsByName()
//	@GetMapping(value="/jpa/memberList")
//	public String memberList7( Model model,  
//			@RequestParam(required= false) String id,
//			@RequestParam(required=false, defaultValue="") String searchKeyword,
//			@PageableDefault( size=3) Pageable pageable) {
//		
//		
//		Page<Member> members = null;
//		
//		if(searchKeyword.isEmpty()) {
//			System.out.println("전체 출력");
//			
//			members = memberRepository.findAll(pageable);
//		}else {
//			System.out.println("검색 출력");
//			boolean isMember = memberRepository.existsByName(searchKeyword);
//			int isExistCount = memberRepository.existsQuery(searchKeyword, id);
//			
//			존재여부
//			System.out.println("isMember: " + isMember);
//			System.out.println("isExistCount = " + isExistCount);
//			
//			members = memberRepository.findByName(searchKeyword, pageable);
//			members = memberRepository.selectAllSQL(searchKeyword, pageable);
//		}
//		model.addAttribute("members", members);
//		model.addAttribute("searchKeyword", searchKeyword);
//		
//		return "jpa/memberList";
//	}

	
	  // 회원 리스트
	  @GetMapping(value = "/jpa/memberList") 
	  public String memberList( Model model,
	  Pageable pageable,
	  @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword)
	  {
		  
	   pageable = PageRequest.of(pageable.getPageNumber(), 5, Sort.Direction.ASC, "age");
	   
	   Page<Member> members = null;
	   
		if(searchKeyword.isEmpty()){
			
	      members = memberRepository.findAll(pageable);
	      
		 }else {
	  
	  // 검색 기능이 있는 리스트 출력 
//			 int age는 못쓴다. 순신 이라는 문자는 숫자 int로 변형될 수 없기 때문임.
//			 그렇다고 int를 String age로 고친다는 것도 하면 안좋다. 이유는 repository에서 age로 between 을 사용할 수 없게 된다.
//			 숫자를 검색한다고 하더라도, 안된다. 이유는 memberList.html에 type=number인 애가 없어서?
	      members = memberRepository.findByNameContainsOrIdContainsOrPhoneContains
			  (searchKeyword, searchKeyword, searchKeyword, pageable);
	      
	      model.addAttribute("searchKeyword", searchKeyword);
		 }
	  
	  model.addAttribute("members", members);
	  
	  return "jpa/memberList"; 
}
	 

	// 회원삭제
	@PreAuthorize("hasRole('USER')")
	@GetMapping(value = "/jpa/memberDelete")
	public String memberDelete(@RequestParam(value = "num", required = false) Integer num) {
		// 삭제처리시 DB에 있는 애를 가져와서 삭제 하는것이고, memberList.html에 삭제 버튼을 눌러서 삭제가 되는거라, form하곤
		// 관련이
		// 없어 보여서 @RequestParam 을 사용하는 것이 이해가 안간다. 하지만, 그냥 그렇게 외우면 될것같다. 뭐 이해 안가는게 당연하긴
		// 하다.
		// 애초에 사용자가 입력하는 form에서 가져오는거하고 DB에 저장된 값을 가져오는거 하고 다르긴 하니깐 말이다.

		System.out.println(num);
		memberRepository.deleteById(num);

		return "redirect:/";
	}

	// 회원 상세 페이지 paging
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/jpa/memberDetail")
	public String memberDetail(@RequestParam(value = "num", required = false) Integer num, Model model) {

		System.out.println(num);

		// findById로 해당 num에 해당되는 num 회원을 찾아서 뷰페이지로 전달 하는 목적
		Member member = memberRepository.findById(num).orElse(null);
		model.addAttribute("member", member);

		return "/jpa/memberDetail";
	}

}