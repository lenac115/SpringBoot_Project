package com.springProject.repository;

import static com.springProject.entity.QPosts.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.Query;
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

	@Override
	public Page<Posts> searchPosts(SearchData searchData, String sortBy, Pageable pageable) {
		String category = searchData.getCategory();
		String location = searchData.getLocation();
		int star = searchData.getStar();
		String hashtag = searchData.getHashtag();
		String startDate = searchData.getStartDate();
		String endDate = searchData.getEndDate();

		Timestamp start = stringToTimeStamp("start", startDate);
		Timestamp end = stringToTimeStamp("end", endDate);

		OrderSpecifier order = switch (sortBy) {
			case "oldPost" -> posts.created_at.asc();
			case "bigStar" -> posts.star.desc();
			case "smallStar" -> posts.star.asc();
			default -> posts.created_at.desc();
		};

		JPAQuery<Posts> query = queryFactory
			.selectFrom(posts)
			.where(posts.star.goe(star)
				.or(eqCategory(category))
				.or(eqLocation(location))
				.or(containsHashtag(hashtag))
				.or(betweenDate(start, end)))
			.orderBy(order)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());
		return new PageImpl<>(query.fetch());
	}

	private BooleanExpression eqCategory(String category) {
		if( category != null || !category.equals("null"))

			return Expressions.stringTemplate(
					"LOWER({0})", posts.category)
				.eq(Expressions.stringTemplate(
					"LOWER({0})",category));
		return posts.category.eq(posts.category);
	}

	private BooleanExpression eqLocation(String location) {
		if(location != null || !location.equals("null"))
			return Expressions.stringTemplate(
					"LOWER({0})", posts.location)
				.eq(Expressions.stringTemplate(
					"LOWER({0})",location));
		return posts.location.eq(posts.location);
	}

	private BooleanExpression containsHashtag(String hashtag) {
		if(hashtag != null || !hashtag.equals("null"))
			return Expressions.stringTemplate(
					"LOWER({0})", posts.hashtags)
				.contains(Expressions.stringTemplate(
					"LOWER({0})",hashtag));
		return posts.hashtags.contains(hashtag);
	}

	private BooleanExpression betweenDate(Timestamp start, Timestamp end) {

		return posts.updated_at.between(start, end);

	}

	private static Timestamp stringToTimeStamp(String keyword, String dateString) {
		//  String to Timestamp
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setLenient(false);// 날짜와 시간을 엄격하게 확인
		if(dateString != null && !dateString.equals("null")) {
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
		if (keyword.equals("start"))
			return Timestamp.valueOf("1900-01-01 00:00:00.000");
			// 끝 점이 지정 안되었으면 지금까지 게시된 게시물까지 출력해야함
		else
			return new Timestamp(System.currentTimeMillis());

	}
}
