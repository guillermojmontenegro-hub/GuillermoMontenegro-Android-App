package com.example.cvguillermomontenegro.ui.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val ARTICLES = "articles"
    const val ARTICLE_DETAIL = "articleDetail/{slug}"
    const val USERS = "users"
    const val USER_FORM = "userForm"
    const val USER_FORM_WITH_ID = "userForm/{userId}"

    fun articleDetail(slug: String): String = "articleDetail/$slug"

    fun userForm(userId: Long? = null): String = if (userId == null) {
        USER_FORM
    } else {
        "userForm/$userId"
    }
}
