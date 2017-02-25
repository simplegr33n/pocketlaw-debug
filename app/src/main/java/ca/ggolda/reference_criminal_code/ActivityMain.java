package ca.ggolda.reference_criminal_code;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by gcgol on 01/18/2017.
 */

public class ActivityMain extends AppCompatActivity {

    private ListView mListViewSections;
    private AdapterSection mAdapterSection;

    private ListView mListViewHeadings;
    private AdapterHeading mAdapterHeading;

    private ListView mListViewQuery;
    private AdapterQuery mAdapterQuery;

    private ImageView mBtnSearch;
    private ImageView mBtnSearchOpen;
    private EditText mEdtSearch;

    private LinearLayout loadCover;

    private ImageView mBtnParts;
    public static LinearLayout mParts;

    private RelativeLayout layoutSearchbar;

    DbHelper dbHelper;

    public static int partsVisible = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DbHelper.getInstance(getApplicationContext());

        mAdapterSection = new AdapterSection(ActivityMain.this, R.layout.card_section, dbHelper.getAllSection());
        mListViewSections = (ListView) findViewById(R.id.listview_section);
        mListViewSections.setAdapter(mAdapterSection);

        mAdapterHeading = new AdapterHeading(ActivityMain.this, R.layout.card_heading, dbHelper.getAllHeading());
        mListViewHeadings = (ListView) findViewById(R.id.listview_heading);
        mListViewHeadings.setAdapter(mAdapterHeading);

        mListViewQuery = (ListView) findViewById(R.id.listview_query);

        mBtnParts = (ImageView) findViewById(R.id.btn_parts);
        mParts = (LinearLayout) findViewById(R.id.parts);

        layoutSearchbar = (RelativeLayout) findViewById(R.id.lyt_search);
        mBtnSearchOpen = (ImageView) findViewById(R.id.btn_search_open);
        mBtnSearch = (ImageView) findViewById(R.id.btn_search);
        mEdtSearch = (EditText) findViewById(R.id.edt_search);
        loadCover = (LinearLayout) findViewById(R.id.load_cover);


        // bring parts up or down
        mBtnParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                partsHideShow();

            }
        });

        // bring searchbar up
        mBtnSearchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutSearchbar.setVisibility(View.VISIBLE);

                // Kinda hackish, get keyboard and edittext focus onclick
                mEdtSearch.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mEdtSearch, 0);
            }
        });


//
//        mEdtSearch.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
//                //If the keyevent is a key-down event on the "enter" button
//                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//
//
//
//                    return true;
//                }
//                return false;
//            }
//        });

        // Search on enter press
        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    hideSoftKeyboard(ActivityMain.this);

                    actionSearch();

                    return true;
                }
                return false;
            }
        });



        // Search on search button click
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(ActivityMain.this);

                actionSearch();

            }
        });


        // If the no sections in database, import via ActivityImport
        // TODO: figure out why activity main must be accessed before the import func in ActivityImport / ActivityDebug work
        if (dbHelper.getAllSection().size() < 1) {
            Intent intent = new Intent(ActivityMain.this, ActivityImport.class);
            startActivity(intent);
        } else {

            //TODO: fix, still loading via white screen
            // TODO: maybe remove loadCover, vis already set gone in XML as it doesn't seem to work
            loadCover.setVisibility(View.GONE);
        }

    }


    private void actionSearch() {
        layoutSearchbar.setVisibility(View.GONE);
        String query = mEdtSearch.getText().toString();

        if (!query.equals("")) {
            mListViewQuery.setVisibility(View.VISIBLE);

            mAdapterQuery = new AdapterQuery(ActivityMain.this, R.layout.card_query, dbHelper.getSearchResults(query));
            mListViewQuery.setAdapter(mAdapterQuery);
        }

        mEdtSearch.setText("");

        // Hide headings listview (inside mParts linearlayout) if it's up
        mParts.setVisibility(View.GONE);
    }

    public static void partsHideShow() {
        if (partsVisible == 0) {
            mParts.setVisibility(View.VISIBLE);
            partsVisible = 1;
        } else if (partsVisible == 1) {
            mParts.setVisibility(View.GONE);
            partsVisible = 0;
        }
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    // TODO: determine why this is here....
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }



}