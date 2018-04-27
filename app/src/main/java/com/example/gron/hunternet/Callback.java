package com.example.gron.hunternet;

// TODO коллбек - это то, у чего обычно методы вида onSomething
public interface Callback {
    void applyDataToActivity();
    void finishSaveImageProfile();
    void showProgressLoad();
    void hideProgressLoad();
}
