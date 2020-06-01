package com.chase.kudzie.chasemusic.domain.interactor.browse.queue

import com.chase.kudzie.chasemusic.domain.repository.SongQueueRepository
import javax.inject.Inject

/**
 * @author Kudzai Chasinda
 */
class GetQueueSongs @Inject constructor(
    val repository: SongQueueRepository
) {
    suspend operator fun invoke() = repository.getQueueSongs()
}