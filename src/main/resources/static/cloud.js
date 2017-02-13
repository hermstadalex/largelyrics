$().ready(function(){

    data = text2wordmap(words).sort( function(a,b) {return (a.value < b.value) ? 1 : 0;}).slice(0,100)

    console.log(data);

    var my_color = d3.scale.category20();
    var href_func = function(d){  }

    window.makeWordCloud(data, href_func, "body", 800, "my_svg", "Impact", true, my_color)
    $(document).attr("title", artistName.charAt(0).toUpperCase() + artistName.slice(1) );

})