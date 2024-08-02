var parsingURI = location.search.split("page")[0];

const pageContainer = document.querySelectorAll('.move-page');

for (let i = 0; i < pageContainer.length; i++) {
    var moveURI = parsingURI+"page"+pageContainer[i].href.split("page")[1];
    pageContainer[i].href = moveURI;
    console.log(moveURI);
}