angular.module('bilderbuchControllers', []).controller('bilderbuchController', ['$scope', '$http', '$log', '$window', '$document', 'speech', function ($scope, $http, $log, $window, $document, speech) {

    $scope.slides = [];

    $scope.search = function () {

        speech.recordSentence().then(function (sentence) {

            $log.debug(sentence);
            $scope.loading = true;

            $http.get('/images?sentence=' + sentence)
                .success(function (data) {
                    var slide = {
                        imageIndex: 0,
                        sentence: sentence
                    };
                    angular.extend(slide, data);
                    $scope.loading = false;
                    $scope.slides.push(slide);
                    $scope.selectedSlide = slide;
                    $scope.selectedSlideIndex = $scope.slides.length - 1;
                });

        }, function (event) {
            $log.error(event);
        });
    };

    $scope.downloadPdf = function () {

        var pages = $scope.slides.map(function (slide) {
            return {
                sentence: slide.sentence,
                image: slide.images[slide.imageIndex]
            }
        });

        $http.post('/pdf', {pages: pages}).then(function () {
            $window.location.href = '/pdf';
        });
    };

    $scope.nextImage = function () {
        var slide = $scope.selectedSlide;
        if (slide.imageIndex < (slide.images.length - 1)) {
            slide.imageIndex++;
        }
    };

    $scope.prevImage = function () {
        var slide = $scope.selectedSlide;
        if (slide.imageIndex > 0) {
            slide.imageIndex--;
        }
    };

    $scope.slideImageUrl = function (slide) {
        if (slide && slide.images && slide.images.length) {
            return slide.images[slide.imageIndex].url;
        }
        return null;
    };

    $scope.selectSlide = function (index) {
        if (index >= 0 && index < $scope.slides.length) {
            $scope.selectedSlideIndex = index;
            $scope.selectedSlide = $scope.slides[index];
        }
    }

    $document.bind('keydown', function (event) {
        $scope.$apply(function () {
            switch (event.keyCode) {
                case 37:
                    $scope.prevImage();
                    break;
                case 38:
                    $scope.selectSlide($scope.selectedSlideIndex - 1);
                    break;
                case 39:
                    $scope.nextImage();
                    break;
                case 40:
                    $scope.selectSlide($scope.selectedSlideIndex + 1);
                    break;
            }
        });
    });

}]);
