package com.branch.server.data.community

import com.branch.server.data.entity.community.Community
import com.branch.server.data.entity.community.CommunityRepository
import com.branch.server.data.entity.reservation.GardenReservation
import com.branch.server.error.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class CommunityRepositoryTest {
    @Autowired
    private lateinit var communityRepository: CommunityRepository

    private val gardenReservation: GardenReservation = GardenReservation(
        reservationStartTime = System.currentTimeMillis(),
        reservationSpace = "A"
    )

    private val mockCommunity: Community = Community(
        contentTitle = "Class Test",
        contentAuthor = "KangDroid",
        innerContent = "We are~",
        contentNeeds = "Pencils",
        contentDeadline = "2021.06",
        firstMeeting = "2021.somewhen",
        contentRecruitment = 10,
        currentRecruitment = 5,
        isCommunityExpired = false,
        gardenReservation = gardenReservation
    )

    @BeforeEach
    @AfterEach
    fun destroy() {
        communityRepository.deleteAll()
    }

    @Test
    fun is_saving_works_well() {
        val savedCommunity: Community = communityRepository.save(mockCommunity)
        val findResult: Community = communityRepository.findById(savedCommunity.id)
        val totalList: List<Community> = communityRepository.findAll()

        assertThat(findResult.contentTitle).isEqualTo(mockCommunity.contentTitle)
        assertThat(totalList.isEmpty()).isEqualTo(false)
        assertThat(totalList.size).isEqualTo(1)
    }

    @Test
    fun is_findById_throw_404() {
        runCatching {
            communityRepository.findById(10)
        }.onSuccess {
            fail("We do not have any data in DB, but it succeed?")
        }.onFailure {
            assertThat(it is NotFoundException).isEqualTo(true)
        }
    }
}