package com.example.gpt_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.os.AsyncTask;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPrompt;
    private TextView textViewOutput;
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPrompt = findViewById(R.id.editTextPrompt);
        textViewOutput = findViewById(R.id.textViewOutput);
    }

    public void onGenerateClicked(View view) {
        String prompt = editTextPrompt.getText().toString().trim();
        if (!prompt.isEmpty()) {
            new OpenAIAsyncTask().execute(prompt);
        }
    }

    private class OpenAIAsyncTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String prompt = params[0];
            String responseText = "";

            try {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"model\": \"text-davinci-003\", \"prompt\": \"" + prompt + "\", \"max_tokens\": 150}");
                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer "+API_KEY)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    responseText = response.body().string();
                } else {
                    responseText = "Failed to fetch response: " + response.message();
                }
            } catch (IOException e) {
                responseText = "Error occurred: " + e.getMessage();
            }

            return responseText;
        }

        protected void onPostExecute(String result) {
            textViewOutput.setText(result);
        }
    }
}