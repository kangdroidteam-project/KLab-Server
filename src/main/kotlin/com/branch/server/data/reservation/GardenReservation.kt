package com.branch.server.data.reservation

import javax.persistence.*

@Entity
class GardenReservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    var id: Long = -1,
    var reservationStartTime: Long,
    var reservationEndTime: Long,
    var reservationSpace: String
) {
}