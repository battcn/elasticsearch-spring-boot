package com.test;

import com.google.common.collect.Lists;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Test;

import java.io.IOException;

public class PluginTest {

    @Test
    public void test1() throws IOException {
        IndexRequest indexRequest = new IndexRequest();
        XContentBuilder builder = JsonXContent.contentBuilder()
                .startObject()
                .startObject("mappings")
                    .startObject("properties")
                        .startObject("title")
                            .field("type", "text")
                            .field("analyzer", "ik_pinyin_analyzer")
                        .endObject()
                    .startObject("content")
                        .field("type", "text")
                        .field("index", "analyzed")
                        .field("analyzer", "ik_max_word")
                    .endObject()
                    .startObject("desc").field("type", "keyword").endObject()
                    .endObject()
                .endObject()
                .startObject("aliases").endObject()
                .startObject("settings")
                .startObject("analysis")
                    .startObject("analyzer")
                        .startObject("ik_pinyin_analyzer")
                            .field("type", "text")
                            .field("tokenizer", "ik_max_word")
                            .field("filter", Lists.newArrayList("ik_pinyin", "word_delimiter"))
                        .endObject()
                    .endObject()
                .endObject()
                .startObject("filter").startObject("ik_pinyin").field("type", "pinyin").endObject().endObject()
                .endObject()
                .endObject();
        indexRequest.source(builder);
        String source = indexRequest.source().utf8ToString();
        System.out.println(source);
    }


}
