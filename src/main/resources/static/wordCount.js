$().ready(function() {
    var options = {
        workerUrl: workerPath,
        languages: ['english']
    };


    console.log(workerPath);

    var text = "text one two three three three three three three three three three";
    var wordFreq = WordFreqSync(options);

    // Initialize and run process() function
    console.log(wordFreq.process(text));
})