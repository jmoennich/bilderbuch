package de.cuseb.bilderbuch.sentence

import spock.lang.Specification

class SentenceServiceSpec extends Specification {

    SentenceService service

    def setup() {
        service = new SentenceService()
        service.init()
    }

    def "GetQueryForSentence"() {

        given:
        service.setTags("NN,ADJA")

        expect:
        service.getQueryForSentence(sentence) == result

        where:
        sentence                       || result
        null                           || null
        ""                             || ""
        "Ich ging in den dunklen Wald" || "dunklen Wald"
    }

}
