package com.dadino.quickstart2.core.components

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import com.dadino.quickstart2.core.entities.OneOffAction
import com.dadino.quickstart2.core.entities.OneOffActionWithValue
import com.dadino.quickstart2.core.entities.Operation
import com.dadino.quickstart2.core.exceptions.SmartLockSaveError
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status

class SmartLockViewModel(application: Application)
	: GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
	  BaseViewModel<SmartLockUiModel, SmartLockEvent>(application) {

	private var mCredentialsClient: GoogleApiClient = GoogleApiClient.Builder(
			application.applicationContext)
			.addConnectionCallbacks(this)
			.addApi(Auth.CREDENTIALS_API)
			.build()
	private var mIsReading: Boolean = false
	private var mIsHinting: Boolean = false
	private var mIsSaving: Boolean = false
	private var clientConnected: Boolean = false
	var autoShowHints: Boolean = false

	init {
		mCredentialsClient.connect()
	}

	fun onShowExistingAccountsClicked(activity: Activity) {
		try {
			model().multipleAccountsStatus!!.startResolutionForResult(activity, RC_READ)
			mIsReading = true
		} catch (e: IntentSender.SendIntentException) {
			e.printStackTrace()
		}

	}

	fun onShowEmailHintsClicked(activity: Activity) {
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

	fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
		when (requestCode) {
			RC_READ -> {
				mIsReading = false
				if (resultCode == Activity.RESULT_OK && data != null) {
					val credential: Credential = data.getParcelableExtra(Credential.EXTRA_KEY)
					pushEvent(SmartLockEvent.LoginWithCredentialsRequested(credential))
				} else {
					Log.d("SLL", "Credentials reading failed")
				}
				return true
			}
			RC_HINT -> {
				mIsHinting = false
				if (resultCode == Activity.RESULT_OK && data != null) {
					val credential: Credential = data.getParcelableExtra(Credential.EXTRA_KEY)
					pushEvent(SmartLockEvent.LoginWithCredentialsRequested(credential))
				} else {
					Log.d("SLL", "Hint reading failed")
				}
				return true
			}
			RC_SAVE -> {
				mIsSaving = false
				if (resultCode == Activity.RESULT_OK) {
					Log.d("SLL", "Credentials saved")
					pushEvent(SmartLockEvent.SaveCredentialCompleted())
				} else {
					Log.d("SLL", "Credentials save canceled by user")
					pushEvent(SmartLockEvent.SaveCredentialCancelled())
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
		pushEvent(SmartLockEvent.SaveCredentialInProgress())
		Auth.CredentialsApi.save(mCredentialsClient, credential)
				.setResultCallback({ result ->
									   val status = result.status
									   if (status.isSuccess) {
										   Log.d("SLL", "Credentials saved")
										   pushEvent(SmartLockEvent.SaveCredentialCompleted())
									   } else {
										   if (status.hasResolution()) {
											   if (mIsSaving) {
												   //TODO
												   Log.d("SLL", "Entered in unknown state")
												   pushEvent(SmartLockEvent.SaveCredentialCompleted())
											   } else
												   try {
													   Log.d("SLL", "Attempting save resolution")
													   status.startResolutionForResult(activity, RC_SAVE)
													   mIsSaving = true
												   } catch (e: IntentSender.SendIntentException) {
													   Log.d("SLL", "Credentials save failed")
													   pushEvent(SmartLockEvent.SaveCredentialError(SmartLockSaveError()))
												   }

										   } else {
											   Log.d("SLL", "Credentials save failed")
											   pushEvent(SmartLockEvent.SaveCredentialError(SmartLockSaveError()))
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
		pushEvent(SmartLockEvent.GoogleConnectionError())
	}

	override fun onConnectionFailed(connectionResult: ConnectionResult) {
		Log.d("SLL", "OnConnection failed: " + connectionResult.errorMessage)
		clientConnected = false
		pushEvent(SmartLockEvent.GoogleConnectionError())
	}

	private fun onCredentialRetrieved(credential: Credential) {
		pushEvent(SmartLockEvent.ExistingAccountChanged(credential))
	}

	private fun resolveResult(status: Status) {
		if (mIsReading) return
		if (status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
			pushEvent(SmartLockEvent.MultipleAccountsStatusChanged(status))
		} else {
			// The user must create an account or sign in manually.
			Log.d("SLL", "No saved credentials")
			if (autoShowHints) pushEvent(SmartLockEvent.ShowEmailHintsRequested())
		}
	}

	override fun initialModel(): SmartLockUiModel {
		return SmartLockUiModel()
	}

	override fun reduce(previous: SmartLockUiModel, event: SmartLockEvent): SmartLockUiModel {
		return SmartLockUiModel(
				existingAccount = when (event) {
					is SmartLockEvent.ExistingAccountChanged -> event.existingAccount
					else                                     -> previous.existingAccount
				},
				multipleAccountsStatus = when (event) {
					is SmartLockEvent.MultipleAccountsStatusChanged -> event.multipleAccountsStatus
					else                                            -> previous.multipleAccountsStatus
				},
				saveCredentialOperation = when (event) {
					is SmartLockEvent.SaveCredentialCompleted  -> Operation.done()
					is SmartLockEvent.SaveCredentialCancelled  -> Operation.idle()
					is SmartLockEvent.SaveCredentialInProgress -> Operation.inProgress()
					is SmartLockEvent.SaveCredentialError      -> Operation.error(event.error)
					else                                       -> previous.saveCredentialOperation
				},
				loginWithCredentialAction = when (event) {
					is SmartLockEvent.LoginWithCredentialsRequested -> OneOffActionWithValue(event.credential)
					else                                            -> previous.loginWithCredentialAction
				},
				saveCredentialCompletedAction = when (event) {
					is SmartLockEvent.SaveCredentialCompleted -> OneOffAction()
					is SmartLockEvent.SaveCredentialCancelled -> OneOffAction()
					else                                      -> previous.saveCredentialCompletedAction
				},
				showEmailHintsAction = when (event) {
					is SmartLockEvent.ShowEmailHintsRequested -> OneOffAction()
					else                                      -> previous.showEmailHintsAction
				}
							   )
	}

	override fun onCleared() {
		mCredentialsClient.let {
			if (it.isConnected || it.isConnecting) it.disconnect()
		}
	}

	companion object {

		private val RC_READ = 1
		private val RC_HINT = 2
		private val RC_SAVE = 3
	}
}

data class SmartLockUiModel(val existingAccount: Credential? = null,
							val multipleAccountsStatus: Status? = null,
							val saveCredentialOperation: Operation = Operation.idle(),
							val loginWithCredentialAction: OneOffActionWithValue<Credential>? = null,
							val saveCredentialCompletedAction: OneOffAction? = null,
							val showEmailHintsAction: OneOffAction? = null)

sealed class SmartLockEvent {
	class LoginWithCredentialsRequested(val credential: Credential) : SmartLockEvent()
	class ShowEmailHintsRequested : SmartLockEvent()
	class ExistingAccountChanged(val existingAccount: Credential) : SmartLockEvent()
	class MultipleAccountsStatusChanged(val multipleAccountsStatus: Status) : SmartLockEvent()
	class SaveCredentialCompleted : SmartLockEvent()
	class SaveCredentialCancelled : SmartLockEvent()
	class SaveCredentialInProgress : SmartLockEvent()
	class SaveCredentialError(val error: Throwable) : SmartLockEvent()
	class GoogleConnectionError : SmartLockEvent()
}