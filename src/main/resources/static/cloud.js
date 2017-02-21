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

    console.log(wordFreqAnnotations[0][1]);

    if(typeof wordFreqLyrics[0] !== 'undefined') {
        console.log(wordFreqLyrics[0][1]);


        if (wordFreqLyrics[0][1] < 100) {
            normalizeFactor = .5;
        }
        else if (wordFreqLyrics[0][1] > 200 && wordFreqLyrics[0][1] < 300) {
            normalizeFactor = 1;
        }
        else if (wordFreqLyrics[0][1] > 200 && wordFreqLyrics[0][1] < 300) {
            normalizeFactor = 2;
        }
        else if (wordFreqLyrics[0][1] > 400 && wordFreqLyrics[0][1] < 450) {
            normalizeFactor = 2.3;
        }
        else if (wordFreqLyrics[0][1] > 450 && wordFreqLyrics[0][1] < 500) {
            normalizeFactor = 4;
        }
        else if (wordFreqLyrics[0][1] > 600) {
            normalizeFactor = 4;
        }

        wordFreqLyrics = wordFreqLyrics.map(function (item) {
            return [item[0], [item[1] / normalizeFactor]];
        });

        var lyricOptions = {
            shape: 'circle',
            list: wordFreqLyrics,
            drawOutOfBound: true,
            minSize: minSize
        }

        WordCloud(document.getElementById('lyric-canvas'), lyricOptions );
    }

    normalizeFactor = 1;

    if (wordFreqAnnotations[0][1] < 50) {
        normalizeFactor = .1;
    }
    else if(wordFreqAnnotations[0][1] > 200 && wordFreqAnnotations[0][1] < 300) {
        normalizeFactor = 1.5;
    }
    else if(wordFreqAnnotations[0][1] > 200 && wordFreqAnnotations[0][1] < 300) {
        normalizeFactor = 3;
    }
    else if(wordFreqAnnotations[0][1] > 400 && wordFreqAnnotations[0][1] < 500) {
        normalizeFactor = 3;
    }
    else if(wordFreqAnnotations[0][1] > 600 ) {
        normalizeFactor = 4;
    }

    wordFreqAnnotations = wordFreqAnnotations.map(function(item) {
        return [item[0], [item[1]/normalizeFactor]];
    });

    var annotationOptions = {
        shape: 'circle',
        list: wordFreqAnnotations.slice(0,200),
        drawOutOfBound: true,
        minSize: minSize
    }

    WordCloud(document.getElementById('annotation-canvas'), annotationOptions );

    $(document).attr("title", artistName.charAt(0).toUpperCase() + artistName.slice(1) );
})