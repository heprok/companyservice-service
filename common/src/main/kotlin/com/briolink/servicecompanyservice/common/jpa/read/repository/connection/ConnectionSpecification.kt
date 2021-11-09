package com.briolink.servicecompanyservice.common.jpa.read.repository.connection

import com.briolink.servicecompanyservice.common.jpa.matchBoolMode
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity_
import org.springframework.data.jpa.domain.Specification
import java.time.Year
import java.util.*

fun equalsServiceId(serviceId: UUID): Specification<ConnectionReadEntity> {
    return Specification<ConnectionReadEntity> { root, _, builder ->
        builder.equal(root.get(ConnectionReadEntity_.serviceId), serviceId)
    }
}

fun betweenDateCollab(start: Year?, end: Year?): Specification<ConnectionReadEntity>? {

    return if (start == null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), end)
        }
    } else if (start != null && end == null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.startCollaboration), start)
        }.and { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), start)
        }
    } else if (start != null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.startCollaboration), start)
        }.and { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), end)
        }
    } else {
        null
    }
}


fun inVerificationStage(status: List<ConnectionReadEntity.ConnectionStatus>?): Specification<ConnectionReadEntity>? {
    return if (status != null && status.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.verificationStage).`in`(status))
        }
    } else {
        null
    }
}

fun inIndustryIds(industryIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (industryIds != null && industryIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.industryId).`in`(industryIds))
        }
    } else {
        null
    }
}


fun fullTextSearchByLocation(location: String?): Specification<ConnectionReadEntity>? {
    return if (location != null) Specification { root, _, criteriaBuilder ->
        criteriaBuilder.like(
                root.get(ConnectionReadEntity_.location),
                "%$location%",
        )
    } else {
        null
    }
}

fun inBuyerRoleIds(roleIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (roleIds != null && roleIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.buyerRoleId).`in`(roleIds))
        }
    } else {
        null
    }
}

fun inBuyerIds(companyIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (companyIds != null && companyIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.buyerId).`in`(companyIds))
        }
    } else {
        null
    }
}
