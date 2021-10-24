package com.briolink.servicecompanyservice.common.jpa.write.entity

import com.briolink.servicecompanyservice.common.domain.v1_0.Domain
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "event_store", catalog = "schema_write")
@Entity
class EventStoreWriteEntity(
    @Column(name = "data", nullable = false)
    val data: String,
    @Column(name = "created", nullable = false)
    var created: Instant

) : BaseWriteEntity() {
}
