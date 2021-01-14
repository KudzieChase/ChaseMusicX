package com.chase.kudzie.chasemusic

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.chase.kudzie.chasemusic.base.BaseMediaActivity
import com.chase.kudzie.chasemusic.databinding.ActivityMainBinding
import com.chase.kudzie.chasemusic.extensions.hide
import com.chase.kudzie.chasemusic.extensions.show
import com.chase.kudzie.chasemusic.media.model.*
import com.chase.kudzie.chasemusic.ui.nowplaying.PlayerMiniFragment
import com.chase.kudzie.chasemusic.util.bindingadapters.contentView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

class MainActivity :
    BaseMediaActivity(),
    HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var fragmentMiniPlayer: PlayerMiniFragment

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    Timber.e("SHEET EXPANDED")
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    Timber.e("SHEET COLLAPSED")
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            hideBottomNavOnDrag(slideOffset)
        }

    }

    private fun hideBottomNavOnDrag(slideOffset: Float) {
        val alpha = 1 - slideOffset

        //TODO Maybe create a listener and do this inside the fragment instead?
        fragmentMiniPlayer.view?.alpha = alpha
        fragmentMiniPlayer.view?.visibility = if (alpha == 0f) View.GONE else View.VISIBLE

        binding.bottomNav.translationY = slideOffset * 500
        binding.bottomNav.alpha = alpha
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if (hasPermissions()) {
            applyBinding()
        }
    }

    private fun applyBinding() {
        binding.apply {
            val navigationController =
                Navigation.findNavController(this@MainActivity, R.id.nav_host)
            bottomNav.setupWithNavController(navigationController)

            lifecycleScope.launchWhenResumed {
                //It crashes without this delay... Maybe it could be reduced to 50ms
                delay(100)
                navigationController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.songs,
                        R.id.playlists,
                        R.id.albums,
                        R.id.artists -> {
                            bottomNav.show()
                            recalculateLayout(true)
                        }
                        else -> {
                            bottomNav.hide()
                            recalculateLayout(false)
                        }
                    }
                }
            }

            //Init Bottom bar
            behavior = BottomSheetBehavior.from(layoutBottomSheet.playerBottomSheet)
            behavior.addBottomSheetCallback(bottomSheetCallback)

            //TODO unsafe call, maybe null
            fragmentMiniPlayer =
                supportFragmentManager.findFragmentById(R.id.player_mini_fragment) as PlayerMiniFragment

            fragmentMiniPlayer.view?.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun recalculateLayout(isShowing: Boolean) {
        binding.layoutBottomSheet.playerBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    behavior.isHideable = false
                    if (isShowing) {
                        behavior.peekHeight = binding.bottomNav.height * 2
                    } else {
                        behavior.peekHeight = binding.bottomNav.height
                    }

                    binding.layoutBottomSheet.playerBottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(
                        this
                    )
                }
            })
    }

    override fun onBackPressed() {
        when (behavior.state) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                super.onBackPressed()
            }
        }
    }

    override fun onHasPermissionsChanged(hasPermissions: Boolean) {
        super.onHasPermissionsChanged(hasPermissions)
        if (hasPermissions) {
            applyBinding()
        }
    }

    override fun getPermissionsArray(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }
}
