package com.fisight.location

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Location(@Id val id: Int, val name: String, val entityName: String)
