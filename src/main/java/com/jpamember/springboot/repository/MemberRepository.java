package com.jpamember.springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.jpamember.springboot.entity.Member;

// CrudRepository, 혹은 JpaRepository 상속
// 앤터티가 테이블명인 애를 재너릭에 넣음.
public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	// 회원 검색용 메서드 select박스의 value 값들에 따라 db조회 메소드의 기능도 달리하는것.
	Page<Member> findByNameContaining( String name, Pageable pageable );
	
	Page<Member> findByIdContaining( String id, Pageable pageable );
	
	//Containing은 포함 이므로 010- 만 검색해도 010 이 포함된 모든 번호를 검색
	Page<Member> findByPhoneContaining( String phone, Pageable pageable );
	
	//LIKE 함수가 포함된 JPQL 메서드 작성 %111% 사용.
	Page<Member> findByPhoneLike( String phone, Pageable pageable );
	
//	밑에 JPQL은 where phone like '111%'; 에 해당하고,
	Page<Member> findByPhoneStartsWith( String phone, Pageable pageable );
//	밑에 JPQL은 where phone like '%111'; 에 해당한다. 그러니까 정리하면 findByPhoneLike()하고 똑같은 기능이라 보면 됨. 
//	차이점은 대신 %를 바로 repository 추상메서드에서 바로 지정한다는 것이다. 그냥 뭐 이런 방법도 있다고 알면됨.
	Page<Member> findByPhoneEndsWith( String phone, Pageable pageable );
	
//	대소문자 구분하기 --- MySQL command line에서 id의 columm 자료형을 varchar --> varbinary로 변경해주면 됨. 그럼 소문자는 소문자.
//	대문자는 대문자로 검색시 출력이 된다.
	Page<Member> findByIdContains(String id, Pageable pageable);
	
//	where절 and 쿼리 조건 만들기
	Page<Member> findByNameAndId( String name, String id, Pageable pageable );
	
//	where절 or 쿼리 조건 만들기
	Page<Member> findByNameOrId( String name, String id, Pageable pageable );
	
//	where절 or+contains 쿼리조건만들기 plus 010-4137에서 4137만 입력해도 다 나오는 것처럼 성씨나, ms, mr 일부만 적어도 다 검색되게 하는거
//	구현하는 메서드임 이게 아마 가장 네이버나 구글같은 검색엔진에서 일부만 입력해도 관련된거 다 나오는 것처럼 그거에 가장 가까운 완성도 있는 메서드
//	라고 생각함.
	Page<Member> findByNameContainsOrIdContains( String name, String id, Pageable pageable );
	
    Page<Member> findByNameContainsOrIdContainsOrPhoneContains
	      ( String name, String id, String phone, Pageable pageable );
	
//	 Page<Member> findByNameContainsOrIdContainsOrPhoneContainsOrAgeContains(
//			String name, String id, String phone, Integer age, Pageable pageable);
	
//	정렬 쿼리 메소드 만들기 ~이상, 오름차순 정렬
//	 어째서 ???? num의 Type이 여기선 Integer 인 것일까????????
	Page<Member> findByNumGreaterThanEqualOrderByNameAsc( Integer num, Pageable pageable );
	
//	문제1 - 나이가 20 살 이하인 회원들을 이름을 기준으로 내림차순으로 정렬하는 검색을 구현 하시오.
	Page<Member> findByAgeLessThanEqualOrderByNameDesc( Integer age, Pageable pageable );
	
//	문제2 - 순신 이라는 이름이 들어가는 회원을 모두 찾으세요, 이때 이름기준 오름차순 정렬, 페이징도 같이 구현하시오.
	Page<Member> findByNameContainsOrderByNameDesc(String name, Pageable pageable);
	
	Page<Member> findByNameContains(String name, Pageable pageable);
	
//	@Query 에너테이션을 이용하여 쿼리를 작성하는 법
//	사용하는 이유? --> 보다 구체적이고 좀더 직관적 빠르고 유연한 쿼리를 작성하여 사용하고자 할때 많이 사용.
//	JPQL(Java Persistence Query Language) 이라는 미니 sql 사용. 또는 순수 SQL 사용
//	회원 이름으로 검색하기
//	@Query("select m from Member m where m.name = ?1  and m.age > 10 order by m.name asc")
	
