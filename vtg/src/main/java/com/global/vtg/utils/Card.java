package com.global.vtg.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.global.vtg.utils.textView.RegularTextView;
import com.global.vtg.utils.textView.SemiBoldTextView;
import com.vtg.R;

public class Card extends RelativeLayout {
    private final Context mContext;
    @DrawableRes
    private int mCardBackBackground;
    private String mPersonPic;
    private ImageView imgPerson;
    private ImageView flag;
    private ImageView qrCode;
    private SemiBoldTextView txtLName;
    private SemiBoldTextView txtDob;
    private SemiBoldTextView txtCardNo;
    private RegularTextView txtFName;
    private RegularTextView txtAdd1;
    private RegularTextView txtAdd2;
    private TextView txtDl;
    private RegularTextView txtAdd3;
    private TextView txtPP;
    private int mTextColor = Color.BLACK;
    public Card(Context context) {
        this(context, null);
    }

    public Card(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (context != null) {
            this.mContext = context;
        } else {
            this.mContext = getContext();
        }


        init();
        loadAttributes(attrs);
        initDefaults();

//        initDefaults();
//        addListeners();
    }


    private void init() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.card, this, true);

        imgPerson = findViewById(R.id.person);
        txtLName = findViewById(R.id.tvLName);
        txtFName = findViewById(R.id.tvFName);
        txtAdd1 = findViewById(R.id.add1);
        txtAdd2 = findViewById(R.id.add2);
        txtAdd3 = findViewById(R.id.add3);
        txtDob = findViewById(R.id.dob);
        qrCode = findViewById(R.id.qrCode);
        txtDl = findViewById(R.id.tvDL);
        flag = findViewById(R.id.flag);
        txtPP = findViewById(R.id.tvPP);
        txtCardNo = findViewById(R.id.tvCardNo);

    }

    private void loadAttributes(@Nullable AttributeSet attrs) {

        final TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs,
                R.styleable.vaccincard, 0, 0);

        try {

            mCardBackBackground = a.getResourceId(R.styleable.vaccincard_cardBackground,
                    R.drawable.ic_card_vaccine_bg);
            mPersonPic= a.getString(R.styleable.vaccincard_person_pic);
            mTextColor = a.getColor(R.styleable.vaccincard_textColor,
                    Color.BLACK);
        } finally {
            a.recycle();
        }
    }

    private void initDefaults() {
        setBackground(ContextCompat.getDrawable(mContext, mCardBackBackground));
        Glide.with(mContext)
            .load(mPersonPic)
            .into(imgPerson);

        txtLName.setTextColor(mTextColor);
        txtFName.setTextColor(mTextColor);
        txtAdd1.setTextColor(mTextColor);
        txtAdd2.setTextColor(mTextColor);
        txtAdd3.setTextColor(mTextColor);
        txtDl.setTextColor(mTextColor);
        txtPP.setTextColor(mTextColor);
        txtDob.setTextColor(mTextColor);
        txtCardNo.setTextColor(mTextColor);
        txtAdd2.setVisibility(View.GONE);
//
//        txtLName.setTextSize(13);
//        txtDob.setTextSize(13);
//        txtDl.setTextSize(12);
    }


    public void setPersonImage(String url){
        Glide.with(mContext)
                .load(url)
                .into(imgPerson);
        redrawViews();
    }
   public void setLastName(String name){
       txtLName.setText(name);
        redrawViews();
    }
    public void setFirstName(String name){
       txtFName.setText(name);
        redrawViews();
    }
    public void setAddressline1(String name){
        txtAdd1.setText(name);
        redrawViews();
    }
    public void setAddressline2(String name){
        txtAdd2.setVisibility(View.VISIBLE);
        txtAdd2.setText(name);
        redrawViews();
    }
    public void setAddressline3(String name){
        txtAdd3.setText(name);
        redrawViews();
    }
    public void setDL(Spanned  name){
        txtDl.setText(name);
        redrawViews();
    }
    public void setCardNo(String name){
        txtCardNo.setText(name);
        redrawViews();
    }

    public void setCountryImage(String name){

        Glide.with(mContext)
                .load(name)
                .into(flag);
        redrawViews();
    }

    public void setDob(String name){
        txtDob.setText(name);
        redrawViews();
    }

    public void setPP(Spanned name){
        txtPP.setText(name);
        redrawViews();
    }
    public void setQrCode(Bitmap name){
        qrCode.setImageBitmap(name);
        redrawViews();
    }

    private void redrawViews() {
        invalidate();
        requestLayout();
    }



}
