package com.example.triviafest.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.triviafest.controllers.AppController;
import com.example.triviafest.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    private List<Question> questionList = new ArrayList<>();
    private final String URL = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Question> getQuestionList(final QuestionListAsyncResponse callback) {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++) {
                    try {
                        JSONArray jsonArray = response.getJSONArray(i);
                        Question question = new Question(jsonArray.getString(0), jsonArray.getBoolean(1));
                        questionList.add(question);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(callback != null) {
                    callback.processFinished(questionList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSONSTUFF", "onError: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(arrayRequest);
        return questionList;
    }
}
