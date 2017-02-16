$().ready(function(){

    lyrics = lyrics.replace(/This Lyrics is NOT for Commercial use/g, "")

    var options = {
        workerUrl: workerPath,
        languages: ['english']
    };

    var wordFreq = WordFreqSync(options);

    var wordFreqAnnotations = wordFreq.process(words);
    var wordFreqLyrics = wordFreq.process(lyrics);

    wordFreqAnnotations = wordFreqAnnotations.map(function(item) {
        return {
            text: item[0],
            value: item[1]
        }
    });

    wordFreqLyrics = wordFreqLyrics.map(function(item) {
        return {
            text: item[0],
            value: item[1]
        }
    });

    var my_color = d3.scale.category20();
    var href_func = function(d){  }

    window.makeWordCloud(wordFreqAnnotations.slice(0,80), href_func, "div.cloud1", 700, "my_svg", "Impact", true, my_color)
    window.makeWordCloud(wordFreqLyrics.slice(0,80), href_func, "div.cloud2", 700, "my_svg2", "Impact", true, my_color)
    $(document).attr("title", artistName.charAt(0).toUpperCase() + artistName.slice(1) );
})