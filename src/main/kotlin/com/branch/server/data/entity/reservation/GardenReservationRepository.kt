package com.branch.server.data.entity.reservation

import org.springframework.data.jpa.repository.JpaRepository

interface GardenReservationRepository: JpaRepository<GardenReservation, Long> {
}