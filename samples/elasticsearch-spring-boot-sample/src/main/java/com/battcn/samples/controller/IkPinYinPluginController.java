package com.battcn.samples.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * IK + PinYin 分词器插件
 *
 * @author Levin
 */
@Slf4j
@RestController
@RequestMapping("/plugins")
@AllArgsConstructor
@Api(tags = "2.0.0", description = "插件使用", value = "插件使用")
public class IkPinYinPluginController {


    private final RestHighLevelClient restHighLevelClient;

    @PostMapping("/{index}/map")
    public CreateIndexResponse index1(@PathVariable String index) throws IOException {
        CreateIndexRequest indexRequest = new CreateIndexRequest(index);

        Map<String, Object> settings = Maps.newHashMap();
        Map<String, Object> analysis = Maps.newHashMap();

        Map<String, Object> analyzer = Maps.newHashMap();

        Map<String, Object> ikPinyinAnalyzer = Maps.newHashMap();
        ikPinyinAnalyzer.put("type", "custom");
        ikPinyinAnalyzer.put("tokenizer", "ik_max_word");
        ikPinyinAnalyzer.put("filter", Lists.newArrayList("ik_pinyin", "word_delimiter"));
        analyzer.put("ik_pinyin_analyzer", ikPinyinAnalyzer);

        Map<String, Object> filter = Maps.newHashMap();
        Map<String, String> pinyin = Maps.newHashMap();
        pinyin.put("type", "pinyin");
        filter.put("ik_pinyin", pinyin);

        analysis.put("filter", filter);
        analysis.put("analyzer", analyzer);
        settings.put("analysis", analysis);
        indexRequest.settings(settings);

        Map<String, Object> mapping = Maps.newHashMap();
        Map<String, Object> properties = Maps.newHashMap();
        Map<String, String> id = Maps.newHashMap();
        id.put("type", "integer");
        Map<String, String> username = Maps.newHashMap();
        username.put("type", "text");
        username.put("analyzer", "ik_pinyin_analyzer");
        Map<String, String> description = Maps.newHashMap();
        description.put("type", "text");
        description.put("analyzer", "ik_pinyin_analyzer");
        properties.put("id", id);
        properties.put("username", username);
        properties.put("description", description);
        mapping.put("properties", properties);
        indexRequest.mapping(mapping);
        return restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
    }


    @PostMapping("/{index}/object")
    public CreateIndexResponse index2(@PathVariable String index) throws IOException {
        CreateIndexRequest indexRequest = new CreateIndexRequest(index);
        XContentBuilder settings = JsonXContent.contentBuilder().startObject()
                .startObject("analysis")
                .startObject("analyzer")
                .startObject("ik_pinyin_analyzer")
                .field("type", "custom")
                .field("tokenizer", "ik_max_word")
                .field("filter ", Lists.newArrayList("ik_pinyin", "word_delimiter"))
                .endObject()
                .endObject()
                .startObject("filter").startObject("ik_pinyin").field("type", "pinyin").endObject().endObject()
                .endObject().endObject();
        indexRequest.settings(settings);

        XContentBuilder mapping = JsonXContent.contentBuilder().startObject()
                .startObject("properties")
                .startObject("title").field("type", "text").field("analyzer", "ik_pinyin_analyzer").endObject()
                .startObject("content").field("type", "text").field("analyzer", "ik_pinyin_analyzer").endObject()
                .endObject().endObject();
        indexRequest.mapping(mapping);
        return restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);


    }

}
