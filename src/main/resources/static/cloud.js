$().ready(function(){

    lyrics = lyrics.replace(/This Lyrics is NOT for Commercial use/g, "")

    cloudAnnotations = text2wordmap(words).sort(function(a,b){return (a.value < b.value) ? 1 : 0;}).slice(0, 100);
    cloudLyrics = text2wordmap(lyrics).sort(function(a,b){return (a.value < b.value) ? 1 : 0;})

    cloudAnnotations = cloudAnnotations.map(function(item) {
        var text = item.text;
        return {
            text: text,
            value: (item.value)
        }
    });

    cloudLyrics = cloudLyrics.map(function(item) {
        var text = item.text;
        return {
            text: text,
            value: (item.value)
        }
    });

    console.log(cloudLyrics);


    var my_color = d3.scale.category20();
    var href_func = function(d){  }

    window.makeWordCloud(cloudAnnotations, href_func, "div.cloud1", 800, "my_svg", "Impact", true, my_color)
    window.makeWordCloud(cloudLyrics, href_func, "div.cloud2", 800, "my_svg2", "Impact", true, my_color)
    $(document).attr("title", artistName.charAt(0).toUpperCase() + artistName.slice(1) );
})