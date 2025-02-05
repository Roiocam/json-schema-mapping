/* (C)2025 */
package io.github.roiocam.jsm.tools;

public abstract class GrandParent<T> {

    private T balance;

    public T getBalance() {
        return balance;
    }

    public void setBalance(T balance) {
        this.balance = balance;
    }
}
