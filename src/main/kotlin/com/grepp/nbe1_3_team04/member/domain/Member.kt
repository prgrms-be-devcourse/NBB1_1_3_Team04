package com.grepp.nbe1_3_team04.member.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.springframework.security.crypto.password.PasswordEncoder

@SQLDelete(sql = "UPDATE member SET is_deleted = 'TRUE' WHERE member_id = ?")
@Entity
class Member private constructor(
    email: String,
    password: String?,
    name: String,
    phoneNumber: String,
    loginType: LoginType,
    gender: Gender,
    memberRole: MemberRole,
    termsAgreed: TermsAgreed
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long? = null

    @Column(nullable = false, unique = true)
    val email: String = email

    var password: String? = password
        protected set

    @Column(nullable = false)
    var name: String = name
        protected set

    @Column(nullable = false)
    var phoneNumber: String = phoneNumber
        protected set

    @Embedded
    val loginType: LoginType = loginType

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gender: Gender = gender
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val memberRole: MemberRole = memberRole

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val termsAgreed: TermsAgreed = termsAgreed


    fun update(name: String?, phoneNumber: String?, gender: Gender?) {
        name?.let { this.name = it }
        gender?.let { this.gender = it }
        phoneNumber?.let { this.phoneNumber = it }
    }

    fun encodePassword(passwordEncoder: PasswordEncoder) {
        this.password = passwordEncoder.encode(password)
    }

    fun changePassword(password: String?) {
        this.password = password
    }

    companion object {
        fun create(
            email: String,
            password: String?,
            name: String,
            phoneNumber: String,
            loginProvider: LoginProvider,
            snsId: String?,
            gender: Gender,
            memberRole: MemberRole,
            termsAgreed: TermsAgreed
        ): Member {
            return Member(
                email,
                password,
                name,
                phoneNumber,
                LoginType(loginProvider, snsId),
                gender,
                memberRole,
                termsAgreed
            )
        }

    }
}
