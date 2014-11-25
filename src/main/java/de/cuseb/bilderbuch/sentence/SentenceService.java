package de.cuseb.bilderbuch.sentence;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.apache.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "sentence")
public class SentenceService {

    private HashSet<String> tags;
    private POSTaggerME postagger;
    private Logger log = Logger.getLogger(SentenceService.class);

    public void setTags(String tags) {
        if (tags != null) {
            this.tags = new HashSet(Arrays.asList(tags.split(",")));
        }
    }

    @PostConstruct
    public void init() {
        try {
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream("de-pos-maxent.bin");
            POSModel model = new POSModel(resource);
            postagger = new POSTaggerME(model);
        } catch (IOException e) {
            log.error("init", e);
        }
    }

    /**
     * Returns the query for a sentence by configured tags
     *
     * @param sentence i.e. "Ich ging in den dunklen Wald"
     * @return i.e. "dunklen Wald"
     */
    public String getQueryForSentence(String sentence) {

        log.debug("getQueryForSentence in: " + sentence);
        if (sentence == null) {
            return null;
        }

        String words[] = sentence.split(" ");
        String wordsTags[] = postagger.tag(words);
        LinkedHashSet<String> wordsResult = new LinkedHashSet<String>();

        for (int i = 0; i < wordsTags.length; i++) {
            if (tags.contains(wordsTags[i])) {
                wordsResult.add(words[i]);
            }
        }

        String result = Joiner.on(" ").join(wordsResult);
        log.debug("getQueryForSentence result: " + result);
        return result;
    }

}
