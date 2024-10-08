package com.dar_hav_projects.messenger.domens.models

enum class IsSignedEnum(
    val status: String
) {
    IsNotSigned("is_not_signed"),
    DoNotHaveNickname("do_not_have_nickname"),
    DoNotVerifyEmail("email"),
    IsSigned("is_signed")
}