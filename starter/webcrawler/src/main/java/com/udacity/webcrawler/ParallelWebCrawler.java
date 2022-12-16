package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.time.Instant;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler {
    private final Clock clock;
    private final Duration timeout;
    private final int popularWordCount;
    private final ForkJoinPool pool;
    private final PageParserFactory parserFactory;
    private final int maxDepth;
    private final List<Pattern> ignoredUrls;

    @Inject
    ParallelWebCrawler(Clock clock, @Timeout Duration timeout,
                       @PopularWordCount int popularWordCount,
                       @TargetParallelism int threadCount,
                       PageParserFactory parserFactory,
                       @MaxDepth int maxDepth,
                       @IgnoredUrls List<Pattern> ignoredUrls) {
        this.clock = clock;
        this.timeout = timeout;
        this.popularWordCount = popularWordCount;
        this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
        this.parserFactory = parserFactory;
        this.maxDepth = maxDepth;
        this.ignoredUrls = ignoredUrls;
    }

    @Override
    public CrawlResult crawl(List<String> startingUrls) {
        // This is my code . It is copied from SequentialWebCrawler
        Instant deadline = clock.instant().plus(timeout);
        ConcurrentMap<String, Integer> counts = new ConcurrentHashMap<>();
        ConcurrentSkipListSet<String> visitedUrls = new ConcurrentSkipListSet<>();
        // This is my code . It is the same with SequentialWebCrawler
        for (String url : startingUrls) {
            pool.invoke(new CrawlInternalTask(url, deadline, maxDepth, counts, visitedUrls));
        }

        // This is my code . It is copied from SequentialWebCrawler
        if (counts.isEmpty()) {
            return new CrawlResult.Builder()
                    .setWordCounts(counts)
                    .setUrlsVisited(visitedUrls.size())
                    .build();
        }

        return new CrawlResult.Builder()
                .setWordCounts(WordCounts.sort(counts, popularWordCount))
                .setUrlsVisited(visitedUrls.size())
                .build();
    }

    @Override
    public int getMaxParallelism() {
        return Runtime.getRuntime().availableProcessors();
    }

    // This is my code . It is the same with SequentialWebCrawler
    public final class CrawlInternalTask extends RecursiveTask<Boolean> {
        private String url;
        private Instant deadline;
        private int maxDepth;
        private ConcurrentMap<String, Integer> counts;
        private ConcurrentSkipListSet<String> visitedUrls;

        public CrawlInternalTask(String url, Instant deadline, int maxDepth, ConcurrentMap<String, Integer> counts, ConcurrentSkipListSet<String> visitedUrls) {
            this.url = url;
            this.deadline = deadline;
            this.maxDepth = maxDepth;
            this.counts = counts;
            this.visitedUrls = visitedUrls;
        }

        @Override
        protected Boolean compute() {
            // This is my code . It is copied from SequentialWebCrawler
            if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
                return false;
            }
            for (Pattern pattern : ignoredUrls) {
                if (pattern.matcher(url).matches()) {
                    return false;
                }
            }
            if (visitedUrls.contains(url)) {
                return false;
            }
            visitedUrls.add(url);
            PageParser.Result result = parserFactory.get(url).parse();
            List<CrawlInternalTask> subtasks = new ArrayList<>();
            // This is my code . It is copied from SequentialWebCrawler
            for (Map.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
                counts.put(e.getKey(), counts.containsKey(e.getKey()) ? e.getValue() + counts.get(e.getKey()) : e.getValue());
            }
            for (String link : result.getLinks()) {
                subtasks.add(new CrawlInternalTask(link, deadline, maxDepth - 1, counts, visitedUrls));
            }
            invokeAll(subtasks);
            return true;
        }
    }
}
