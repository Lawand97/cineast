package org.vitrivr.cineast.api.messages.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.vitrivr.cineast.core.config.QueryConfig;

public class QueryStage {

  public final List<QueryTerm> terms;
  /* may be null */
  public final QueryConfig config;

  @JsonCreator
  public QueryStage(@JsonProperty(value = "terms", required = true) List<QueryTerm> terms, @JsonProperty(value = "config", required = false) QueryConfig config) {
    this.terms = terms;
    this.config = config;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
