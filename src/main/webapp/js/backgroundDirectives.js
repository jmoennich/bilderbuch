angular.module('backgroundDirectives', []).directive('backgroundCover', [function () {
    return {
        restrict: 'A',
        link: function ($scope, element, attrs) {

            element.css('background-size', 'cover');
            element.css('background-repeat', 'no-repeat');

            // add none displayed image for preload
            var img = angular.element('<img style="display:none;">');
            angular.element(document.body).append(img);

            // evaluate "backgroundCoverLoadEnd" when image has been loaded
            img.on('load', function () {
                element.css('background-image', 'url(' + img.attr('src') + ')');
                $scope.$apply(attrs.backgroundCoverLoadEnd);
            });

            // evaluate "backgroundCoverLoadError" when image could not be loaded
            img.on('error', function () {
                $scope.$apply(attrs.backgroundCoverLoadError);
            });

            // preload image when background-cover attribute changes
            $scope.$watch(attrs.backgroundCover, function (nu) {
                if (nu) {
                    $scope.$eval(attrs.backgroundCoverLoadStart);
                    img.attr('src', nu);
                }
            });
        }
    };
}]);
