package com.server.sellernexus.command;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class BulkTransferExecutor {
    private final ExecutorService executorService;
    private final List<TransferCommand> commands;

    public BulkTransferExecutor() {
        this.executorService = Executors.newFixedThreadPool(5);
        this.commands = new ArrayList<>();
    }

    public void addCommand(TransferCommand command) {
        commands.add(command);
    }

    public List<TransferResult> executeAll() {
        List<TransferResult> results = new ArrayList<>();
        for (TransferCommand command : commands) {
            results.add(command.execute());
        }
        commands.clear();
        return results;
    }

    public CompletableFuture<List<TransferResult>> executeParallel() {
        System.out.println("[BulkTransferExecutor] Starting parallel execution of " + commands.size() + " commands");
        
        List<CompletableFuture<TransferResult>> futures = commands.stream()
            .map(command -> CompletableFuture.supplyAsync(command::execute, executorService))
            .collect(Collectors.toList());

        commands.clear();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
