package com.fisight.fisight.financialasset

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository("financialAssetQueryRepository")
interface FinancialAssetRepository : MongoRepository<FinancialAsset, String>