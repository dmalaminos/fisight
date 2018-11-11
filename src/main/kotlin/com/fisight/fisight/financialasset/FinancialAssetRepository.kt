package com.fisight.fisight.financialasset

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface FinancialAssetRepository : ReactiveMongoRepository<FinancialAsset, String>
