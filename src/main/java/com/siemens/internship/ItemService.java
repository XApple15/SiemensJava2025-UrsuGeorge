package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Problems in the original code:
     * <p>
     * 1.Lack of thread safety :
     * The processedItems list and processedCount variable were shared mutable state accessed by multiple
     * threads without synchronization, which leads to race conditions and unpredictable behavior.
     * <p>
     * 2. Incorrect completion logic :
     * The method returned CompletableFuture.completedFuture(processedItems) immediately after scheduling tasks,
     * without waiting for them to finish — meaning the list was likely incomplete.
     * <p>
     * 3 . No proper error handling:
     * Exceptions inside runAsync tasks were only printed (System.out.println) and not propagated, so failures would
     * silently go unnoticed by the calling code.
     * <p>
     * 5. Improper use of @Async and manual async :
     * The use of Spring's @Async on top of manual CompletableFuture.runAsync(...) with a custom executor was
     * redundant and potentially confusing, since both control async execution.
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {

        List<Long> itemIds = itemRepository.findAllIds();

        // used a thread-safe collection to avoid race conditions during concurrent writes
        List<Item> processedItems = new CopyOnWriteArrayList<>();

        // created a list of CompletableFutures, each processing one item
        List<CompletableFuture<Void>> futures = itemIds.stream()
                .map(id -> CompletableFuture.runAsync(() -> {
                    try {
                        // retrieve the item and fail fast if not found
                        Item item = itemRepository.findById(id).orElseThrow(() ->
                                new RuntimeException("Item not found"));

                        item.setStatus("PROCESSED");
                        itemRepository.save(item);

                        processedItems.add(item);
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing item");
                    }
                }, executor)).collect(Collectors.toUnmodifiableList());


        // combined all futures and ensure we wait for all processing to complete
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> processedItems)// only complete when all items are processed
                .exceptionally(exception -> { // propagate any exception that occurred during processing
                    throw new CompletionException("Error while processing", exception);
                });

    }

}

