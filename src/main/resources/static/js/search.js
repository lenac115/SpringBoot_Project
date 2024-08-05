// 현재 URI 파싱
var parsingURI = location.search.split("page")[0];

// 정렬에 연결할 URI 작업
const sorting = document.querySelectorAll("#sorting option");

sorting.forEach(sort => {
    var optionId = sort.getAttribute("id");
    sort.addEventListener("click", function (e) {
        window.location.href = parsingURI + "sort="+optionId+"&page=1";
    })

})

// 페이지 이동에 연결할 URI 작업
const pageContainer = document.querySelectorAll('.move-page');

for (let i = 0; i < pageContainer.length; i++) {
    var moveURI = parsingURI+"page"+pageContainer[i].href.split("page")[1];
    pageContainer[i].href = moveURI;
}

// select에서 선택된 값이 있으면 색상 변경
const selectBoxElements = document.querySelectorAll("select");
for (let i = 0; i < selectBoxElements.length; i++) {
    selectBoxElements[i].childNodes.forEach(child => {
        child.addEventListener("selected", function (event) {
            if(child !== selectBoxElements[i].firstChild) {
                selectBoxElements[i].style.color = "#171A1FFF";
            }
        })
    })
}


// 공지사항 열고 닫기
const arrowUp = document.querySelector("#up");
const arrowDown = document.querySelector("#down");
const noticeList = document.querySelector("#notice-list");

arrowUp.addEventListener("click", () => {
    arrowUp.style.visibility = "hidden";
    arrowDown.style.visibility = "visible";

    noticeList.classList.toggle("notice-list-hidden");
});

arrowDown.addEventListener("click", () => {
    arrowDown.style.visibility = "hidden";
    arrowUp.style.visibility = "visible";

    noticeList.classList.toggle("notice-list-hidden");

});

// checkbox 하나만 선택되게 하기 = radio 버튼 + 선택 취소
const starTags = document.querySelectorAll("#rating li input");

function checkDuplicate(chk) {
    starTags.forEach(starTag => {
        if(starTag !== chk) {starTag.checked = false;}
    })
}