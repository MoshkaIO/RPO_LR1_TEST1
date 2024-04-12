package com.example.rpo_lr1_test1;

interface TransactionEvents {
    String enterPin(int ptc, String amount);
    void transactionResult(boolean result);
}