package com.paymentez.examplestore.utils;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.paymentez.android.model.Card;
import com.paymentez.examplestore.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mmucito on 06/09/17.
 */

public class MyCardAdapter  extends RecyclerView.Adapter<MyCardAdapter.ViewHolder> {
    private ArrayList<Card> cards;

    public static final Map<String , Integer> TEMPLATE_RESOURCE_MAP = new HashMap<>();
    static {
        TEMPLATE_RESOURCE_MAP.put(Card.AMERICAN_EXPRESS, R.drawable.ic_amex);
        TEMPLATE_RESOURCE_MAP.put(Card.DINERS_CLUB, R.drawable.ic_diners);
        TEMPLATE_RESOURCE_MAP.put(Card.DISCOVER, R.drawable.ic_discover);
        TEMPLATE_RESOURCE_MAP.put(Card.JCB, R.drawable.ic_jcb);
        TEMPLATE_RESOURCE_MAP.put(Card.MASTERCARD, R.drawable.ic_mastercard);
        TEMPLATE_RESOURCE_MAP.put(Card.VISA, R.drawable.ic_visa);
        TEMPLATE_RESOURCE_MAP.put(Card.UNKNOWN, R.drawable.ic_unknown);
    }

    public interface OnCardSelectedListener {
        void onItemClick(Card item);
    }


    private final OnCardSelectedListener listener;

    public interface OnCardDeletedClickListener {
        void onItemClick(Card item);
    }


    private final OnCardDeletedClickListener deleteListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewCardNumber;
        public TextView textViewCardHoldersName;
        public ImageView imageViewBrandCard;
        public ImageButton imageViewDeleteCard;

        public View view;

        public ViewHolder(View view) {
            super(view);
            textViewCardNumber = (TextView) view.findViewById(R.id.textViewCardNumber);
            textViewCardHoldersName = (TextView) view.findViewById(R.id.textViewCardHoldersName);
            imageViewBrandCard = (ImageView) view.findViewById(R.id.imageViewBrandCard);
            imageViewDeleteCard = (ImageButton) view.findViewById(R.id.imageViewDeleteCard);
            this.view = view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyCardAdapter(ArrayList<Card> cards, OnCardSelectedListener listener, OnCardDeletedClickListener deleteListener) {
        this.cards = cards;
        this.deleteListener = deleteListener;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewCardNumber.setText("XXXX." + cards.get(position).getLast4() + " - status: " + cards.get(position).getStatus());
        holder.textViewCardHoldersName.setText(cards.get(position).getHolderName());

        @DrawableRes int iconResourceId = TEMPLATE_RESOURCE_MAP.get(cards.get(position).getType());
        holder.imageViewBrandCard.setImageResource(iconResourceId);



        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(cards.get(position));
            }
        });

        holder.imageViewDeleteCard.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                deleteListener.onItemClick(cards.get(position));
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cards.size();
    }
}