package com.microsoft.cas.discovery.event.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class SimpleEvent implements Serializable {

    @JsonProperty("tenant_uid")
    private final String tenantUid;

    @JsonProperty("stream_uid")
    private final String streamUid;

    @JsonProperty("task_uid")
    private final String taskUid;

    @JsonProperty("index")
    private final String index;

    @JsonProperty
    @Singular("datum")
    private final List<String> data;

}
