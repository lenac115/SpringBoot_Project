
// 현재 URI 파싱
var parsingURI = location.search.split("page")[0];
var parsingURL = window.location.href.split("?")[0];

// 정렬에 연결할 URI 작업
const sorting = document.querySelector("#sorting");

sorting.addEventListener('change', function () {
    var optionId = this.value;

    if(parsingURI.charAt(0) !== "?") { parsingURI = "?"; }
    window.location.href = window.location.href.split("?")[0]
        + parsingURI.split("sort")[0] + "sort="+optionId+"&page=1";
});

// 페이지 이동에 연결할 URI 작업
const pageContainer = document.querySelectorAll('.move-page');

for (let i = 0; i < pageContainer.length; i++) {
    if(parsingURI.charAt(0) !== "?") parsingURI = "?";
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


const imageTags = document.querySelectorAll(".post-image")
let images = [];

var bookmarks = document.querySelectorAll(".bookmark");
var prefers = document.querySelectorAll(".prefer");


document.addEventListener("DOMContentLoaded", function() {

    imageTags.forEach(imageTag => {
        var postId = imageTag.parentNode.id;
        fetch("/api/images/get?postId=" + postId, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                if(data.length > 0) {
                   imageTag.src = '/api/images/display/' + data[0].storeFilename;

                }
            })

    });
    if (user !== null) {
        var postElements = document.querySelectorAll('#post-list > li');

        postElements.forEach(function (postElement) {
            var post = postElement.getAttribute("id");

            fetch("/api/prefers/get?postId=" + post, {
                method: 'GET'
            }).then(response => {
                console.log(response)
                if (response.ok) {
                    var preferImages = postElement.querySelectorAll('.prefer');

                    preferImages.forEach(function (image) {
                        image.classList.toggle("off");
                    });
                }
            });

            fetch("/api/bookmarks/get?postId=" + post, {
                method: 'GET'
            }).then(response => {
                console.log(response)

                if (response.ok) {
                    var bookmarkImages = postElement.querySelectorAll('.bookmark');

                    bookmarkImages.forEach(function (image) {
                        image.classList.toggle("off");
                    });
                }
            });
        });

    }
})

var bookmarkUrl = "/api/bookmarks/";
var preferUrl = "/api/prefers/";

// 북마크 추가 및 취소
bookmarks.forEach(bookmark => {
    bookmark.addEventListener("click", function () {
            if (user !== null) {
                var post = this.parentNode.parentNode.getAttribute("id");

                var offBookmark = this.parentNode.querySelectorAll('.bookmark');
                var deleteBookmark = this.parentNode.querySelector('.bookmark .delete');

                if (this === deleteBookmark) {
                    fetch(bookmarkUrl + "delete?postId=" + post, {
                        method: "DELETE"
                    }).then(res => {
                        console.log(res);

                        if (res.ok) {
                            offBookmark.forEach(bookmark => {
                                bookmark.classList.toggle('off');
                            });

                        } else {
                            console.log(res);
                        }
                    })
                } else {
                    fetch(bookmarkUrl + "save?postId=" + post, {
                        method: "POST"
                    }).then(res => {
                        console.log(res);

                        if (res.ok) {
                            offBookmark.forEach(bookmark => {
                                bookmark.classList.toggle('off');
                            })

                        } else {
                            console.log(res);
                        }
                    })
                }
            } else {
                window.location.href = "/api/users/login";
            }
        }
    )
})

// 좋아요 추가 및 취소
prefers.forEach(prefer => {
    prefer.addEventListener("click", function (e) {
        if (user !== null) {
            var post = this.parentNode.parentNode.getAttribute("id");
            var offPrefer = this.parentNode.querySelectorAll('.prefer');
            var deletePrefer = this.parentNode.querySelector('.prefer.delete');

            if (this === deletePrefer) {
                fetch(preferUrl + "delete?postId=" + post, {
                    method: "DELETE"
                }).then(res => {
                    console.log(res);
                    if (res.ok) {
                        offPrefer.forEach(prefer => {
                            prefer.classList.toggle('off');
                        })

                    } else {
                        console.log(res);
                    }
                })
            } else {
                fetch(preferUrl + "save?postId=" + post, {
                    method: "POST"
                }).then(res => {
                    console.log(res);

                    if (res.ok) {
                        offPrefer.forEach(prefer => {
                            prefer.classList.toggle('off');
                        })
                    } else {
                        console.log(res);
                    }
                })
            }

        } else {
            window.location.href = "/api/users/login";
        }
    })
})






