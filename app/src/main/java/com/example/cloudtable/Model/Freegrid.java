package com.example.cloudtable.Model;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;


/**
 * Created by gembong on 6/10/16.
 */
public class Freegrid extends FrameLayout {
    DataSetObserver dataSetObserver;

    private AdapterView.OnItemClickListener clickListener;
    private AdapterView.OnItemLongClickListener longClickListener;

    private MyClickListener localClickListener;
    private LongClickListener longClick;

    public Freegrid(Context context) {
        super(context);
        init();
    }

    public Freegrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Freegrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init() {
        dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        };
    }

    public void setAdapter(BaseAdapter adapter) {
        localClickListener = new MyClickListener();
        longClick = new LongClickListener();

        if (!(adapter instanceof PositionProfider)) {
            Exception e = new Exception();
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        adapter.registerDataSetObserver(dataSetObserver);
        for (int i = 0; i < adapter.getCount(); i++) {
            View v = adapter.getView(i, null, this);
            Rect rect = ((PositionProfider) adapter).getPositionRect(i);
            addView(v);
                dispatchSetPressed(true);
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                lp.width = rect.width();
                lp.height = rect.height();
                v.setLayoutParams(lp);
                v.setTranslationX(rect.left);
                v.setTranslationY(rect.top);
                v.setOnClickListener(localClickListener);
                v.setOnLongClickListener(longClick);
            if (v instanceof TableView){
                ((TableView) v).setDisplayingShapeName("meja " + i);
            }
        }
    }

    private class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(null, v, indexOfChild(v), v.getId());
        }
    }

    private class LongClickListener implements OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            longClickListener.onItemLongClick(null, v, indexOfChild(v), v.getId());
            return true;
        }
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener click) {
        longClickListener = click;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener click) {
        clickListener = click;
    }

//    public boolean createNewItem(final MainActivity.MyAdapter adapter, final ArrayList<Rect> rects) {
//            final Dialog d = new Dialog(getContext(),0);
//
//                d.setContentView(R.layout.new_item);
//                d.setTitle("Create new Item");
//
//                final EditText leftEdit = (EditText)d.findViewById(R.id.left);
//                final EditText topEdit = (EditText)d.findViewById(R.id.top);
//                final EditText rightEdit = (EditText)d.findViewById(R.id.right);
//                final EditText buttomEdit = (EditText)d.findViewById(R.id.bottom);
//                Button createButton = (Button) d.findViewById(R.id.create_button);
//                Button cancelButton = (Button) d.findViewById(R.id.cancel_button);
//                // if button create is clicked, create new item
//                createButton.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            int L = Integer.parseInt(String.valueOf(leftEdit.getText()));
//                            int T = Integer.parseInt(String.valueOf(topEdit.getText()));
//                            int R = Integer.parseInt(String.valueOf(rightEdit.getText()));
//                            int B = Integer.parseInt(String.valueOf(buttomEdit.getText()));
//                            if(R >L && B > T){
//                            Rect rect = new Rect(L, T, R, B);
//                            rects.add(rect);
//                            adapter.setRects(rects);
//                            adapter.unregisterDataSetObserver(dataSetObserver);
//                            setAdapter(adapter);
//                            d.dismiss();
//                            }else {
//                                Toast.makeText(getContext(),"Righ must bigger than Left and \n Bottom must bigger than Top",Toast.LENGTH_SHORT).show();
//                            }
//                        }catch (Exception e){
//                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//                cancelButton.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        d.dismiss();
//                    }
//                });
//                d.show();
//        return true;
//    }
}


