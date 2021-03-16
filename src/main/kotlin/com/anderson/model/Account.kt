package com.anderson.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Account (
    @Id @GeneratedValue
    var id : Long?=null,
    var name : String,
    var document: String,
    var phone : String
)