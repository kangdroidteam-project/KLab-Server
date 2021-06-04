package com.branch.server.data.median

import com.branch.server.data.entity.community.Community
import com.branch.server.data.entity.community.CommunityRepository
import com.branch.server.data.entity.median.MedianTable
import com.branch.server.data.entity.median.MedianTableRepository
import com.branch.server.data.entity.reservation.GardenReservation
import com.branch.server.data.entity.user.User
import com.branch.server.data.entity.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class MedianTableRepositoryTest {
    @Autowired
    private lateinit var medianTableRepository: MedianTableRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var communityRepository: CommunityRepository

    private fun createCommunityObject(reservationSpace: String): Community {
        val secondReservation: GardenReservation = GardenReservation(
            reservationStartTime = System.currentTimeMillis(),
            reservationSpace = reservationSpace
        )

        return Community(
            contentTitle = "Class Test",
            contentAuthor = "KangDroid",
            innerContent = "We are~",
            contentNeeds = "Pencils",
            contentDeadline = "2021.06",
            firstMeeting = "2021.somewhen",
            contentRecruitment = 10,
            currentRecruitment = 5,
            isCommunityExpired = false,
            gardenReservation = secondReservation
        )
    }

    private fun createMockUser(userId: String): User = User(
        userId = userId,
        userPassword = userId,
        userName = userId,
        userAddress = "test",
        userPhoneNumber = "test"
    )

    @BeforeEach
    @AfterEach
    fun destroy() {
        // Must remove Median Table first [reference]
        medianTableRepository.deleteAll()
        userRepository.deleteAll()
        communityRepository.deleteAll()
    }

    @Test
    fun is_findAllByTargetUser_UserId_works_well() {
        // Save References
        val savedUser: User = userRepository.save(createMockUser("kangdroid"))
        val communitySize: Int = 20
        for (i in 0 until communitySize) {
            val communityObject: Community = createCommunityObject(('A'+i).toString())
            val savedCommunity: Community = communityRepository.save(communityObject)

            val tempMedianTable: MedianTable = MedianTable(
                targetUser = savedUser,
                targetCommunity = savedCommunity
            )
            medianTableRepository.save(tempMedianTable)
        }

        // Find
        val tableList: List<MedianTable> = medianTableRepository.findAllByTargetUser_UserId(savedUser.userId)
        assertThat(tableList.isNotEmpty()).isEqualTo(true)
        assertThat(tableList.size).isEqualTo(communitySize)
    }

    @Test
    fun is_findAllByTargetCommunity_Id_works_well() {
        val savedCommunity: Community = communityRepository.save(createCommunityObject("A"))
        val userSize: Int = 20
        for (i in 0 until userSize) {
            val user: User = createMockUser(('a'+i).toString())
            val savedUser: User = userRepository.save(user)
            val tempMedianTable: MedianTable = MedianTable(
                targetUser = savedUser,
                targetCommunity = savedCommunity
            )
            medianTableRepository.save(tempMedianTable)
        }

        // Find
        val tableList: List<MedianTable> = medianTableRepository.findAllByTargetCommunity_Id(savedCommunity.id)
        assertThat(tableList.size).isEqualTo(userSize)
    }
}