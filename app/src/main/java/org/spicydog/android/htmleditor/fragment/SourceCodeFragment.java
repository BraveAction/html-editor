package org.spicydog.android.htmleditor.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import org.spicydog.android.htmleditor.utility.FileUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.spicydog.android.htmleditor.R;

import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.nio.charset.Charset;

@SuppressLint("ValidFragment")
public class SourceCodeFragment extends Fragment {

    Context mContext;

    private static final String FILENAME_SOURCE_CODE = "last_source_code";
    private static final String FILENAME_URL = "last_url";


    private EditText etURL;
    private EditText etSourceCode;
    private Button btRequest;
    private ProgressDialog pdRequesting;

    public SourceCodeFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_source_code, container, false);

        initViews(view);
        initListeners();

        return view;

    }

    private void initViews(View view) {
        etURL = (EditText) view.findViewById(R.id.et_url);
        etSourceCode = (EditText) view.findViewById(R.id.et_source_code);
        btRequest = (Button) view.findViewById(R.id.bt_request);

        String lastURL = FileUtils.loadFile(getActivity(),FILENAME_URL,mContext.getResources().getString(R.string.default_url));
        String lastSourceCode = FileUtils.loadFile(getActivity(),FILENAME_SOURCE_CODE,"");

        etSourceCode.setText(lastSourceCode);
        etURL.setText(lastURL);
    }

    private void initListeners() {
        btRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etURL.getText().toString();
                requestHTMLFromURL(url, etSourceCode);
            }
        });

        etURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                FileUtils.saveFile(getActivity(),FILENAME_URL,etURL.getText().toString());
            }
        });

        etSourceCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                FileUtils.saveFile(getActivity(),FILENAME_SOURCE_CODE,etSourceCode.getText().toString());
            }
        });
    }

    private void requestHTMLFromURL(final String url, final EditText sourceViewer) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                pdRequesting = new ProgressDialog(getActivity());
                pdRequesting.setMessage(getResources().getString(R.string.loading));
                pdRequesting.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"


                final String sourceCode = new String(response, Charset.forName("UTF8"));
                sourceViewer.setText(sourceCode);

                if(pdRequesting!=null && pdRequesting.isShowing()) {
                    pdRequesting.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                if(pdRequesting!=null && pdRequesting.isShowing()) {
                    pdRequesting.dismiss();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public String getSourceCode() {
        return etSourceCode!=null ? etSourceCode.getText().toString() : "";
    }

    public void setSourceCode(String sourceCode) {
        if(etSourceCode!=null) {
            etURL.setText("");
            etSourceCode.setText(sourceCode);
        } else {
            FileUtils.saveFile(mContext,FILENAME_URL,"");
            FileUtils.saveFile(mContext,FILENAME_SOURCE_CODE,sourceCode);
        }
    }

    public void setURL(String url) {
        if(etSourceCode!=null) {
            etURL.setText(url);
            etSourceCode.setText("");
        } else {
            FileUtils.saveFile(mContext,FILENAME_URL,url);
            FileUtils.saveFile(mContext,FILENAME_SOURCE_CODE,"");
        }
    }

}
