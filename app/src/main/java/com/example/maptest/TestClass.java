package com.example.maptest;

import android.os.AsyncTask;

public class TestClass {
    private String[] test;

    public TestClass() {            // 생성자함수

    }

    public String[] parsing(String num) {
        TaskClass task = new TaskClass();
        try {
            if (task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
        } catch (Exception e) {
        }
        task.execute(num);
        try {
            test = task.get();              //task 끝나면 결과값받기 끝날때까지 대기

            if (task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return test;
    }
}