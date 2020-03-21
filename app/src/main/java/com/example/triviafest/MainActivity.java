package com.example.triviafest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviafest.data.QuestionBank;
import com.example.triviafest.data.QuestionListAsyncResponse;
import com.example.triviafest.model.Question;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    enum IndexOps {
        INCR, DECR
    };

    private TextView questionView, questionNoView, scoreView, highScoreView;
    private Button trueBtn, falseBtn, resetBtn, shareBtn;
    private ImageButton prevBtn, nxtBtn;
    private CardView cardView;
    private int currentIndex = 0, score, highScore;
    private List<Question> questionList;
    private static String PREF_ID = "TRIVIAFEST_ID", SCORE = "SCORE", HIGHSCORE = "HIGHSCORE", CURRENTQUE = "CURRENTQUES";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

        questionView = findViewById(R.id.questionView);
        questionNoView = findViewById(R.id.quesNoView);
        trueBtn = findViewById(R.id.trueBtn);
        falseBtn = findViewById(R.id.falseBtn);
        prevBtn = findViewById(R.id.prevBtn);
        nxtBtn = findViewById(R.id.nxtBtn);
        shareBtn = findViewById(R.id.shareBtn);
        cardView = findViewById(R.id.cardView);
        scoreView = findViewById(R.id.scoreView);
        highScoreView = findViewById(R.id.highScoreView);
        resetBtn = findViewById(R.id.resetBtn);
        sharedPreferences = getSharedPreferences(PREF_ID, MODE_PRIVATE);

        prevBtn.setOnClickListener(this);
        nxtBtn.setOnClickListener(this);
        trueBtn.setOnClickListener(this);
        falseBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);

        questionList = new QuestionBank().getQuestionList(new QuestionListAsyncResponse() {
            @Override
            public void processFinished(List<Question> questions) {
                questionNoView.setText((currentIndex + 1) + "/" + questions.size());
                questionView.setText(questions.get(currentIndex).getQuestion());
            }
        });

        updateScoreView();
    }

    private void updateScoreView() {
        highScore = sharedPreferences.getInt(HIGHSCORE, 0);
        if(score > highScore) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(HIGHSCORE, score);
            editor.apply();
            highScore = score;
        }
        scoreView.setText(getText(R.string.score_text) + " " + String.valueOf(score));
        highScoreView.setText(getText(R.string.highscore) + " " + String.valueOf(highScore));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prevBtn:
                updateIndex(IndexOps.DECR);
                updateQuestion();
                break;
            case R.id.nxtBtn:
                updateIndex(IndexOps.INCR);
                updateQuestion();
                break;
            case R.id.trueBtn:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.falseBtn:
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.resetBtn:
                resetGame();
                break;
            case R.id.shareBtn:
                sendGameStatus();
                break;
        }
    }

    private void sendGameStatus() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_SUBJECT, "I am playing Trivia");
        intent.putExtra(Intent.EXTRA_TEXT, String.format("My current score is %d, and my highest " +
                "score is %d.", score, highScore));

        startActivity(intent);
    }

    private void resetGame() {
        score = 0;
        highScore = 0;
        currentIndex = 0;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SCORE, score);
        editor.putInt(HIGHSCORE, highScore);
        editor.putInt(CURRENTQUE, currentIndex);
        editor.apply();
        updateQuestion();
        updateScoreView();
    }

    private void checkAnswer(boolean answer) {
        int toastMsgId;
        if(answer == questionList.get(currentIndex).isAnswerTrue()) {
            toastMsgId = R.string.right_answer;
            fadeAnimation();
            score = (score + 1) % questionList.size();
        } else {
            toastMsgId = R.string.wrong_answer;
            shakeAnimation();
            score--;
        }
        Toast.makeText(this, toastMsgId, Toast.LENGTH_SHORT).show();
        updateScoreView();
    }

    private void updateIndex(IndexOps ops) {
        if(ops == IndexOps.INCR) {
            currentIndex = (currentIndex + 1) % questionList.size();
        } else {
            currentIndex = (currentIndex - 1) < 0 ? 0 : (currentIndex - 1);
        }
    }

    private void updateQuestion() {
        questionView.setText(questionList.get(currentIndex).getQuestion());
        questionNoView.setText((currentIndex + 1) + "/" + questionList.size());
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                updateIndex(IndexOps.INCR);
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveScore();
    }

    private void saveScore() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HIGHSCORE, highScore);
        editor.putInt(SCORE, score);
        editor.putInt(CURRENTQUE, currentIndex);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retriveScore();
        currentIndex = sharedPreferences.getInt(CURRENTQUE, 0);
    }

    private void retriveScore() {
        score = sharedPreferences.getInt(SCORE, 0);
        highScore = sharedPreferences.getInt(HIGHSCORE, 0);
        updateScoreView();
    }

}