//	이름이 "순신" 들어가고, 나이가 20 이상이고, 정렬은 이름기준 오름차순으로 정렬하시오
	@Query("select m from Member m where m.name Like %?1% and m.age > 20 order by m.name asc")
	public Page<Member> nameSearch(String name, Pageable pageable);
	
//	이름이 "순신이" 들어가고, 나이가 10~20 사이의 회원을 이름기준 오름차순으로 정렬하시오 between 사용
	@Query("select m from Member m where (m.name Like %?1%) and (m.age between 10 and 20) order by m.name asc")
	public Page<Member> findByNameBetween( String name, Pageable pageable );
	
//	위 문제를 키워드 쿼리 메소드로 만들어 보시오.
	Page<Member> findByNameContainingAndAgeBetweenOrderByNameDesc( String name, int start, int end,
			Pageable pageable );
	
	
//	JPQL 다양한 사용법id
//	: 콜론을 사용하는 이름 기반 (순서가 햇갈려서 잘못 입력해도 에러 표시 안나는 ? 사용보다는 실수가 훨씬 덜 하게된다. 실무에서 더 선호하는편)
	@Modifying
	@Transactional
	@Query("update Member m set m.name = :name, m.age = :age, m.phone = :phone where m.num = :num")
	int updateMemberQuery(@Param("num") int num, @Param("name") String name, @Param("age") int age,
			@Param("phone") String phone);
	
	
//	JPQL 사용 Like 콜론 : 파라미터 바인딩
//	between 10 and 20 사용해서 나이 값을 매개변수로 처리하여 재작성 해보시오. 이름엔 순신 들어가고 나이 10~20, 정렬 이름기준 오름차순 하시오.
	@Query("select m from Member m where (m.name like %:name%) and (m.age between :from and :to)"
			+ " order by m.name asc")
	Page<Member> findMembers(@Param("name") String name, @Param("to") int to, @Param("from") int from,
			Pageable pageable);
	
//	exists, count 쿼리 메서드, 순수 SQL 사용하기
//	하나의 테이블이 다른 테이블과 연관 관계 일때 매우 유용한 아이라고 함.
//	예시) 1. 특정 회원이 주문한적이 있는지 파악하기 위해 주문번호가 존재하는지 알고싶은 경우,
//	예시) 2. 이름과 아이디가 동일한 회원이 존재하는지 알고 싶은 경우.
//	select * from member m where (m.name like '%순신%') and exists (select 1 from member mm where mm.age > 10);
	
	
	
	
	
	
	
	
	
	Page<Member> findByName(String name, Pageable pageable);
	
	boolean existsByName(String name);
	
//	select count(*) from member where name = '홍순신'; --> 1출력 예상
	@Query(
			"select count(m.num)" + 
			"from Member m" + 
			" where m.name = :name and m.id = :id"
			)
	int existsQuery(@Param(value = "name") String name, @Param(value = "id") String id);
	
//	일반 순수 sql 쿼리
//	JPA 사용 목적과는 반대인 부분 권장 하지는 않는 편임.
//	특정 데이터 베이스에 특화된 함수들을 사용하는것이 가능
//	속성으로 nativeQuery = true를 설정 --> exists 함수 사용이 @Query안에 사용 가능해짐.
//	해석: exists 내부 서브쿼리는 전체 테이블 안에서 단 1개라도 조건에 해당한다면 메인 쿼리를 실행하는것이다. 만약 age < 0과 같이
//	테이블 모든 행에 대해서 만족 시키는 데이터가 없으면 메인쿼리가 아에 실행조차도 안되서 검색해도 아무것도 안나오게 됨.
	@Query(value = "select * from member m where m.name like %:searchKeyword% and exists "
			+ "(select 1 from member mm where mm.age < 20)", nativeQuery = true)
	Page<Member> selectAllSQL(@Param("searchKeyword") String searchKeyword, Pageable pageable);
	
	
	
	
	
	
	
	
	
	
	

}
