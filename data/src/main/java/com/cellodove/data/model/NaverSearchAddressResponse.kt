package com.cellodove.data.model

import com.google.gson.annotations.SerializedName

data class NaverSearchAddressResponse(
    @SerializedName("status") val status : String,
    @SerializedName("errorMessage") val errorMessage : String,
    @SerializedName("meta") val meta : AddressMeta,
    @SerializedName("addresses") val addresses : List<Addresses>,

    )

data class AddressMeta(
    @SerializedName("totalCount") val totalCount : Int,
    @SerializedName("page") val page : Int,
    @SerializedName("count") val count : Int,
)

data class Addresses(
    @SerializedName("roadAddress") val roadAddress : String,
    @SerializedName("jibunAddress") val jibunAddress : String,
    @SerializedName("x") val x : Double,
    @SerializedName("y") val y : Double
)