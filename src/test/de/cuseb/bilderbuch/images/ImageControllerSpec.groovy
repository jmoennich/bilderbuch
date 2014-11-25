package de.cuseb.bilderbuch.images

import de.cuseb.bilderbuch.sentence.SentenceService
import spock.lang.Specification

class ImageControllerSpec extends Specification {

    ImageController controller = new ImageController()

    def "Sentence"() {

        given:
        def response = new ImageResponse()
        controller.sentenceService = Mock(SentenceService)
        controller.imageSearchService = Mock(ImageSearchService)

        when:
        def result = controller.sentence("This is a test")

        then:
        controller.sentenceService.getQueryForSentence("This is a test") >> "test"
        controller.imageSearchService.searchImages("test") >> response

        and:
        result == response
    }

}
