package com.springProject;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pagination {
	private int pageSize = 12; // 한 페이지의 게시글 수
	private int blockSize = 10; // 한 블록당 페이지 수
	private int totalPostCount = 1; // 총 게시글 수
	private int currentPage = 1; // 현재 페이지

	private int startPage = 1; // 한 블럭의 시작 페이지
	private int endPage = 1; // 끝 페이지

	private int totalBlockCount = 1;

	private boolean isPrev = false;
	private boolean isNext = false;

	private int offset = 1;

	public Pagination(int totalPostCount, int currentPage, int pageSize) {
		this.pageSize = pageSize;

		// 홈페이지 목록 개수 구하기
		setTotalBlockCount(totalPostCount, pageSize);

		// 한 블럭의 첫 페이지 구하기
		setStartPage(this.startPage, currentPage, this.blockSize);

		// 한 블럭의 끝 페이지 구하기
		setEndPage(this.startPage, this.blockSize, this.totalBlockCount);

		// 이전 블럭 버튼 유무 판별
		setIsPrev(currentPage, this.blockSize);

		// 다음 블럭 버튼 유무 판별
		setIsNext(this.endPage, this.totalBlockCount);

		// offset 구하기
		setOffset(currentPage, pageSize);

	}

	private void setTotalBlockCount(int totalPostCount, int pageSize) {
		this.totalBlockCount = (int) Math.ceil(totalPostCount * 1.0 / pageSize); // 계산 결과 올림(cf.반올림)
	}

	private void setStartPage(int startPage, int currentPage, int blockSize) {
		this.startPage = startPage + (((currentPage - startPage) * blockSize) * blockSize);
	}

	private void setEndPage(int startPage, int blockSize, int totalBlockCount) {
		this.endPage = (startPage - 1 + blockSize) < totalBlockCount ? (startPage - 1) + blockSize : totalBlockCount;
	}

	private void setIsPrev(int currentPage, int blockSize) {
		this.isPrev = ((currentPage * 1.0) / blockSize) > 1 ? true : false;
	}

	private void setIsNext(int endPage, int totalBlockCount) {
		this.isNext = endPage < totalBlockCount ? true : false;
	}

	private void setOffset(int currentPage, int pageSize) {
		this.offset = (currentPage - 1) * pageSize;
	}

}
