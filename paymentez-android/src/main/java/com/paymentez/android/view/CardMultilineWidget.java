package com.paymentez.android.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.paymentez.android.R;
import com.paymentez.android.model.Card;
import com.paymentez.android.util.CardUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static com.paymentez.android.model.Card.BRAND_RESOURCE_MAP;
import static com.paymentez.android.view.CardInputListener.FocusField.FOCUS_CARD;
import static com.paymentez.android.view.CardInputListener.FocusField.FOCUS_CARDHOLDERNAME;
import static com.paymentez.android.view.CardInputListener.FocusField.FOCUS_CVC;
import static com.paymentez.android.view.CardInputListener.FocusField.FOCUS_EXPIRY;
import static com.paymentez.android.view.CardInputListener.FocusField.FOCUS_POSTAL;


/**
 * A multiline card input widget using the support design library's {@link TextInputLayout}
 * to match Material Design.
 */
public class CardMultilineWidget extends LinearLayout {

    static final String CARD_MULTILINE_TOKEN = "CardMultilineView";
    static final long CARD_NUMBER_HINT_DELAY = 120L;
    static final long COMMON_HINT_DELAY = 90L;

    private @Nullable
    CardInputListener mCardInputListener;
    private CardNumberEditText mCardNumberEditText;
    private ExpiryDateEditText mExpiryDateEditText;
    private PaymentezEditText mCvcEditText;
    private PaymentezEditText mPostalCodeEditText;
    private PaymentezEditText mCardHolderNameEditText;
    private TextInputLayout mCardNumberTextInputLayout;
    private TextInputLayout mExpiryTextInputLayout;
    private TextInputLayout mCvcTextInputLayout;
    private TextInputLayout mNipTextInputLayout;
    private TextInputLayout mFiscalNumberTextInputLayout;

    private TextInputLayout mPostalInputLayout;
    private TextInputLayout mCardHolderNameInputLayout;
    private ImageButton imageButtonScanCard;
    private ImageView imageViewPaymentezLogo;
    private LinearLayout second_row_layout;
    private LinearLayout third_row_layout;
    private LinearLayout four_row_layout;
    private Button buttonHideNip;
    private PaymentezEditText mFiscalNumberEditText;
    private PaymentezEditText mNipEditText;


    private boolean mIsEnabled;
    private boolean mShouldShowPostalCode;
    private boolean mShouldShowCardHolderName;
    private boolean mShouldShowFiscalNumber;
    private boolean mShouldShowNip;
    private boolean mShouldShowScanCard;
    private boolean mShouldShowPaymentezLogo;
    private boolean mHasAdjustedDrawable;

    private int MY_SCAN_REQUEST_CODE = 10344;

    private @DrawableRes
    int mCachedIconResource;
    String mCardBrand;
    String mCardLogo;
    boolean mIsOtp;
    private @ColorInt
    int mTintColorInt;

    public CardMultilineWidget(Context context) {
        super(context);
        initView(null);
    }

    public CardMultilineWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CardMultilineWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @VisibleForTesting
    CardMultilineWidget(Context context, boolean shouldShowPostalCode, boolean shouldShowCardHolderName, boolean shouldShowScanCard, boolean shouldShowPaymentezLogo) {
        super(context);
        mShouldShowPostalCode = shouldShowPostalCode;
        mShouldShowCardHolderName = shouldShowCardHolderName;
        mShouldShowScanCard = shouldShowScanCard;
        mShouldShowPaymentezLogo = shouldShowPaymentezLogo;
        initView(null);
    }

    /**
     * @param cardInputListener A {@link CardInputListener} to be notified of changes
     *                          to the user's focused field
     */
    public void setCardInputListener(@Nullable CardInputListener cardInputListener) {
        mCardInputListener = cardInputListener;
    }

