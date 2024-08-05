package com.springProject.repository;

import static com.springProject.entity.QPosts.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.core.types.dsl.StringPath;
import com.springProject.dto.PostsDto;
import com.springProject.utils.ConvertUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springProject.SearchData;
import com.springProject.entity.Posts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PostsRepositoryImpl implements PostsRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	// SQL 언어 수행 메서드
	// 공지사항 외 검색 조건에 맞는 검색 결과 찾기
	@Override
	public Page<PostsDto> searchPosts(SearchData searchData, String sortBy, Pageable pageable) {
		String keyword = searchData.getKeyword();
		String category = searchData.getCategory();
		String location = searchData.getLocation();
		int star = searchData.getStar();
		String hashtag = searchData.getHashtag();
		String startDate = searchData.getStartDate();
		String endDate = searchData.getEndDate();

		Timestamp start = stringToTimeStamp("start", startDate);
		Timestamp end = stringToTimeStamp("end", endDate);

		OrderSpecifier order = getOrder(sortBy);

		Map<StringPath, String> map = new HashMap<>();
		map.put(posts.title, keyword);
		map.put(posts.body, keyword);
		map.put(posts.category, category);
		map.put(posts.location, location);
		map.put(posts.hashtags, hashtag);

		log.info("keyword = {}, category = {}, location = {}, star = {}, hashtags = {}, startdate = {}, enddate = {}, sortBy = {}",
			searchData.getKeyword(), searchData.getCategory(), searchData.getLocation(), searchData.getStar(), searchData.getHashtag(),
			start, end, sortBy);


		// 실행할 쿼리 정의
		// 공지사항은 아닌 게시물 중 조건에 맞는 게시물 검색
		JPAQuery<Posts> query = queryFactory
			.selectFrom(posts)
			.where(posts.isNotice.isFalse()
				.and(
					posts.star.goe(star)
					.and(betweenDate(start, end))
					))
			.orderBy(order);

		// keyword -> or 조건이나 and 조건 앞에 오는 값은 null일 수가 없으므로 확인해주기
		// 다만, or이나 and 조건 뒤에 오는 값이 null이면 무시
		if (StringUtils.hasText(keyword)) {
			query = query
				.where(containWord(posts.title, keyword) // 일치하는 제목 혹은 내용을 포함하는 데이터 찾기 -> 대소문자 구분 X
					.or(containWord(posts.body, keyword))
					.or(isEqual(posts.category, category)) // 일치하는 카테고리를 가진 데이터 찾기 -> 대소문자 구분 X
					.or(isEqual(posts.location, location)) // 일치하는 위치 값을 가진 데이터 찾기 -> 대소문자 구분 X
					.or(isEqual(posts.hashtags, hashtag)) // 일치하는 해시태그를 포함하는 데이터 찾기 -> 대소문자 구분 X
				)
				.orderBy(order);
		}
		else if(StringUtils.hasText(category)) {
			query = query
				.where(isEqual(posts.category, category) // 일치하는 카테고리를 가진 데이터 찾기 -> 대소문자 구분 X
					.or(containWord(posts.title, keyword)) // 일치하는 제목 혹은 내용을 포함하는 데이터 찾기 -> 대소문자 구분 X
					.or(containWord(posts.body, keyword))
					.or(isEqual(posts.location, location)) // 일치하는 위치 값을 가진 데이터 찾기 -> 대소문자 구분 X
					.or(isEqual(posts.hashtags, hashtag)) // 일치하는 해시태그를 포함하는 데이터 찾기 -> 대소문자 구분 X
				)
				.orderBy(order);
		}
		else if(StringUtils.hasText(location)) {
			query = query
				.where(isEqual(posts.location, location) // 일치하는 위치 값을 가진 데이터 찾기 -> 대소문자 구분 X
					.or(containWord(posts.title, keyword)) // 일치하는 제목 혹은 내용을 포함하는 데이터 찾기 -> 대소문자 구분 X
					.or(containWord(posts.body, keyword))
					.or(isEqual(posts.category, category)) // 일치하는 카테고리를 가진 데이터 찾기 -> 대소문자 구분 X
					.or(isEqual(posts.hashtags, hashtag)) // 일치하는 해시태그를 포함하는 데이터 찾기 -> 대소문자 구분 X
				)
				.orderBy(order);
		}
		else if(StringUtils.hasText(hashtag)) {
			query = query
				.where(isEqual(posts.hashtags, hashtag) // 일치하는 해시태그를 포함하는 데이터 찾기 -> 대소문자 구분 X
					.or(containWord(posts.title, keyword)) // 일치하는 제목 혹은 내용을 포함하는 데이터 찾기 -> 대소문자 구분 X
					.or(containWord(posts.body, keyword))
					.or(isEqual(posts.category, category)) // 일치하는 카테고리를 가진 데이터 찾기 -> 대소문자 구분 X
					.or(isEqual(posts.location, location)) // 일치하는 위치 값을 가진 데이터 찾기 -> 대소문자 구분 X)
				)
				.orderBy(order);
		}


		// 출력될 쿼리 결과가 총 몇 개인지 확인 -> 게시물 개수 확인 해서 페이징
		long totalCount = query.stream().count();

    	query = query.offset(pageable.getOffset()) // pageable에 설정된 값으로 몇번째 데이터 부터 보여줄 지가 정의됨
					.limit(pageable.getPageSize()); // offset 부터 몇 개의 데이터를 출력될 지가 정의됨

    // 쿼리 실행이 끝난 데이터를 리스트 형태로 받은 후 Page<>에 담아 반환
    return new PageImpl<>(
        query.fetch().stream().map(ConvertUtils::convertPostsToDto).collect(Collectors.toList()),
        pageable, totalCount);
	}

	// 정렬 방법 정하기
	private static OrderSpecifier getOrder(String sortBy) {
		// default=newPost -> 최신 게시물 순 / oldPost -> 오래된 게시물 순
		// bigStar -> 별점 높은 순 / smallStar -> 별점 낮은 순
		OrderSpecifier order = switch (sortBy) {
			case "oldPost" -> posts.created_at.asc();
			case "bigStar" -> posts.star.desc();
			case "smallStar" -> posts.star.asc();
			default -> posts.created_at.desc();
		};
		return order;
	}


	// 일치 확인 메서드
	private static BooleanExpression isEqual(StringPath tableData, String searchData) {
		return StringUtils.hasText(searchData) ?
				Expressions.stringTemplate(
						"LOWER({0})", tableData)
					.eq(Expressions.stringTemplate(
						"LOWER({0})", searchData)) : null;
	}

	// 포함 확인 메서드
	private static BooleanExpression containWord(StringPath tableData, String searchData) {
		return StringUtils.hasText(searchData) ?
				Expressions.stringTemplate(
						"LOWER({0})", tableData)
					.contains(Expressions.stringTemplate(
						"LOWER({0})", searchData)) : null;
	}

	// 시작날짜부터 끝나는날짜 사이에 생성된 게시물 불러오기
	private BooleanExpression betweenDate(Timestamp start, Timestamp end) {

		return posts.created_at.between(start, end);

	}

	// 날짜가 String으로 들어오기 때문에 TimeStamp 값으로 바꿔줘야함
	private static Timestamp stringToTimeStamp(String point, String dateString) {
		//  String to Timestamp
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);// 날짜와 시간을 엄격하게 확인
		if(StringUtils.hasText(dateString)) {
			try {
				Date stringToDate = dateFormat.parse(dateString);
				Timestamp stringToTimestamp = new Timestamp(stringToDate.getTime());
				System.out.println(stringToTimestamp);
				return stringToTimestamp;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// 시작점이 지정 안되었으면 첫번째 게시물부터 출력해야함
		if (point.equals("start"))
			return Timestamp.valueOf("1900-01-01 00:00:00.000");

		// 끝 점이 지정 안되었으면 지금까지 게시된 게시물까지 출력해야함
		else
			return new Timestamp(System.currentTimeMillis());
	}

}
