package com.grepp.nbe1_3_team04.member.oauth2

import com.grepp.nbe1_3_team04.member.domain.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2UserDetails(val member: Member?, private val attributes: Map<String, Any>) : UserDetails, OAuth2User {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val collection: MutableCollection<GrantedAuthority> = ArrayList()
        collection.add(GrantedAuthority { member?.memberRole?.text })

        return collection
    }

    override fun getPassword(): String? {
        return member?.password
    }

    override fun getName(): String? {
        return member?.name
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }


    override fun getUsername(): String? {
        return member?.email
    }


}
