package org.retriever.dailypet.model.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContributionResponse(
    val contributionDetailList : List<ContributionItem>
)
