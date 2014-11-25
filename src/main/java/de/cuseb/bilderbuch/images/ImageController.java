/**
 * bilderbuch - voice driven image search on the web
 * Copyright (C) 2014  Jan Mönnich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cuseb.bilderbuch.images;

import de.cuseb.bilderbuch.sentence.SentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    @Autowired
    SentenceService sentenceService;

    @Autowired
    ImageSearchService imageSearchService;

    @RequestMapping("/images")
    public ImageResponse sentence(@RequestParam(value = "sentence", required = true) String sentence) {

        String query = sentenceService.getQueryForSentence(sentence);

        return imageSearchService.searchImages(query);
    }

}