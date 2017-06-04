package com.example.george.dictionary;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by George on 2017/6/3.
 */

public class DicPage extends Fragment {
    //输入框
    private EditText dic_input;
    //搜索按钮
    private Button dic_search;
    //基本信息布局
    private LinearLayout dic_information;
    //要查询的内容
    private TextView dic_basic;
    //发音按钮
    private Button dic_pron;
    //音标
    private TextView dic_phonetic;
    //释义
    private TextView dic_interpre;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dictionary, null);
        dic_input = (EditText) view.findViewById(R.id.dic_input);
        dic_search = (Button) view.findViewById(R.id.dic_search);
        dic_information = (LinearLayout) view.findViewById(R.id.dic_information);
        dic_basic = (TextView) view.findViewById(R.id.dic_basic);
        dic_pron = (Button) view.findViewById(R.id.dic_pron);
        dic_phonetic = (TextView) view.findViewById(R.id.dic_phonetic);
        dic_interpre = (TextView) view.findViewById(R.id.dic_interpre);

        //查询之前基本信息不可见
        dic_information.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = dic_input.getText().toString().trim();

                try {
                    word = URLEncoder.encode(word, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (null != word && !"".equals(word)) {
                    String contans = "http://v.juhe.cn/xhzd/query?key=2dfdd9bc5b46fc8b6aa7bdfd682dd80d&word=" + word;

                    //执行网络请求
                    RxVolley.get(contans, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            Loger.debug("请求的数据：" + t);
                            parseJson(t);
                        }

                        private void parseJson(String json) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                String basic = null;
                                String phonetic = null;
                                String interpre = null;

                                if (jsonObject.getInt("error_code") == 0) {
                                    if (jsonObject.has("result")) {
                                        JSONObject resultObject = jsonObject.getJSONObject("result");

                                        if(resultObject.has("zi")) {
                                            basic = resultObject.getString("zi");
                                        }

                                        if(resultObject.has("pinyin")) {
                                            phonetic = resultObject.getString("pinyin");
                                        }

                                        if (resultObject.has("jijie")) {
                                            JSONArray jijieArr = resultObject.getJSONArray("jijie");
                                            interpre = jijieArr.getString(2);
                                            for (int i = 3; i < jijieArr.length()-4; i ++) {
                                                interpre += "\n" + jijieArr.getString(i);
                                            }
                                        }
                                    }
                                }

                                dic_information.setVisibility(View.VISIBLE);
                                //清空信息
                                dic_basic.setText("");
                                dic_phonetic.setText("");
                                dic_interpre.setText("");

                                dic_basic.setText(basic);
                                dic_phonetic.setText(phonetic);
                                dic_interpre.setText(interpre);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
