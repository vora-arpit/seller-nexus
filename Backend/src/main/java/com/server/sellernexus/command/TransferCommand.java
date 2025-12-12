package com.server.sellernexus.command;

public interface TransferCommand {
    TransferResult execute();
    void undo();
}