    /**
     * Gets a {@link Card} object from the user input, if all fields are valid. If not, returns
     * {@code null}.
     *
     * @return a valid {@link Card} object based on user input, or {@code null} if any field is
     * invalid
     */
    @Nullable
    public Card getCard() {
        if (validateAllFields()) {
            String cardNumber = mCardNumberEditText.getCardNumber();
            int[] cardDate = mExpiryDateEditText.getValidDateFields();
            String cvcValue = mCvcEditText.getText().toString();

            int month = 0;
            int year = 0;

            if(cardDate != null && cardDate.length>=2){
                month = cardDate[0];
                year = cardDate[1];
            }

            Card card = new Card(cardNumber, month, year, cvcValue);

            if(mCardBrand.equals("ex") || mCardBrand.equals("ak")){
                card.setCVC("");
                card.setFiscal_number(mFiscalNumberEditText.getText().toString());
                if(mNipTextInputLayout.getVisibility() == View.VISIBLE){
                    card.setNip(mNipEditText.getText().toString());
                    card.setCard_auth("AUTH_NIP");
                }else{
                    card.setCard_auth("AUTH_OTP");
                }

            }

            if (mShouldShowPostalCode) {
                card.setAddressZip(mPostalCodeEditText.getText().toString());
            }
            if (mShouldShowCardHolderName) {
                card.setHolderName(mCardHolderNameEditText.getText().toString());
            }

            return card.addLoggingToken(CARD_MULTILINE_TOKEN);
        }

        return null;
    }

