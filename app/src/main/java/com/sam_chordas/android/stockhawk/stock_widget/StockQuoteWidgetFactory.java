/*
 * Copyright 2016 Rohit Sharma (skyrohithigh)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sam_chordas.android.stockhawk.stock_widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by rohit on 6/2/16.
 */
public class StockQuoteWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context mContext;
    private int mWidgetId;

    public StockQuoteWidgetFactory(Context mContext, Intent intent) {
        this.mContext = mContext;
        this.mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
            new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                    QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
            QuoteColumns.ISCURRENT + " = ?",
            new String[]{"1"},
            null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_quote);
        if (mCursor.moveToPosition(position)) {
            rv.setTextViewText(R.id.stock_symbol,
                    mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
            rv.setTextViewText(R.id.bid_price,
                    mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            rv.setTextViewText(R.id.change,
                    mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));

            if (mCursor.getInt(mCursor.getColumnIndex("is_up")) == 1) {
                rv.setTextColor(R.id.change,mContext.getResources().getColor(R.color.widget_stock_up));
            } else {
                rv.setTextColor(R.id.change,mContext.getResources().getColor(R.color.widget_stock_down));
            }
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
