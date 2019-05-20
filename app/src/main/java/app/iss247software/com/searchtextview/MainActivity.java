package app.iss247software.com.searchtextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String TAG = "SearchTextView";

    private EditText edtxtSearchText;
    private Button btnSearch;
    private ScrollView scrollView;
    private TextView textView;
    private Button btnClear, btnPrevious, btnNext;

    private String dataString = "", searchString = "";
    private LinkedHashMap<Integer, Integer> searchMap = new LinkedHashMap<>();
    private int searchCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataString = getResources().getString(R.string.data);
        initView();
    }

    private void initView() {
        edtxtSearchText = (EditText) findViewById(R.id.edtxtSearchText);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textView = (TextView) findViewById(R.id.textView);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    onSearchBtnClick();
                }
            }
        });

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearch();
            }
        });

        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrevious();
            }
        });

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNext();
            }
        });
    }

    private boolean validate() {
        searchString = edtxtSearchText.getText().toString();
        return (!TextUtils.isEmpty(searchString));
    }

    private void onSearchBtnClick() {
        resetSearch();
        prepareSearchIndexMap();
    }

    private void prepareSearchIndexMap() {
        Pattern searchPattern = Pattern.compile(searchString);
        Matcher matcher = searchPattern.matcher(dataString);
        int _counter = 1;
        while (matcher.find()) {
            searchMap.put(_counter, matcher.start());
            _counter++;
        }

        if (searchMap.size() > 0) {
            highlightAllSearchedText();
        }
    }

    private void highlightAllSearchedText() {
        String searchStringHighLighted = getFormattedText(searchString);
        String dataStringHighLighted = dataString.replace(searchString, searchStringHighLighted);
        textView.setText(Html.fromHtml(dataStringHighLighted));
    }

    private void highLightSpecific(int start, int end) {
        String subStr1 = dataString.substring(0, start);
        String subStr2 = dataString.substring(end);
        final int lineNumber = textView.getLayout().getLineForOffset(start);
        String dataStringHighLighted = subStr1 + getFormattedText(searchString) + subStr2;
        textView.setText(Html.fromHtml(dataStringHighLighted));
        ViewTreeObserver observer = textView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.scrollTo(0, textView.getLayout().getLineTop(lineNumber));
            }
        });
//        scrollView.scrollTo(0, textView.getLayout().getLineTop(lineNumber));
    }

    private String getFormattedText(String str) {
        return "<font color='red'>" + str + "</font>";
    }

    private void clearSearch() {
        edtxtSearchText.setText("");
        textView.setText(dataString);
        searchMap.clear();
    }

    private void resetSearch() {
        searchCounter = 0;
        textView.setText(dataString);
        searchMap.clear();
    }

    private void showPrevious() {
        if (searchMap.size() > 0) {
            searchCounter--;
            if (searchCounter <= 0) {
                searchCounter = 1;
            }
            findHighLightIndex();
        }
    }

    private void showNext() {
        if (searchMap.size() > 0) {
            searchCounter++;
            if (searchCounter >= searchMap.size()) {
                searchCounter = searchMap.size();
            }
            findHighLightIndex();
        }
    }

    private void findHighLightIndex() {
        int startIndex = searchMap.get(searchCounter);
        highLightSpecific(startIndex, (searchString.length() + startIndex));
    }
}
