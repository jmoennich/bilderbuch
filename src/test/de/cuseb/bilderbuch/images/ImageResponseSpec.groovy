package de.cuseb.bilderbuch.images

import spock.lang.Specification

class ImageResponseSpec extends Specification {

    def "Shuffle"() {

        given:
        def image1 = new Image(url: 'a')
        def image2 = new Image(url: 'b')
        def image3 = new Image(url: 'c')
        def image4 = new Image(url: 'd')
        def image5 = new Image(url: 'e')
        def response = new ImageResponse()
        response.addImages([image1, image2, image3, image4, image5])

        when:
        response.shuffle()

        then:
        response.images != [image1, image2, image3, image4, image5]
    }

    def "AddImages"() {

        given:
        def image1 = new Image(url: 'a')
        def image2 = new Image(url: 'b')
        def response = new ImageResponse()

        when:
        response.addImages([image1, image2])

        then:
        response.@images == [image1, image2]
    }

    def "GetImages"() {

        given:
        def image1 = new Image(url: 'a')
        def image2 = new Image(url: 'b')
        def response = new ImageResponse()
        response.@images = [image1, image2]

        expect:
        response.getImages() == [image1, image2]
    }
}
