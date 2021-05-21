package com.paymentez.android.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.paymentez.android.Paymentez;
import com.paymentez.android.model.Card;
import com.paymentez.android.rest.PaymentezService;
import com.paymentez.android.rest.model.CardBinResponse;
import com.paymentez.android.util.CardUtils;
import com.paymentez.android.util.PaymentezTextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An {@link EditText} that handles spacing out the digits of a credit card.
 */
public class CardNumberEditText extends PaymentezEditText {

    private static final int MAX_LENGTH_COMMON = 19;
    // Note that AmEx and Diners Club have the same length
    // because Diners Club has one more space, but one less digit.
    private static final int MAX_LENGTH_AMEX_DINERS = 17;

    private static final Integer[] SPACES_ARRAY_COMMON = {4, 9, 14};
    private static final Set<Integer> SPACE_SET_COMMON =
            new HashSet<>(Arrays.asList(SPACES_ARRAY_COMMON));

    private static final Integer[] SPACES_ARRAY_AMEX = {4, 11};
    private static final Set<Integer> SPACE_SET_AMEX =
            new HashSet<>(Arrays.asList(SPACES_ARRAY_AMEX));

    @VisibleForTesting
    String mCardBrand = Card.UNKNOWN;
    String mCardLogo = Card.UNKNOWN;
    boolean mIsOtp = false;
    private CardBrandChangeListener mCardBrandChangeListener;
    private CardNumberCompleteListener mCardNumberCompleteListener;
    private int mLengthMax = 19;
    private boolean mIgnoreChanges = false;
    private boolean mIsCardNumberValid = false;
    Context mContext;

    public CardNumberEditText(Context context) {
        super(context);
        this.mContext = context;
        listenForTextChanges();
    }

