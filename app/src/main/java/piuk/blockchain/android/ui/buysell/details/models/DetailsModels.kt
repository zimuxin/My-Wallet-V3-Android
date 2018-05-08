package piuk.blockchain.android.ui.buysell.details.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents complete UI for a buy/sell transaction detail page. All Strings must be formatted
 * correctly, as the page does no real data manipulation and merely renders.
 */
@Parcelize
data class BuySellDetailsModel(
        val isSell: Boolean,
        val pageTitle: String,
        val amountReceived: String,
        val date: String,
        val tradeId: String,
        val currencyReceivedTitle: String,
        val exchangeRate: String,
        val amountSent: String,
        val paymentFee: String,
        val totalCost: String
) : Parcelable

@Parcelize
data class AwaitingFundsModel(
        val formattedAmount: String,
        val reference: String,
        val recipientName: String,
        val recipientAddress: String,
        val iban: String,
        val bic: String,
        val bank: String
) : Parcelable