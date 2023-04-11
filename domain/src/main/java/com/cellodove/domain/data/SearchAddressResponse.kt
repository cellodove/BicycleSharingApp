package com.cellodove.domain.data

data class SearchAddressResponse(
    val status : String,
    val errorMessage : String,
    val meta : AddressMeta,
    val addresses : List<DomainAddresses>,
    )

data class AddressMeta(
    val totalCount : Int,
    val page : Int,
    val count : Int,
)

data class DomainAddresses(
    val roadAddress : Int,
    val jibunAddress : Int,
    val x : Int,
    val y : Int
)