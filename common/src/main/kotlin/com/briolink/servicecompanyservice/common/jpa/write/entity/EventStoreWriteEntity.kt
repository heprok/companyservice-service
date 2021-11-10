package com.briolink.servicecompanyservice.common.jpa.write.entity

import com.briolink.servicecompanyservice.common.domain.v1_0.Domain
import org.hibernate.annotations.Type
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "event_store", schema = "write")
@Entity
class EventStoreWriteEntity(
    @Type(type = "jsonb")
    @Column(name = "data", nullable = false)
    val data: String,
    @Column(name = "created", nullable = false)
    var created: Instant

) : BaseWriteEntity() {
}
