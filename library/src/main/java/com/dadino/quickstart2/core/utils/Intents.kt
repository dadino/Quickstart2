package com.dadino.quickstart2.core.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.app.ShareCompat
import com.dadino.quickstart2.core.R

object Intents {

	fun openDialer(context: Context, phoneNumber: String?) {
		if (TextUtils.isEmpty(phoneNumber)) return
		val intent = Intent(Intent.ACTION_DIAL)
		intent.data = Uri.parse("tel:$phoneNumber")
		safeOpen(context, intent)
	}

	fun openWebsite(context: Context, website: String?) {
		if (TextUtils.isEmpty(website)) return
		website?.let {
			val isValidUrl = it.startsWith("http") || it.startsWith("https")
			val url = if (isValidUrl.not()) "http://$it" else it
			val intent = Intent(Intent.ACTION_VIEW)
			intent.data = Uri.parse(url)
			safeOpen(context, intent)
		}
	}

	fun openEmail(context: Context, email: String?) {
		if (TextUtils.isEmpty(email)) return
		email?.let {
			val intent = Intent(Intent.ACTION_SEND)
			intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email))
			intent.type = "message/rfc822"

			safeOpen(context, intent)
		}
	}

	fun shareWebsite(context: Activity, website: String?) {
		if (TextUtils.isEmpty(website)) return
		website?.let {
			val isValidUrl = it.startsWith("http://") || it.startsWith("https://")
			val url = if (isValidUrl.not()) "http://$it" else it

			ShareCompat.IntentBuilder.from(context)
				.setType("text/plain")
				.setChooserTitle(R.string.share_website_label)
				.setText(url)
				.startChooser()
		}
	}

	fun openApp(context: Context, packageName: String) {
		val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
		if (launchIntent != null) {
			context.startActivity(launchIntent)
		} else {
			openWebsite(context, "https://play.google.com/store/apps/details?id=$packageName")
		}
	}

	private fun safeOpen(context: Context, intent: Intent) {
		val packageManager = context.packageManager
		val activities = packageManager.queryIntentActivities(intent, 0)
		val isIntentSafe = activities.size > 0

		if (isIntentSafe) {
			context.startActivity(intent)
		}
	}

	private fun safeChoose(context: Context, intent: Intent, label: String) {
		val chooser = Intent.createChooser(intent, label)

		if (intent.resolveActivity(context.packageManager) != null) {
			context.startActivity(chooser)
		}
	}
}