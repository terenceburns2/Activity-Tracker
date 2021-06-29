package com.example.activitytracker.service;

public interface ICallBack {

    void updateTime(double elapsedTime);

    void updateDistance(double distanceTravelled);

    void updatePace(double pace);

    void storeMilePace(double pace);
}
