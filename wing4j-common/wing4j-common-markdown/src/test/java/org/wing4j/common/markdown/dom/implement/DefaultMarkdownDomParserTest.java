package org.wing4j.common.markdown.dom.implement;

import org.junit.Test;
import org.wing4j.common.markdown.dom.MarkdownDocument;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by wing4j on 2017/1/30.
 */
public class DefaultMarkdownDomParserTest {

    @Test
    public void testParse() throws Exception {
        DefaultMarkdownDomParser parser = new DefaultMarkdownDomParser();
        MarkdownDocument document = parser.parse(new File("target/test-classes/example1.md").toURI(), "GBK");
        System.out.println(document);
    }
}