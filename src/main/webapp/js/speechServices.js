angular.module('speechServices', []).factory('speech', ['$q', function ($q) {

    return {

        recordSentence: function () {

            var defer = $q.defer();
            var final_transcript = '';
            var recognition = new webkitSpeechRecognition();

            recognition.lang = 'de-DE';
            recognition.continuous = false;
            recognition.interimResults = true;
            recognition.onresult = function (event) {
                var interim_transcript = '';
                for (var i = event.resultIndex; i < event.results.length; ++i) {
                    if (event.results[i].isFinal) {
                        final_transcript += event.results[i][0].transcript;
                        recognition.stop();
                        defer.resolve(final_transcript);
                    } else {
                        interim_transcript += event.results[i][0].transcript;
                    }
                }
            };
            recognition.onerror = function (event) {
                defer.reject(event);
            };
            recognition.start();

            return defer.promise;
        }

    };

}]);
