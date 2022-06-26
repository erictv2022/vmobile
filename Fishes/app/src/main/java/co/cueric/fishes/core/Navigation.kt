package co.cueric.fishes.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import co.cueric.fishes.R
import co.cueric.fishes.features.authentication.AuthenticationActivity
import co.cueric.fishes.features.authentication.home.HomeActivity

const val EXTRA_NAVIGATION_TYPE_KEY = "EXTRA_NAVIGATION_TYPE_KEY"
const val NAV_TYPE_DEFAULT = 0
const val NAV_TYPE_NONE = 1
const val NAV_TYPE_SLIDE_UP = 2
const val NAV_TYPE_SLIDE_IN = 3

/**
 * Start activity with animation
 *
 * @param fromActivity
 * @param intNavigationType
 */
private fun setStartActivityAnimation(
    fromActivity: Activity,
    intNavigationType: Int
) {
    when (intNavigationType) {
        NAV_TYPE_DEFAULT -> {}
        NAV_TYPE_NONE -> fromActivity.overridePendingTransition(0, 0)
        NAV_TYPE_SLIDE_UP -> fromActivity.overridePendingTransition(R.anim.slide_bottom_up, R.anim.fade_out)
        NAV_TYPE_SLIDE_IN -> fromActivity.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        else -> fromActivity.overridePendingTransition(
            R.anim.slide_bottom_up,
            R.anim.fade_out
        )
    }
}

/**
 * Handy function for start activity
 *
 * @param fromActivity
 * @param intent
 * @param navigationType
 * @param bundle
 */
fun startActivity(
    fromActivity: Activity,
    intent: Intent,
    navigationType: Int,
    bundle: Bundle? = null,
) {
    intent.putExtra(EXTRA_NAVIGATION_TYPE_KEY, navigationType)
    fromActivity.startActivity(intent, bundle)
    setStartActivityAnimation(fromActivity, navigationType)
}

/**
 * Start registration activity
 *
 * @param from
 */
fun startRegister(from: Activity){
    val intent = Intent(from, AuthenticationActivity::class.java)
    startActivity(from, intent, navigationType = NAV_TYPE_SLIDE_UP)
}

/**
 * Start landing view after user sign in
 *
 * @param from
 */
fun startHome(from: Activity){
    val intent = Intent(from, HomeActivity::class.java)
    startActivity(from, intent, navigationType = NAV_TYPE_SLIDE_UP)
}