package com.springProject.repository;

import static com.springProject.entity.QPosts.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import com.springProject.dto.PostsDto;
import com.springProject.utils.ConvertUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springProject.SearchData;
import com.springProject.entity.Posts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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

		// 실행할 쿼리 정의
		JPAQuery<Posts> query = queryFactory
			.selectFrom(posts)
			.where(posts.star.goe(star)
			.where(posts.isNotice.isFalse()
				.and((posts.star.goe(star))
				.or(containsKeyword(keyword))
				.or(eqCategory(category))
				.or(eqLocation(location))
				.or(containsHashtag(hashtag))
				.or(betweenDate(start, end))))
			.orderBy(order);

		long totalCount = query.stream().count(); // 쿼리 결과가 총 몇개인지 확인

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

	// 일치하는 제목 혹은 내용을 포함하는 데이터 찾기 -> 대소문자 구분 X
	private BooleanExpression containsKeyword(String keyword) {
		if(keyword != null) {
			return (Expressions.stringTemplate(
					"LOWER({0})", posts.title)
				.contains(Expressions.stringTemplate(
					"LOWER({0})",keyword)))
				.or(Expressions.stringTemplate(
					"LOWER({0})", posts.body)
				.contains(Expressions.stringTemplate(
					"LOWER({0})",keyword)));
			}
		return (posts.title.contains(posts.title)).or(posts.body.contains(posts.body));
	}


	// 일치하는 카테고리를 가진 데이터 찾기 -> 대소문자 구분 X
	private BooleanExpression eqCategory(String category) {
		if( category != null)

			return Expressions.stringTemplate(
					"LOWER({0})", posts.category)
				.eq(Expressions.stringTemplate(
					"LOWER({0})",category));
		return posts.category.eq(posts.category);
	}

	// 일치하는 위치 값을 가진 데이터 찾기 -> 대소문자 구분 X
	private BooleanExpression eqLocation(String location) {
		if(location != null)
			return Expressions.stringTemplate(
					"LOWER({0})", posts.location)
				.eq(Expressions.stringTemplate(
					"LOWER({0})",location));
		return posts.location.eq(posts.location);
	}

	// 일치하는 해시태그를 포함하는 데이터 찾기 -> 대소문자 구분 X
	private BooleanExpression containsHashtag(String hashtag) {
		if(hashtag != null)
			return Expressions.stringTemplate(
					"LOWER({0})", posts.hashtags)
				.contains(Expressions.stringTemplate(
					"LOWER({0})",hashtag));
		return posts.hashtags.contains(posts.hashtags);
	}

	// 시작날짜부터 끝나는날짜 사이에 생성된 게시물 불러오기
	private BooleanExpression betweenDate(Timestamp start, Timestamp end) {

		return posts.updated_at.between(start, end);

	}

	// 날짜가 String으로 들어오기 때문에 TimeStamp 값으로 바꿔줘야함
	private static Timestamp stringToTimeStamp(String point, String dateString) {
		//  String to Timestamp
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setLenient(false);// 날짜와 시간을 엄격하게 확인
		if(dateString != null) {
			try {
				Date stringToDate = dateFormat.parse(dateString);
				Timestamp stringToTimestamp = new Timestamp(stringToDate.getTime());
				System.out.println(stringToTimestamp);
				return stringToTimestamp;
			} catch (ParseException e) {
				e.printStackTrace();
				// 시작점이 지정 안되었으면 첫번째 게시물부터 출력해야함
				if (point.equals("start"))
					return Timestamp.valueOf("1900-01-01 00:00:00.000");

					// 끝 점이 지정 안되었으면 지금까지 게시된 게시물까지 출력해야함
				else
					return new Timestamp(System.currentTimeMillis());
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
