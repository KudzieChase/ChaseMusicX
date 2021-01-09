package com.chase.kudzie.chasemusic.ui.queue

import androidx.lifecycle.ViewModel
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueueViewModel @Inject constructor(private val preferences: PreferencesRepository) :
    ViewModel() {

    val currentQueuePositionFlow: Flow<Int> = preferences.observeCurrentQueuePosition()

    val currentQueuePosition
        get() = preferences.getCurrentQueuePosition()

}