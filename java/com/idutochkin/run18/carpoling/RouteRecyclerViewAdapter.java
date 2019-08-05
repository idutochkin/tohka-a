package com.idutochkin.run18.carpoling;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class RouteRecyclerViewAdapter extends RecyclerView.Adapter<RouteRecyclerViewAdapter.CustomViewHolder> {
    private List<FeedRoute> feedRoutes;
    private Context mContext;
    private OnRouteClickListener onRouteClickListener;

    public RouteRecyclerViewAdapter(Context context, List<FeedRoute> feedRoutes) {
        this.feedRoutes = feedRoutes;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        final FeedRoute feedRoute = feedRoutes.get(i);

        customViewHolder.id.setText(Html.fromHtml(feedRoute.getId()));
        customViewHolder.checkId.setText(Html.fromHtml(feedRoute.getCheckId()));
        customViewHolder.pointFrom.setText(Html.fromHtml(feedRoute.getPointFrom().toString()));
        customViewHolder.pointTo.setText(Html.fromHtml(feedRoute.getPointTo()));
        customViewHolder.dateFrom.setText(Html.fromHtml(feedRoute.getDateFrom()));
        customViewHolder.timeFrom.setText(Html.fromHtml(feedRoute.getTimeFrom()));
        customViewHolder.maxPassengers.setText(Html.fromHtml(feedRoute.getMaxPassengers()));
        customViewHolder.leftPassengers.setText(Html.fromHtml(feedRoute.getLeftPassengers()));
        customViewHolder.price.setText(Html.fromHtml(feedRoute.getPrice()));
        if(feedRoute.getCheckId().equals("0")) {
        } else {
            customViewHolder.check.setText("ОТМЕНИТЬ");
            customViewHolder.route.setBackgroundColor(Color.parseColor("#ffc532"));
            customViewHolder.route.setElevation(20);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRouteClickListener.onItemClick(feedRoute);
            }
        };
        customViewHolder.check.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != feedRoutes ? feedRoutes.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout route;
        protected TextView id;
        protected TextView checkId;
        protected TextView pointFrom;
        protected TextView pointTo;
        protected TextView dateFrom;
        protected TextView timeFrom;
        protected TextView maxPassengers;
        protected TextView leftPassengers;
        protected TextView price;
        protected TextView check;

        public CustomViewHolder(View view) {
            super(view);
            this.route = (RelativeLayout) view.findViewById(R.id.route);
            this.id = (TextView) view.findViewById(R.id.id);
            this.checkId = (TextView) view.findViewById(R.id.checkId);
            this.pointFrom = (TextView) view.findViewById(R.id.pointFrom);
            this.pointTo = (TextView) view.findViewById(R.id.pointTo);
            this.dateFrom = (TextView) view.findViewById(R.id.dateFrom);
            this.timeFrom = (TextView) view.findViewById(R.id.timeFrom);
            this.maxPassengers = (TextView) view.findViewById(R.id.maxPassengers);
            this.leftPassengers = (TextView) view.findViewById(R.id.leftPassengers);
            this.price = (TextView) view.findViewById(R.id.price);
            this.check = (TextView) view.findViewById(R.id.check);
        }
    }

    public OnRouteClickListener getOnRouteClickListener() {
        return onRouteClickListener;
    }

    public void setOnRouteClickListener(OnRouteClickListener onRouteClickListener) {
        this.onRouteClickListener = onRouteClickListener;
    }
}