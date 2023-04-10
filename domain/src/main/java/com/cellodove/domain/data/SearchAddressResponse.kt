package com.cellodove.domain.data

data class SearchAddressResponse(
    val status : String,
    val errorMessage : String,
    val meta : AddressMeta,
    val addresses : Addresses,
    )

data class AddressMeta(
    val totalCount : Int,
    val page : Int,
    val count : Int,
)

data class Addresses(
    val roadAddress : Int,
    val jibunAddress : Int,
    val x : Int,
    val y : Int
)