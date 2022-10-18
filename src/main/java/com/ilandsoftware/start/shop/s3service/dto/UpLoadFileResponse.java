package com.ilandsoftware.start.shop.s3service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class UpLoadFileResponse {
    @JsonProperty("url")
    private final String url;
}
