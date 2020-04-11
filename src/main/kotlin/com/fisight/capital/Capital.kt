package com.fisight.capital

import javax.persistence.Embeddable

//TODO: add currency, wrap in money class
@Embeddable
data class Capital(val amount: Double)
