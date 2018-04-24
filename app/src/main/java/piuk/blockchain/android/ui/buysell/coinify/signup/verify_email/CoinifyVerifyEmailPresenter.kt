package piuk.blockchain.android.ui.buysell.coinify.signup.verify_email

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import piuk.blockchain.android.util.extensions.addToCompositeDisposable
import piuk.blockchain.androidcore.data.settings.SettingsDataManager
import piuk.blockchain.androidcore.utils.extensions.applySchedulers
import piuk.blockchain.androidcoreui.ui.base.BasePresenter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CoinifyVerifyEmailPresenter @Inject constructor(
        private val settingsDataManager: SettingsDataManager
) : BasePresenter<CoinifyVerifyEmailView>() {

    override fun onViewReady() {

        settingsDataManager.getSettings()
                .applySchedulers()
                .addToCompositeDisposable(this)
                .doOnError { view.onShowErrorAndClose() }
                .subscribe { settings ->
                    if (settings.isEmailVerified) {
                        view.onShowVerifiedEmail(settings.email)
                    } else {
                        view.onShowUnverifiedEmail(settings.email)
                        resendVerificationLink(settings.email)
                    }
                }
    }

    fun resendVerificationLink(emailAddress: String) {
        settingsDataManager.updateEmail(emailAddress)
                .applySchedulers()
                .addToCompositeDisposable(this)
                .doOnError { view.onShowErrorAndClose() }
                .subscribe {
                    pollForEmailVerified()
                }
    }

    fun pollForEmailVerified() {
        Observable.interval(10, TimeUnit.SECONDS, Schedulers.io())
                .flatMap { settingsDataManager.fetchSettings() }
                .applySchedulers()
                .addToCompositeDisposable(this)
                .doOnError { view.onShowErrorAndClose() }
                .doOnNext {
                    if (it.isEmailVerified) {
                        view.onShowVerifiedEmail(it.email)
                    }
                }
                .takeUntil {
                    it.isEmailVerified }
                .subscribe {
                    //no-op
                }
    }
}