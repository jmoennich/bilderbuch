package de.cuseb.bilderbuch.images

import com.google.api.services.customsearch.Customsearch
import spock.lang.Specification

class GoogleImageSearchSpec extends Specification {

    GoogleImageSearch service

    def setup() {
        service = new GoogleImageSearch()

    }

    def "SearchImages"() {

        setup:
        def customsearch = Mock(Customsearch)
        service.metaClass.getCustomsearch = { -> customsearch }

        when:
        def result = service.searchImages('query')

        then:
        result.size() == 1
    }

}
