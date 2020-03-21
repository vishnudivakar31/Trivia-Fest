package com.example.triviafest.data;

import com.example.triviafest.model.Question;

import java.util.List;

public interface QuestionListAsyncResponse {
    void processFinished(List<Question> questions);
}
