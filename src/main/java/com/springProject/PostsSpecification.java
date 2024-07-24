package com.springProject;

import java.sql.Timestamp;

import org.springframework.data.jpa.domain.Specification;

import com.springProject.entity.Posts;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PostsSpecification {

	// 카테고리 값이 일치하는 데이터 검색
	public static Specification<Posts> equalPostsCategory(String category) {
		return new Specification<Posts>() {
			@Override
			public Predicate toPredicate(Root<Posts> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				System.out.println("category = "+ category);
				return cb.equal(root.get("category"),  category);
			}
		};
	}

	// 위치 값이 일치하는 데이터 검색
	public static Specification<Posts> equalPostsLocation(String location) {
		return new Specification<Posts>() {
			@Override
			public Predicate toPredicate(Root<Posts> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				System.out.println("location = "+ location);
				return cb.equal(root.get("location"), location);
			}
		};
	}

	// 해당 별점 개수 이상인 데이터 검색
	public static Specification<Posts> greaterThanPostsStar(int star) {
		return new Specification<Posts>() {
			@Override
			public Predicate toPredicate(Root<Posts> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				System.out.println("star = "+ star);
				return cb.greaterThan(root.get("star"), star);
			}
		};
	}

	// 해시태그 값이 포함된 데이터 검색
	public static Specification<Posts> likePostsHashtag(String hashtag) {
		return new Specification<Posts>() {
			@Override
			public Predicate toPredicate(Root<Posts> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				System.out.println("hashtag = "+ hashtag);
				return cb.like(root.get("hashtags"),  "%" + hashtag + "%");
			}
		};
	}

	// 일치하는 날짜 사이에 생성된 데이터 검색
	public static Specification<Posts> betweenPostsDate(Timestamp startDate, Timestamp endDate) {
		return new Specification<Posts>() {
			@Override
			public Predicate toPredicate(Root<Posts> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				System.out.println("Date = "+ startDate +" ~ "+ endDate);
				return cb.between(root.get("created_at"), startDate, endDate);
			}
		};
	}

}
