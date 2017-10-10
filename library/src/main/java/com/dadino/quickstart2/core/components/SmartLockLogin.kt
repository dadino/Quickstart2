package com.dadino.quickstart2.core.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status

class SmartLockLogin(context: Context,
					 private val callbacks: Callbacks,
					 private val autoShowHints: Boolean = false)
	: GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private var mCredentialsClient: GoogleApiClient = GoogleApiClient.Builder(
			context.applicationContext)
			.addConnectionCallbacks(this)
			.addApi(Auth.CREDENTIALS_API)
			.build()
	private var mIsReading: Boolean = false
	private var mIsHinting: Boolean = false
	private var mIsSaving: Boolean = false
	private var clientConnected: Boolean = false
	private var multipleAccountsStatus: Status? = null
	private var existingAccount: Credential? = null

	init {
		mCredentialsClient.connect()
	}

	fun showMultipleExistingAccounts(activity: Activity) {
		try {
			multipleAccountsStatus!!.startResolutionForResult(activity, RC_READ)
			mIsReading = true
		} catch (e: IntentSender.SendIntentException) {
			e.printStackTrace()
		}

	}

	fun showEmailHints(activity: Activity) {
		if (!clientConnected) return
		if (mIsHinting) return
		val hintRequest = HintRequest.Builder().setHintPickerConfig(
				CredentialPickerConfig.Builder().setShowCancelButton(true)
						.build())
				.setEmailAddressIdentifierSupported(true)
				.setAccountTypes(
						IdentityProviders.GOOGLE,
						IdentityProviders.FACEBOOK,
						IdentityProviders.LINKEDIN,
						IdentityProviders.MICROSOFT,
						IdentityProviders.PAYPAL,
						IdentityProviders.YAHOO,
						IdentityProviders.TWITTER)
				.build()

		val intent = Auth.CredentialsApi.getHintPickerIntent(mCredentialsClient,
															 hintRequest)
		try {
			activity.startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0)
			mIsHinting = true
		} catch (e: IntentSender.SendIntentException) {
			Log.d("SLL", "Could not start hint picker Intent")
			e.printStackTrace()
		}


	}

	fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean {
		when (requestCode) {
			RC_READ -> {
				mIsReading = false
				if (resultCode == Activity.RESULT_OK) {
					existingAccount = data.getParcelableExtra(Credential.EXTRA_KEY)
					loginWithExisting()
				} else {
					Log.d("SLL", "Credentials reading failed")
				}
				return true
			}
			RC_HINT -> {
				mIsHinting = false
				if (resultCode == Activity.RESULT_OK) {
					val credential: Credential = data.getParcelableExtra(Credential.EXTRA_KEY)
					onHintChoosen(credential)
				} else {
					Log.d("SLL", "Hint reading failed")
				}
				return true
			}
			RC_SAVE -> {
				mIsSaving = false
				callbacks.onSaveToSmartLockFinished()
				if (resultCode == Activity.RESULT_OK) {
					Log.d("SLL", "Credentials saved")
				} else {
					Log.d("SLL", "Credentials save canceled by user")
				}
				return true
			}
			else    -> return false
		}

	}

	fun onSuccessfulLogin(activity: Activity, email: String, password: String) {
		if (!clientConnected) return
		val credential = Credential.Builder(email).setPassword(password)
				.build()
		Log.d("SLL", "Saving credentials to SmartLock")
		Auth.CredentialsApi.save(mCredentialsClient, credential)
				.setResultCallback({ result ->
									   val status = result.status
									   if (status.isSuccess) {
										   Log.d("SLL", "Credentials saved")
										   callbacks.onSaveToSmartLockFinished()
									   } else {
										   if (status.hasResolution()) {
											   if (mIsSaving) {
												   callbacks.onSaveToSmartLockFinished()
											   } else
												   try {
													   status.startResolutionForResult(activity, RC_SAVE)
													   mIsSaving = true
												   } catch (e: IntentSender.SendIntentException) {
													   Log.d("SLL", "Credentials save failed")
													   callbacks.onSaveToSmartLockFinished()
												   }

										   } else {
											   Log.d("SLL", "Credentials save failed")
											   callbacks.onSaveToSmartLockFinished()
										   }
									   }
								   })
	}

	fun onUnsuccessfulLogin(email: String, password: String) {
		if (!clientConnected) return
		val credential = Credential.Builder(email).setPassword(password)
				.build()

		Auth.CredentialsApi.delete(mCredentialsClient, credential)
				.setResultCallback({ result ->
									   val status = result.status
									   if (status.isSuccess) {
										   Log.d("SLL", "Credentials deleted")
									   }
								   })
	}

	override fun onConnected(bundle: Bundle?) {
		Log.d("SLL", "OnConnection established")
		clientConnected = true
		val mCredentialRequest = CredentialRequest.Builder().setPasswordLoginSupported(true)
				.setAccountTypes(IdentityProviders.GOOGLE,
								 IdentityProviders.FACEBOOK,
								 IdentityProviders.LINKEDIN,
								 IdentityProviders.MICROSOFT,
								 IdentityProviders.PAYPAL,
								 IdentityProviders.YAHOO,
								 IdentityProviders.TWITTER)
				.build()

		Auth.CredentialsApi.request(mCredentialsClient, mCredentialRequest)
				.setResultCallback({ credentialRequestResult ->
									   if (credentialRequestResult.status
											   .isSuccess) {
										   onCredentialRetrieved(credentialRequestResult.credential)
									   } else {
										   resolveResult(credentialRequestResult.status)
									   }
								   })
	}

	override fun onConnectionSuspended(i: Int) {
		Log.d("SLL", "OnConnection suspended")
		clientConnected = false
	}

	override fun onConnectionFailed(connectionResult: ConnectionResult) {
		Log.d("SLL", "OnConnection failed: " + connectionResult.errorMessage)
		clientConnected = false
	}

	private fun onCredentialRetrieved(credential: Credential) {
		existingAccount = credential
		callbacks.onExistingAccountFound(credential)
	}

	private fun resolveResult(status: Status) {
		if (mIsReading) return
		if (status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
			multipleAccountsStatus = status
			callbacks.onMultipleExistingAccounts()
		} else {
			// The user must create an account or sign in manually.
			Log.d("SLL", "No saved credentials")
			if (autoShowHints) callbacks.onShowEmailHintsRequested()
		}
	}

	private fun onHintChoosen(credential: Credential) {
		callbacks.onHintChoosen(credential.id,
								credential.password)
	}

	private fun loginWithExisting() {
		callbacks.onLoginRequestedWith(existingAccount)
	}

	fun onDestroy() {
		mCredentialsClient.let {
			if (it.isConnected || it.isConnecting) it.disconnect()
		}
	}

	interface Callbacks {

		fun onLoginRequestedWith(credential: Credential?)
		fun onExistingAccountFound(credential: Credential)
		fun onMultipleExistingAccounts()
		fun onHintChoosen(email: String, password: String?)
		fun onSaveToSmartLockFinished()

		fun onShowEmailHintsRequested()
	}

	companion object {

		private val RC_READ = 1
		private val RC_HINT = 2
		private val RC_SAVE = 3
	}
}
