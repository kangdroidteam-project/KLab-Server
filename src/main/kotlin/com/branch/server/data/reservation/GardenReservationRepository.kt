package com.branch.server.data.reservation

import org.springframework.data.jpa.repository.JpaRepository

interface GardenReservationRepository: JpaRepository<GardenReservation, Long> {
}