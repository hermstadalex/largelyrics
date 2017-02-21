$().ready(function(){

    lyrics = lyrics.replace(/This Lyrics is NOT for Commercial use/g, "")
    words = words.replace(/www.youtube.com/g,"")
    words = words.replace(/http/g,"")


    var options = {
        workerUrl: workerPath,
        languages: ['english']
    };

    var wordFreqAno = WordFreqSync(options);
    var wordFreqLyr = WordFreqSync(options);

    var wordFreqAnnotations = wordFreqAno.process(words);
    var wordFreqLyrics = wordFreqLyr.process(lyrics);
    var minSize = 10;
    var normalizeFactor = 1;


    if(typeof wordFreqLyrics[0] !== 'undefined') {

        console.log("Lyric word count: " + totalWordCount(wordFreqLyrics));

        normalizeFactor = normalizeValue(totalWordCount(wordFreqLyrics));

        console.log("Lyric normalize factor: " + normalizeFactor);

        wordFreqLyrics = wordFreqLyrics.map(function (item) {
            return [item[0], [item[1] / normalizeFactor]];
        });

        var lyricOptions = {
            shape: 'circle',
            list: wordFreqLyrics,
            drawOutOfBound: true,
            minSize: minSize,
            backgroundColor: '#F8F8FF'
        }

        WordCloud(document.getElementById('lyric-canvas'), lyricOptions );
    }

    normalizeFactor = 1;

    console.log("Annotation word count: " + totalWordCount(wordFreqAnnotations));

    normalizeFactor = normalizeValue(totalWordCount(wordFreqAnnotations));

    console.log("Annotation normalize factor: " + normalizeFactor);

    wordFreqAnnotations = wordFreqAnnotations.map(function(item) {
        return [item[0], [item[1]/normalizeFactor]];
    });

    var annotationOptions = {
        shape: 'circle',
        list: wordFreqAnnotations.slice(0,200),
        drawOutOfBound: true,
        minSize: minSize,
        backgroundColor: '#F8F8FF'
    }

    WordCloud(document.getElementById('annotation-canvas'), annotationOptions );

    $(document).attr("title", artistName.charAt(0).toUpperCase() + artistName.slice(1) );
})

function totalWordCount(wordArray) {

    shortendedWordArray = wordArray.slice(0,10);

    totalValue = shortendedWordArray.reduce(function(prev, current) {
        return prev + current[1];
    }, 0)

    return totalValue
}

function normalizeValue(wordCount) {

    var normalizeFactor = 1;


    if (wordCount < 100) {
        normalizeFactor = .1;
    }
    else if(wordCount > 100 && wordCount < 200) {
        normalizeFactor = .2;
    }
    else if(wordCount > 200 && wordCount < 300) {
        normalizeFactor = .4;
    }
    else if(wordCount > 300 && wordCount < 400) {
        normalizeFactor = .5;
    }
    else if(wordCount > 400 && wordCount < 500) {
        normalizeFactor = .8;
    }
    else if(wordCount > 500 && wordCount < 800) {
        normalizeFactor = 1;
    }
    else if(wordCount > 800 && wordCount < 1100) {
        normalizeFactor = 1.2;
    }
    else if(wordCount > 1100 && wordCount < 1600) {
        normalizeFactor = 1.5;
    }
    else if(wordCount > 1600 && wordCount < 1800) {
        normalizeFactor = 2;
    }
    else if(wordCount > 1800 && wordCount < 2100 ) {
        normalizeFactor = 2.5;
    }
    else if(wordCount > 2100 && wordCount < 2500 ) {
        normalizeFactor = 2.8;
    }
    else if(wordCount > 2500 && wordCount < 2900) {
        normalizeFactor = 3.3;
    }
    else {
        normalizeFactor = 3.7;
    }

    return normalizeFactor;
}