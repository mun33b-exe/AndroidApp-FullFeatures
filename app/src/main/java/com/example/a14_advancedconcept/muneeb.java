package com.example.advancedconcept;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class muneeb extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvStatus;
    private Button btnAsyncTask, btnHandler, btnThreadPool;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private boolean isTaskRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muneeb);

        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
        btnAsyncTask = findViewById(R.id.btnAsyncTask);
        btnHandler = findViewById(R.id.btnHandler);
        btnThreadPool = findViewById(R.id.btnThreadPool);

        btnAsyncTask.setOnClickListener(v -> startAsyncTask());
        btnHandler.setOnClickListener(v -> startHandlerThread());
        btnThreadPool.setOnClickListener(v -> startThreadPoolTask());
    }

    // ================== AsyncTask Example ==================
    private void startAsyncTask() {
        if (isTaskRunning) {
            Toast.makeText(this, "Task already running", Toast.LENGTH_SHORT).show();
            return;
        }
        new MyAsyncTask().execute();
    }

    private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            isTaskRunning = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 1; i <= 100; i++) {
                if (isCancelled()) break;
                publishProgress(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            tvStatus.setText(getString(R.string.task_running, values[0]));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tvStatus.setText(R.string.task_completed);
            isTaskRunning = false;
        }

        @Override
        protected void onCancelled() {
            tvStatus.setText(R.string.task_cancelled);
            isTaskRunning = false;
        }
    }

    // ================== Handler + Thread Example ==================
    private void startHandlerThread() {
        if (isTaskRunning) {
            Toast.makeText(this, "Task already running", Toast.LENGTH_SHORT).show();
            return;
        }
        isTaskRunning = true;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                if (!isTaskRunning) break;
                final int progress = i;
                mainHandler.post(() -> {
                    progressBar.setProgress(progress);
                    tvStatus.setText(getString(R.string.task_running, progress));
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mainHandler.post(() -> {
                tvStatus.setText(R.string.task_completed);
                isTaskRunning = false;
            });
        }).start();
    }

    // ================== ThreadPool Example ==================
    private void startThreadPoolTask() {
        if (isTaskRunning) {
            Toast.makeText(this, "Task already running", Toast.LENGTH_SHORT).show();
            return;
        }
        isTaskRunning = true;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        threadPool.execute(() -> {
            for (int i = 1; i <= 100; i++) {
                if (!isTaskRunning) break;
                final int progress = i;
                runOnUiThread(() -> {
                    progressBar.setProgress(progress);
                    tvStatus.setText(getString(R.string.task_running, progress));
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> {
                tvStatus.setText(R.string.task_completed);
                isTaskRunning = false;
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTaskRunning = false;
        threadPool.shutdown();
    }
}