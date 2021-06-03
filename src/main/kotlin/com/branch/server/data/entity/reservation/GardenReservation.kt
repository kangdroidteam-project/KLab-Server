package com.branch.server.data.entity.reservation

import javax.persistence.*

@Entity
class GardenReservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,
    var reservationStartTime: Long,
    var reservationEndTime: Long,
    var reservationSpace: String
) {
}