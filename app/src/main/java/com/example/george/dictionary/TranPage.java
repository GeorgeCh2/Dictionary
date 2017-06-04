package com.example.george.dictionary;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.toolbox.Loger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by George on 2017/6/3.
 */

public class TranPage extends Fragment implements View.OnClickListener {
    //输入框
    private EditText tran_input;
    //搜索按钮
    private Button tran_search;
    //基本信息布局
    private LinearLayout tran_information;
    //要查询的内容
    private TextView tran_basic;
    //发音按钮
    private Button tran_pron;
    //音标
    private TextView tran_phonetic;
    //释义
    private TextView tran_interpre;

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translation, null);
        tran_input = (EditText) view.findViewById(R.id.tran_input);
        tran_search = (Button) view.findViewById(R.id.tran_search);
        tran_information = (LinearLayout) view.findViewById(R.id.tran_information);
        tran_basic = (TextView) view.findViewById(R.id.tran_basic);
        tran_pron = (Button) view.findViewById(R.id.tran_pron);
        tran_phonetic = (TextView) view.findViewById(R.id.tran_phonetic);
        tran_interpre = (TextView) view.findViewById(R.id.tran_interpre);

        //查询之前基本信息不可见
        tran_information.setVisibility(View.GONE);

        mediaPlayer = new MediaPlayer();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //查询按钮点击事件
        tran_search.setOnClickListener(this);

        //发音按钮点击事件
        tran_pron.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tran_search:
                search();
                break;
            case R.id.tran_pron:
                try {
                    player();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    private void player() throws IOException {
        String word = tran_input.getText().toString().trim();
        final String contain = "http://dict.youdao.com/dictvoice?audio=" + word;

        mediaPlayer.setDataSource(getActivity(), Uri.parse(contain));
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
            }
        });
    }

    private void search() {
        String word = tran_input.getText().toString().trim();
        try {
            word = URLEncoder.encode(word, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (null != word && !"".equals(word)) {
            //拼接URL
            final String contants = "http://fanyi.youdao.com/openapi.do?keyfrom=Georgechou&key=1912076918&type=data&doctype=json&version=1.1&q=" + word;

            //执行网络请求
            RxVolley.get(contants, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    Loger.debug("请求的数据：" + t);
                    parseJson(t);
                }
            });
        }
    }

    //解析json数据
    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String basic = null;
            String phonetic = null;
            String interpre = null;

            if (jsonObject.getInt("errorCode") == 0) {
                basic = jsonObject.getString("query");

                if (jsonObject.has("basic")) {
                    JSONObject basicObject = jsonObject.getJSONObject("basic");

                    if (basicObject.has("phonetic")) {
                        phonetic = basicObject.getString("phonetic");
                    }

                    if (basicObject.has("explains")) {
                        JSONArray explainArr = basicObject.getJSONArray("explains");
                        interpre = explainArr.getString(0);
                        for (int i = 1; i < explainArr.length(); i++) {
                            interpre += "\n" + explainArr.getString(i);
                        }
                    }
                }
            }
            //使基本信息布局可见
            tran_information.setVisibility(View.VISIBLE);
            //清空信息
            tran_basic.setText("");
            tran_phonetic.setText("");
            tran_interpre.setText("");

            tran_basic.setText(basic);
            tran_phonetic.setText(phonetic);
            tran_interpre.setText(interpre);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
