package com.crawl.backend.query;

import com.google.gson.annotations.SerializedName;

public enum QueryStatusEnum {
    @SerializedName("uninitialized")
    UNINITIALIZED,
    @SerializedName("active")
    ACTIVE,
    @SerializedName("done")
    DONE,
}