    public CardNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        listenForTextChanges();
    }

    public CardNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        listenForTextChanges();
    }

    @NonNull
    public String getCardBrand() {
        return mCardBrand;
    }

    /**
     * Gets a usable form of the card number. If the text is "4242 4242 4242 4242", this
     * method will return "4242424242424242". If the card number is invalid, this returns
     * {@code null}.
     *
     * @return a space-free version of the card number, or {@code null} if the number is invalid
     */
    @Nullable
    public String getCardNumber() {
        return mIsCardNumberValid
                ? PaymentezTextUtils.removeSpacesAndHyphens(getText().toString())
                : null;
    }

    public int getLengthMax() {
        return mLengthMax;
    }

    /**
     * Check whether or not the card number is valid
     *
     * @return the value of {@link #mIsCardNumberValid}
     */
    public boolean isCardNumberValid() {
        return mIsCardNumberValid;
    }

    void setCardNumberCompleteListener(@NonNull CardNumberCompleteListener listener) {
        mCardNumberCompleteListener = listener;
    }

    void setCardBrandChangeListener(@NonNull CardBrandChangeListener listener) {
        mCardBrandChangeListener = listener;
        // Immediately display the brand if known, in case this method is invoked when
        // partial data already exists.
        mCardBrandChangeListener.onCardBrandChanged(mCardBrand, mCardLogo, mIsOtp);
    }

    void updateLengthFilter() {
        setFilters(new InputFilter[] {new InputFilter.LengthFilter(mLengthMax)});
    }

    /**
     * Updates the selection index based on the current (pre-edit) index, and
     * the size change of the number being input.
     *
     * @param newLength the post-edit length of the string
     * @param editActionStart the position in the string at which the edit action starts
     * @param editActionAddition the number of new characters going into the string (zero for delete)
     * @return an index within the string at which to put the cursor
     */
    @VisibleForTesting
    int updateSelectionIndex(
            int newLength,
            int editActionStart,
            int editActionAddition) {
        int newPosition, gapsJumped = 0;
        Set<Integer> gapSet = Card.AMERICAN_EXPRESS.equals(mCardBrand)
                ? SPACE_SET_AMEX
                : SPACE_SET_COMMON;
        boolean skipBack = false;
        for (Integer gap : gapSet) {
            if (editActionStart <= gap && editActionStart + editActionAddition > gap) {
                gapsJumped++;
            }

            // editActionAddition can only be 0 if we are deleting,
            // so we need to check whether or not to skip backwards one space
            if (editActionAddition == 0 && editActionStart == gap + 1) {
                skipBack = true;
            }
        }

        newPosition = editActionStart + editActionAddition + gapsJumped;
        if (skipBack && newPosition > 0) {
            newPosition--;
        }

        return newPosition <= newLength ? newPosition : newLength;
    }

    private void listenForTextChanges() {
        addTextChangedListener(new TextWatcher() {
            int latestChangeStart;
            int latestInsertionSize;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mIgnoreChanges) {
                    latestChangeStart = start;
                    latestInsertionSize = after;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIgnoreChanges) {
                    return;
                }

                if(start < 6){
                    updateCardBrand(Card.UNKNOWN, Card.UNKNOWN, false);
                }

                if (start >= 6 && start <= 10) {
                    updateCardBrandFromNumber(s.toString());
                }

                if (start > 16) {
                    // no need to do formatting if we're past all of the spaces.
                    return;
                }

                String spacelessNumber = PaymentezTextUtils.removeSpacesAndHyphens(s.toString());
                if (spacelessNumber == null) {
                    return;
                }

                String[] cardParts = ViewUtils.separateCardNumberGroups(
                        spacelessNumber, mCardBrand);
                StringBuilder formattedNumberBuilder = new StringBuilder();
                for (int i = 0; i < cardParts.length; i++) {
                    if (cardParts[i] == null) {
                        break;
                    }

                    if (i != 0) {
                        formattedNumberBuilder.append(' ');
                    }
                    formattedNumberBuilder.append(cardParts[i]);
                }

                String formattedNumber = formattedNumberBuilder.toString();
                int cursorPosition = updateSelectionIndex(
                        formattedNumber.length(),
                        latestChangeStart,
                        latestInsertionSize);

                mIgnoreChanges = true;
                setText(formattedNumber);
                setSelection(cursorPosition);
                mIgnoreChanges = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == mLengthMax) {
                    boolean before = mIsCardNumberValid;
                    mIsCardNumberValid = CardUtils.isValidCardNumber(s.toString());
                    setShouldShowError(!mIsCardNumberValid);
                    if (!before && mIsCardNumberValid && mCardNumberCompleteListener != null) {
                        mCardNumberCompleteListener.onCardNumberComplete();
                    }
                } else {
                    mIsCardNumberValid = getText() != null
                            && CardUtils.isValidCardNumber(getText().toString());
                    // Don't show errors if we aren't full-length.
                    setShouldShowError(false);
                }
            }
        });
    }

    private void updateCardBrand(@NonNull String brand, String cardLogo, boolean isOtp) {
        if (mCardBrand.equals(brand)) {
            return;
        }

        mCardBrand = brand;
        mCardLogo = cardLogo;
        mIsOtp = isOtp;

        if (mCardBrandChangeListener != null) {
            mCardBrandChangeListener.onCardBrandChanged(mCardBrand, cardLogo, isOtp);
        }

        int oldLength = mLengthMax;
        mLengthMax = getLengthForBrand(mCardBrand);
        if (oldLength == mLengthMax) {
            return;
        }

        updateLengthFilter();
    }

    private void updateCardBrandFromNumber(String partialNumber) {
        String spacelessCardNumber = PaymentezTextUtils.removeSpacesAndHyphens(partialNumber);



        if(partialNumber.length() >= 6 && partialNumber.length() <= 10){
            PaymentezService paymentezService = Paymentez.getPaymentezService(mContext);
            paymentezService.cardBin(spacelessCardNumber).enqueue(new Callback<CardBinResponse>() {
                @Override
                public void onResponse(Call<CardBinResponse> call, Response<CardBinResponse> response) {
                    CardBinResponse cardBinResponse = response.body();
                    if(response.isSuccessful()) {
                        if(cardBinResponse.getCardType()!=null && !cardBinResponse.getCardType().equals("")){
                            String cardLogo = cardBinResponse.getUrlLogoPng();
                            CardUtils.setPossibleCardType(cardBinResponse.getCardType());
                            updateCardBrand(cardBinResponse.getCardType(), cardLogo, cardBinResponse.isOtp());
                        }

                    }
                }

                @Override
                public void onFailure(Call<CardBinResponse> call, Throwable e) {

                }
            });
        }



    }

    private static int getLengthForBrand(String cardBrand) {
        if (Card.AMERICAN_EXPRESS.equals(cardBrand) || Card.DINERS_CLUB.equals(cardBrand)) {
            return MAX_LENGTH_AMEX_DINERS;
        } else {
            return MAX_LENGTH_COMMON;
        }
    }

    interface CardNumberCompleteListener {
        void onCardNumberComplete();
    }

    interface CardBrandChangeListener {
        void onCardBrandChanged(@NonNull String brand, String cardLogo, boolean isOtp);
    }
}
