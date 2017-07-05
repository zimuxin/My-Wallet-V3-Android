package piuk.blockchain.android.ui.backup.transfer

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.content.LocalBroadcastManager
import android.view.*
import kotlinx.android.synthetic.main.dialog_transfer_funds.*
import piuk.blockchain.android.R
import piuk.blockchain.android.ui.account.SecondPasswordHandler
import piuk.blockchain.android.ui.balance.BalanceFragment
import piuk.blockchain.android.ui.base.BaseDialogFragment
import piuk.blockchain.android.ui.customviews.MaterialProgressDialog
import piuk.blockchain.android.ui.customviews.ToastCustom
import piuk.blockchain.android.ui.send.AddressAdapter
import piuk.blockchain.android.util.extensions.gone
import piuk.blockchain.android.util.extensions.toast
import piuk.blockchain.android.util.helperfunctions.OnItemSelectedListener
import uk.co.chrisjenx.calligraphy.CalligraphyUtils
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class ConfirmFundsTransferDialogFragment : BaseDialogFragment<ConfirmFundsTransferView, ConfirmFundsTransferPresenter>(),
        ConfirmFundsTransferView {

    private var progressDialog: MaterialProgressDialog? = null

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = inflater?.inflate(R.layout.dialog_transfer_funds, container, false)?.apply {
        isFocusableInTouchMode = true
        requestFocus()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog.window
        window?.let {
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
        }
        dialog.setCancelable(true)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.title = CalligraphyUtils.applyTypefaceSpan(
                getString(R.string.transfer_confirm),
                TypefaceUtils.load(context.assets, "fonts/Montserrat-Regular.ttf")
        )

        val receiveToAdapter = AddressAdapter(
                activity,
                R.layout.spinner_item,
                presenter.getReceiveToList(),
                true
        ).apply { setDropDownViewResource(R.layout.spinner_dropdown) }
        spinner_destination.adapter = receiveToAdapter
        spinner_destination.onItemSelectedListener = OnItemSelectedListener {
            spinner_destination.setSelection(spinner_destination.selectedItemPosition)
            presenter.accountSelected(spinner_destination.selectedItemPosition)
        }

        spinner_destination.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        spinner_destination.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        spinner_destination.dropDownWidth = spinner_destination.width
                    }
                })

        button_transfer_all.setOnClickListener {
            SecondPasswordHandler(activity).validate(object : SecondPasswordHandler.ResultListener {
                override fun onNoSecondPassword() {
                    presenter.sendPayment(null)
                }

                override fun onSecondPasswordValidated(validateSecondPassword: String) {
                    presenter.sendPayment(validateSecondPassword)
                }
            })
        }

        spinner_destination.setSelection(presenter.getDefaultAccount())

        onViewReady()
    }

    override fun showProgressDialog() {
        hideProgressDialog()
        if (activity != null && !activity.isFinishing) {
            progressDialog = MaterialProgressDialog(context).apply {
                setMessage(getString(R.string.please_wait))
                setCancelable(false)
                show()
            }
        }
    }

    override fun hideProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }

    override fun onUiUpdated() {
        loading_layout.gone()
    }

    override fun updateFromLabel(label: String) {
        label_from.text = label
    }

    override fun updateTransferAmountBtc(amount: String) {
        label_transfer_amount_btc.text = amount
    }

    override fun updateTransferAmountFiat(amount: String) {
        label_transfer_amount_fiat.text = amount
    }

    override fun updateFeeAmountBtc(amount: String) {
        label_fee_amount_btc.text = amount
    }

    override fun updateFeeAmountFiat(amount: String) {
        label_fee_amount_fiat.text = amount
    }

    override fun setPaymentButtonEnabled(enabled: Boolean) {
        button_transfer_all.isEnabled = enabled
    }

    override fun getIfArchiveChecked() = checkbox_archive.isChecked

    override fun dismissDialog() {
        val intent = Intent(BalanceFragment.ACTION_INTENT)
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)
        dismiss()
    }

    override fun showToast(@StringRes message: Int, @ToastCustom.ToastType toastType: String) {
        activity?.toast(message, toastType)
    }

    override fun createPresenter() = ConfirmFundsTransferPresenter()

    override fun getMvpView() = this

    companion object {

        const val TAG = "ConfirmFundsTransferDialogFragment"

        @JvmStatic
        fun newInstance(): ConfirmFundsTransferDialogFragment {
            return ConfirmFundsTransferDialogFragment().apply {
                setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullscreenDialog)
            }
        }
    }
}
