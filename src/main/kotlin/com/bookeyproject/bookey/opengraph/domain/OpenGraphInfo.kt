package com.bookeyproject.bookey.opengraph.domain

import org.apache.commons.lang3.StringUtils.EMPTY

data class OpenGraphInfo(
    var title: String = EMPTY,
    var description: String = EMPTY,
    var image: String = EMPTY,
    var type: String = EMPTY,
    var siteName: String = EMPTY,
    var creator: String = EMPTY,
    var favicon: String = EMPTY
)