    /**
     * Validates all fields and shows error messages if appropriate.
     *
     * @return {@code true} if all shown fields are valid, {@code false} otherwise
     */
    public boolean validateAllFields() {
        boolean cardNumberIsValid =
                CardUtils.isValidCardNumber(mCardNumberEditText.getCardNumber());
        boolean expiryIsValid = mExpiryDateEditText.getValidDateFields() != null &&
                mExpiryDateEditText.isDateValid();

        boolean cvcIsValid = ViewUtils.isCvcMaximalLength(
                mCardBrand, mCvcEditText.getText().toString());

        if(mCardBrand.equals("ex") || mCardBrand.equals("ak")){
            expiryIsValid = true;
            cvcIsValid = true;
        }
        mCardNumberEditText.setShouldShowError(!cardNumberIsValid);
        mExpiryDateEditText.setShouldShowError(!expiryIsValid);
        mCvcEditText.setShouldShowError(!cvcIsValid);
        boolean postalCodeIsValidOrGone, cardHolderNameIsValidOrGone, nipIsValidOrGone, fiscalNumberIsValidOrGone;
        if (mShouldShowPostalCode) {
            postalCodeIsValidOrGone = isPostalCodeMaximalLength(true,
                    mPostalCodeEditText.getText().toString());
            mPostalCodeEditText.setShouldShowError(!postalCodeIsValidOrGone);
        } else {
            postalCodeIsValidOrGone = true;
        }

        if (mShouldShowCardHolderName) {
            cardHolderNameIsValidOrGone = isCardHolderNameValid(
                    mCardHolderNameEditText.getText().toString());
            mCardHolderNameEditText.setShouldShowError(!cardHolderNameIsValidOrGone);
        } else {
            cardHolderNameIsValidOrGone = true;
        }


        if(mShouldShowFiscalNumber){
            fiscalNumberIsValidOrGone = isFiscalNumberValid(
                    mFiscalNumberEditText.getText().toString());
            mFiscalNumberEditText.setShouldShowError(!fiscalNumberIsValidOrGone);
        } else {
            fiscalNumberIsValidOrGone = true;
        }

        if (mShouldShowNip) {
            nipIsValidOrGone = isNipValid(
                    mNipEditText.getText().toString());
            mNipEditText.setShouldShowError(!nipIsValidOrGone);
        } else {
            nipIsValidOrGone = true;
        }

        return cardNumberIsValid
                && expiryIsValid
                && cvcIsValid
                && postalCodeIsValidOrGone
                && fiscalNumberIsValidOrGone
                && nipIsValidOrGone
                && cardHolderNameIsValidOrGone;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            //updateBrand(mCardBrand, mCardLogo);
        }
    }

    public void setShouldShowPostalCode(boolean shouldShowPostalCode) {
        mShouldShowPostalCode = shouldShowPostalCode;
        adjustViewForPostalCodeAttribute();
    }

    public void setShouldShowCardHolderName(boolean shouldShowCardHolderName) {
        mShouldShowCardHolderName = shouldShowCardHolderName;
        adjustViewForCardHolderNameAttribute();
    }

    public void setShouldShowScanCard(boolean shouldShowScanCard) {
        mShouldShowScanCard = shouldShowScanCard;
        adjustViewForScanCardAttribute();
    }

    public void setShouldShowPaymentezLogo(boolean shouldShowPaymentezLogo) {
        mShouldShowPaymentezLogo = shouldShowPaymentezLogo;
        adjustViewForPaymentezLogoAttribute();
    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mExpiryTextInputLayout.setEnabled(enabled);
        mCardNumberTextInputLayout.setEnabled(enabled);
        mCvcTextInputLayout.setEnabled(enabled);
        mNipTextInputLayout.setEnabled(enabled);
        mFiscalNumberTextInputLayout.setEnabled(enabled);
        mPostalInputLayout.setEnabled(enabled);
        mCardHolderNameInputLayout.setEnabled(enabled);
        mIsEnabled = enabled;
    }

    void adjustViewForPostalCodeAttribute() {
        // Set the label/hint to the shorter value if we have three things in a row.
        @StringRes int expiryLabel = mShouldShowPostalCode
                ? R.string.expiry_label_short
                : R.string.acc_label_expiry_date;
        mExpiryTextInputLayout.setHint(getResources().getString(expiryLabel));

        @IdRes int focusForward = mShouldShowPostalCode
                ? R.id.et_add_source_postal_ml
                : NO_ID;
        mCvcEditText.setNextFocusForwardId(focusForward);
        mCvcEditText.setNextFocusDownId(focusForward);

        int visibility = mShouldShowPostalCode ? View.VISIBLE : View.GONE;
        mPostalInputLayout.setVisibility(visibility);

        int marginPixels = mShouldShowPostalCode
                ? getResources().getDimensionPixelSize(R.dimen.add_card_expiry_middle_margin)
                : 0;
        LinearLayout.LayoutParams linearParams =
                (LinearLayout.LayoutParams) mCvcTextInputLayout.getLayoutParams();
        linearParams.setMargins(0, 0, marginPixels, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            linearParams.setMarginEnd(marginPixels);
        }

        mCvcTextInputLayout.setLayoutParams(linearParams);
    }


    void adjustViewForCardHolderNameAttribute() {
        int visibility = mShouldShowCardHolderName ? View.VISIBLE : View.GONE;
        mCardHolderNameInputLayout.setVisibility(visibility);
        if(mShouldShowCardHolderName){
            mCardHolderNameEditText.requestFocus();
        }else{
            mCardNumberEditText.requestFocus();
        }
    }


    void adjustViewForPaymentezLogoAttribute() {

        int visibility = mShouldShowPaymentezLogo ? View.VISIBLE : View.GONE;
        imageViewPaymentezLogo.setVisibility(visibility);
    }
    void adjustViewForScanCardAttribute() {

        int visibility = mShouldShowScanCard ? View.VISIBLE : View.GONE;
        imageButtonScanCard.setVisibility(visibility);
    }


    static boolean isCardHolderNameValid(@Nullable String text) {
        return text != null && text.length() >= 5;
    }

    static boolean isFiscalNumberValid(@Nullable String text) {
        return text != null && text.length() >= 6;
    }

    static boolean isNipValid(@Nullable String text) {
        return text != null && text.length() == 4;
    }

    static boolean isPostalCodeMaximalLength(boolean isZip, @Nullable String text) {
        return isZip && text != null && text.length() == 5;
    }

    private void checkAttributeSet(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CardMultilineWidget,
                    0, 0);

            try {
                mShouldShowScanCard =
                        a.getBoolean(R.styleable.CardMultilineWidget_shouldShowScanCard, false);
                mShouldShowPaymentezLogo =
                        a.getBoolean(R.styleable.CardMultilineWidget_shouldShowPaymentezLogo, true);

                mShouldShowPostalCode =
                        a.getBoolean(R.styleable.CardMultilineWidget_shouldShowPostalCode, false);
                mShouldShowCardHolderName =
                        a.getBoolean(R.styleable.CardMultilineWidget_shouldShowCardHolderName, true);
            } finally {
                a.recycle();
            }
        }
    }

    private void flipToCvcIconIfNotFinished() {
        if (ViewUtils.isCvcMaximalLength(mCardBrand, mCvcEditText.getText().toString())) {
            return;
        }

        @DrawableRes int resourceId = Card.AMERICAN_EXPRESS.equals(mCardBrand)
                ? R.drawable.ic_cvc_amex
                : R.drawable.ic_cvc;

        updateDrawable(resourceId, true, null);
    }

    @StringRes
    private int getCvcHelperText() {
        return Card.AMERICAN_EXPRESS.equals(mCardBrand)
                ? R.string.cvc_multiline_helper_amex
                : R.string.cvc_multiline_helper;
    }

    private int getDynamicBufferInPixels() {
        float pixelsToAdjust = getResources()
                .getDimension(R.dimen.card_icon_multiline_padding_bottom);
        BigDecimal bigDecimal = new BigDecimal(pixelsToAdjust);
        BigDecimal pixels = bigDecimal.setScale(0, RoundingMode.HALF_DOWN);
        return pixels.intValue();
    }

    private void initView(AttributeSet attrs) {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.card_multiline_widget, this);
        final Context mContext = this.getContext();

        imageViewPaymentezLogo = (ImageView) findViewById(R.id.imageViewPaymentezLogo);

        imageButtonScanCard = (ImageButton) findViewById(R.id.imageButtonScanCard);
        imageButtonScanCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
                Fragment auxiliary = new Fragment() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (requestCode == MY_SCAN_REQUEST_CODE) {

                            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                                if(scanResult.cardNumber!=null) {
                                    mCardNumberEditText.setText(scanResult.cardNumber);
                                }
                                if(scanResult.cardholderName!=null){
                                    mCardHolderNameEditText.setText(scanResult.cardholderName);
                                }

                                if(scanResult.cvv!=null){
                                    mCvcEditText.setText(scanResult.cvv);
                                }

                                if(scanResult.expiryMonth > 0 && scanResult.expiryYear > 0){

                                    mExpiryDateEditText.setText(String.format(Locale.ENGLISH, "%02d", scanResult.expiryMonth)+"/"+ (""+scanResult.expiryYear).substring(2));
                                }

                                validateAllFields();

                            }

                        }

                        super.onActivityResult(requestCode, resultCode, data);
                        fm.beginTransaction().remove(this).commit();
                    }
                };
                fm.beginTransaction().add(auxiliary, "FRAGMENT_TAG").commit();
                fm.executePendingTransactions();

                Intent scanIntent = new Intent(mContext, CardIOActivity.class);

                // customize these values to suit your needs.
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
                scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
                scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true);
                scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true);

                // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
                auxiliary.startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);

            }
        });




        mCardNumberEditText = (CardNumberEditText) findViewById(R.id.et_add_source_card_number_ml);
        mNipEditText = (PaymentezEditText) findViewById(R.id.et_add_source_nip_ml);
        mFiscalNumberEditText = (PaymentezEditText) findViewById(R.id.et_add_source_fiscal_number_ml);



        mExpiryDateEditText = (ExpiryDateEditText) findViewById(R.id.et_add_source_expiry_ml);
        mCvcEditText = (PaymentezEditText) findViewById(R.id.et_add_source_cvc_ml);
        mPostalCodeEditText = (PaymentezEditText) findViewById(R.id.et_add_source_postal_ml);
        mCardHolderNameEditText = (PaymentezEditText) findViewById(R.id.et_add_source_cardholdername_ml);
        mTintColorInt = mCardNumberEditText.getHintTextColors().getDefaultColor();

        mCardBrand = Card.UNKNOWN;
        // This sets the value of mShouldShowPostalCode
        checkAttributeSet(attrs);


        mCardNumberTextInputLayout = (TextInputLayout) findViewById(R.id.tl_add_source_card_number_ml);
        mExpiryTextInputLayout = (TextInputLayout) findViewById(R.id.tl_add_source_expiry_ml);
        // We dynamically set the hint of the CVC field, so we need to keep a reference.
        mCvcTextInputLayout = (TextInputLayout) findViewById(R.id.tl_add_source_cvc_ml);
        mNipTextInputLayout = (TextInputLayout) findViewById(R.id.tl_add_source_nip_ml);
        mFiscalNumberTextInputLayout = (TextInputLayout) findViewById(R.id.tl_add_source_fiscal_number_ml);
        mPostalInputLayout = (TextInputLayout) findViewById(R.id.tl_add_source_postal_ml);
        mCardHolderNameInputLayout = (TextInputLayout) findViewById(R.id.tl_add_source_cardholdername_ml);


        second_row_layout = (LinearLayout) findViewById(R.id.second_row_layout);
        third_row_layout = (LinearLayout) findViewById(R.id.third_row_layout);
        four_row_layout = (LinearLayout) findViewById(R.id.four_row_layout);


        buttonHideNip = (Button) findViewById(R.id.buttonHideNip);
        buttonHideNip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNipTextInputLayout.getVisibility() == View.INVISIBLE){
                    mShouldShowNip = true;
                    mNipTextInputLayout.setVisibility(View.VISIBLE);
                }else{
                    mShouldShowNip = false;
                    mNipTextInputLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (mShouldShowPostalCode) {
            // Set the label/hint to the shorter value if we have three things in a row.
            mExpiryTextInputLayout.setHint(getResources().getString(R.string.expiry_label_short));
        }

        initTextInputLayoutErrorHandlers(
                mCardHolderNameInputLayout,
                mCardNumberTextInputLayout,
                mExpiryTextInputLayout,
                mCvcTextInputLayout,
                mPostalInputLayout,
                mNipTextInputLayout,
                mFiscalNumberTextInputLayout
                );

        initErrorMessages();
        initFocusChangeListeners();
        initDeleteEmptyListeners();

        mCardNumberEditText.setCardBrandChangeListener(
                new CardNumberEditText.CardBrandChangeListener() {
                    @Override
                    public void onCardBrandChanged(@NonNull String brand, String cardLogo, boolean isOtp) {
                        updateBrand(brand, cardLogo, isOtp);
                    }
                });

        mCardNumberEditText.setCardNumberCompleteListener(
                new CardNumberEditText.CardNumberCompleteListener() {
                    @Override
                    public void onCardNumberComplete() {
                        if(mCardBrand.equals("ex") || mCardBrand.equals("ak")){
                            mFiscalNumberEditText.requestFocus();
                        }else{
                            mExpiryDateEditText.requestFocus();
                        }

                        if (mCardInputListener != null) {
                            mCardInputListener.onCardComplete();
                        }
                    }
                });


        mExpiryDateEditText.setExpiryDateEditListener(
                new ExpiryDateEditText.ExpiryDateEditListener() {
                    @Override
                    public void onExpiryDateComplete() {
                        mCvcEditText.requestFocus();
                        if (mCardInputListener != null) {
                            mCardInputListener.onExpirationComplete();
                        }
                    }
                });

        mCvcEditText.setAfterTextChangedListener(
                new PaymentezEditText.AfterTextChangedListener() {
                    @Override
                    public void onTextChanged(String text) {
                        if (ViewUtils.isCvcMaximalLength(mCardBrand, text)) {
                            updateBrand(mCardBrand, mCardLogo, mIsOtp);
                            if (mShouldShowPostalCode) {
                                mPostalCodeEditText.requestFocus();
                            }
                            if (mCardInputListener != null) {
                                mCardInputListener.onCvcComplete();
                            }
                        } else {
                            flipToCvcIconIfNotFinished();
                        }
                        mCvcEditText.setShouldShowError(false);
                    }
                });




        adjustViewForPostalCodeAttribute();
        adjustViewForCardHolderNameAttribute();
        adjustViewForScanCardAttribute();
        adjustViewForPaymentezLogoAttribute();

        mPostalCodeEditText.setAfterTextChangedListener(
                new PaymentezEditText.AfterTextChangedListener() {
                    @Override
                    public void onTextChanged(String text) {
                        if (isPostalCodeMaximalLength(true, text)
                                && mCardInputListener != null) {
                            mCardInputListener.onPostalCodeComplete();
                        }
                        mPostalCodeEditText.setShouldShowError(false);
                    }
                });

        mCardNumberEditText.updateLengthFilter();
        updateBrand(Card.UNKNOWN, mCardLogo, mIsOtp);
        setEnabled(true);
    }

    private void initDeleteEmptyListeners() {

        mExpiryDateEditText.setDeleteEmptyListener(
                new BackUpFieldDeleteListener(mCardNumberEditText));

        mCvcEditText.setDeleteEmptyListener(
                new BackUpFieldDeleteListener(mExpiryDateEditText));

        // It doesn't matter whether or not the postal code is shown;
        // we can still say where you go when you delete an empty field from it.
        if (mPostalCodeEditText != null) {
            mPostalCodeEditText.setDeleteEmptyListener(
                    new BackUpFieldDeleteListener(mCvcEditText));
        }

    }

    private void initErrorMessages() {
        mCardHolderNameEditText.setErrorMessage(getContext().getString(R.string.invalid_cardholdername));
        mCardNumberEditText.setErrorMessage(getContext().getString(R.string.invalid_card_number));
        mExpiryDateEditText.setErrorMessage(getContext().getString(R.string.invalid_expiry_year));
        mCvcEditText.setErrorMessage(getContext().getString(R.string.invalid_cvc));
        mPostalCodeEditText.setErrorMessage(getContext().getString(R.string.invalid_zip));
        mPostalCodeEditText.setErrorMessage(getContext().getString(R.string.invalid_cardholdername));
        mFiscalNumberEditText.setErrorMessage(getContext().getString(R.string.error_fiscal_number));
        mNipEditText.setErrorMessage(getContext().getString(R.string.error_nip));
    }

    private void initFocusChangeListeners() {
        mCardNumberEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mCardNumberEditText.setHintDelayed(
                            R.string.card_number_hint, CARD_NUMBER_HINT_DELAY);
                    if (mCardInputListener != null) {
                        mCardInputListener.onFocusChange(FOCUS_CARD);
                    }
                } else {
                    mCardNumberEditText.setHint("");
                }
            }
        });

        mExpiryDateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mExpiryDateEditText.setHintDelayed(
                            R.string.expiry_date_hint, COMMON_HINT_DELAY);
                    if (mCardInputListener != null) {
                        mCardInputListener.onFocusChange(FOCUS_EXPIRY);
                    }
                } else {


                    mExpiryDateEditText.setHint("");

                }
            }
        });

        mCvcEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    flipToCvcIconIfNotFinished();
                    @StringRes int helperText = getCvcHelperText();
                    mCvcEditText.setHintDelayed(helperText, COMMON_HINT_DELAY);
                    if (mCardInputListener != null) {
                        mCardInputListener.onFocusChange(FOCUS_CVC);
                    }
                } else {
                    updateBrand(mCardBrand, mCardLogo, mIsOtp);
                    mCvcEditText.setHint("");
                }
            }
        });

        if (mPostalCodeEditText != null) {
            mPostalCodeEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!mShouldShowPostalCode) {
                        return;
                    }
                    if (hasFocus) {
                        mPostalCodeEditText.setHintDelayed(R.string.zip_helper, COMMON_HINT_DELAY);
                        if (mCardInputListener != null) {
                            mCardInputListener.onFocusChange(FOCUS_POSTAL);
                        }
                    } else {
                        mPostalCodeEditText.setHint("");
                    }
                }
            });
        }


        if (mCardHolderNameEditText != null) {
            mCardHolderNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!mShouldShowCardHolderName) {
                        return;
                    }
                    if (hasFocus) {
                        mCardHolderNameEditText.setHintDelayed(R.string.name_helper, COMMON_HINT_DELAY);
                        if (mCardInputListener != null) {
                            mCardInputListener.onFocusChange(FOCUS_CARDHOLDERNAME);
                        }
                    } else {
                        mCardHolderNameEditText.setHint("");
                        if(!isCardHolderNameValid(mCardHolderNameEditText.getText().toString())){
                            mCardHolderNameEditText.setShouldShowError(true);
                        }else{
                            mCardHolderNameEditText.setShouldShowError(false);
                        }
                    }
                }
            });
        }


        if (mFiscalNumberEditText != null) {
            mFiscalNumberEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!mShouldShowFiscalNumber) {
                        return;
                    }
                    if (hasFocus) {
                    } else {
                        mFiscalNumberEditText.setHint("");
                        if(!isFiscalNumberValid(mFiscalNumberEditText.getText().toString())){
                            mFiscalNumberEditText.setShouldShowError(true);
                        }else{
                            mFiscalNumberEditText.setShouldShowError(false);
                        }
                    }
                }
            });
        }


        if (mNipEditText != null) {
            mNipEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!mShouldShowNip) {
                        return;
                    }
                    if (hasFocus) {
                    } else {
                        mFiscalNumberEditText.setHint("");
                        if(!isNipValid(mNipEditText.getText().toString())){
                            mNipEditText.setShouldShowError(true);
                        }else{
                            mNipEditText.setShouldShowError(false);
                        }
                    }
                }
            });
        }


    }

    private void initTextInputLayoutErrorHandlers(
            TextInputLayout cardholdernameInputLayout,
            TextInputLayout cardInputLayout,
            TextInputLayout expiryInputLayout,
            TextInputLayout cvcTextInputLayout,
            TextInputLayout postalInputLayout,
            TextInputLayout nipInputLayout,
            TextInputLayout fiscalNumberInputLayout
            ) {


        mCardNumberEditText.setErrorMessageListener(new ErrorListener(cardInputLayout));
        mExpiryDateEditText.setErrorMessageListener(new ErrorListener(expiryInputLayout));



        mCvcEditText.setErrorMessageListener(new ErrorListener(cvcTextInputLayout));
        if (mPostalCodeEditText != null) {
            mPostalCodeEditText.setErrorMessageListener(new ErrorListener(postalInputLayout));
        }


        if (mCardHolderNameEditText != null) {
            mCardHolderNameEditText.setErrorMessageListener(new ErrorListener(cardholdernameInputLayout));
        }

        if (mNipEditText != null) {
            mNipEditText.setErrorMessageListener(new ErrorListener(nipInputLayout));
        }

        if (mFiscalNumberEditText != null) {
            mFiscalNumberEditText.setErrorMessageListener(new ErrorListener(fiscalNumberInputLayout));
        }

    }

    private void updateBrand(@NonNull String brand, String cardLogo, boolean isOtp) {
        mCardBrand = brand;
        mCardLogo = cardLogo;
        mIsOtp = isOtp;

        updateCvc(mCardBrand);
        int iconResourceId = BRAND_RESOURCE_MAP.get(Card.UNKNOWN);
        try{
            iconResourceId = BRAND_RESOURCE_MAP.get(mCardBrand);
        }catch(Exception e){}


        if(brand.equals("ex") || brand.equals("ak")){
            showTuyaFields(true);
        }else{
            showTuyaFields(false);
        }

        if(!isOtp){
            buttonHideNip.setVisibility(View.INVISIBLE);
        }

        updateDrawable(iconResourceId, Card.UNKNOWN.equals(brand), cardLogo);
    }

    private void showTuyaFields(boolean isVisible){
        if(isVisible){
            second_row_layout.setVisibility(View.GONE);
            third_row_layout.setVisibility(View.VISIBLE);
            four_row_layout.setVisibility(View.VISIBLE);

            mCardNumberEditText.setNextFocusForwardId(R.id.et_add_source_fiscal_number_ml);
            mCardNumberEditText.setNextFocusDownId(R.id.et_add_source_fiscal_number_ml);

            mShouldShowNip = true;
            mShouldShowFiscalNumber = true;
        }else{
            second_row_layout.setVisibility(View.VISIBLE);
            third_row_layout.setVisibility(View.GONE);
            four_row_layout.setVisibility(View.GONE);

            mCardNumberEditText.setNextFocusForwardId(R.id.et_add_source_expiry_ml);
            mCardNumberEditText.setNextFocusDownId(R.id.et_add_source_expiry_ml);

            mShouldShowNip = false;
            mShouldShowFiscalNumber = false;
        }
    }

    private void updateCvc(@NonNull String brand) {
        if (Card.AMERICAN_EXPRESS.equals(brand)) {
            mCvcEditText.setFilters(
                    new InputFilter[]{
                            new InputFilter.LengthFilter(Card.CVC_LENGTH_AMERICAN_EXPRESS)
                    });
            mCvcTextInputLayout.setHint(getResources().getString(R.string.cvc_amex_hint));
        } else {
            mCvcEditText.setFilters(
                    new InputFilter[]{
                            new InputFilter.LengthFilter(Card.CVC_LENGTH_COMMON)});
            mCvcTextInputLayout.setHint(getResources().getString(R.string.cvc_number_hint));
        }
    }

    @SuppressWarnings("deprecation")
    private void updateDrawable(
            @DrawableRes int iconResourceId,
            final boolean needsTint, String brandLogoUrl) {


        final Drawable[] icon = new Drawable[1];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            icon[0] = getResources().getDrawable(iconResourceId, null);
        } else {
            // This method still triggers the "deprecation" warning, despite the other
            // one not being allowed for SDK < 21
            icon[0] = getResources().getDrawable(iconResourceId);
        }

        Drawable[] drawables = mCardNumberEditText.getCompoundDrawables();
        Drawable original = drawables[0];
        if (original == null) {
            return;
        }

        final Rect copyBounds = new Rect();
        original.copyBounds(copyBounds);

        final int iconPadding = mCardNumberEditText.getCompoundDrawablePadding();

        if (!mHasAdjustedDrawable) {
            copyBounds.top = copyBounds.top - getDynamicBufferInPixels();
            copyBounds.bottom = copyBounds.bottom - getDynamicBufferInPixels();
            mHasAdjustedDrawable = true;
        }

        icon[0].setBounds(copyBounds);
        final Drawable[] compatIcon = {DrawableCompat.wrap(icon[0])};
        if (needsTint) {
            DrawableCompat.setTint(compatIcon[0].mutate(), mTintColorInt);
        }

        mCardNumberEditText.setCompoundDrawablePadding(iconPadding);
        mCardNumberEditText.setCompoundDrawables(compatIcon[0], null, null, null);

        if(brandLogoUrl != null && !brandLogoUrl.equals(Card.UNKNOWN)){
            Picasso.get().load(brandLogoUrl).into(new Target() {

                @Override
                public void onPrepareLoad(Drawable arg0) {


                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                    Drawable resource = new BitmapDrawable(getContext().getResources(),bitmap);
                    resource.setBounds(copyBounds);
                    Drawable compatIcon = DrawableCompat.wrap(resource);
                    if(needsTint){
                        DrawableCompat.setTint(compatIcon.mutate(), mTintColorInt);
                    }
                    mCardNumberEditText.setCompoundDrawablePadding(iconPadding);
                    mCardNumberEditText.setCompoundDrawables(resource, null, null, null);

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }
            });

        }

    }

    private static class ErrorListener implements PaymentezEditText.ErrorMessageListener {

        TextInputLayout textInputLayout;

        ErrorListener(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @Override
        public void displayErrorMessage(@Nullable String message) {
            if (message == null) {
                textInputLayout.setError(message);
                textInputLayout.setErrorEnabled(false);
            } else {
                textInputLayout.setError(message);
            }
        }
    }
}
