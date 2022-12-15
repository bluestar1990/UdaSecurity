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
        Instant deadline = clock.instant().plus(timeout);
        ConcurrentMap<String, Integer> counts = new ConcurrentHashMap<>();
        ConcurrentSkipListSet<String> visitedUrls = new ConcurrentSkipListSet<>();
        for (String url : startingUrls) {
            pool.invoke(new CrawlInternal(url, deadline, maxDepth, counts, visitedUrls));
        }


        Map<String, Integer> sortWord = (counts.isEmpty()) ? counts : WordCounts.sort(counts, popularWordCount);
        return new CrawlResult.Builder()
                .setWordCounts(sortWord)
                .setUrlsVisited(visitedUrls.size())
                .build();
    }

    @Override
    public int getMaxParallelism() {
        return Runtime.getRuntime().availableProcessors();
    }


    class CrawlInternal extends RecursiveTask<Boolean> {
        private String url;
        private Instant deadline;
        private int maxDepth;
        private ConcurrentMap<String, Integer> counts;
        private ConcurrentSkipListSet<String> visitedUrls;

        public CrawlInternal(String url, Instant deadline, int maxDepth, ConcurrentMap<String, Integer> counts, ConcurrentSkipListSet<String> visitedUrls) {
            this.url = url;
            this.deadline = deadline;
            this.maxDepth = maxDepth;
            this.counts = counts;
            this.visitedUrls = visitedUrls;
        }

        @Override
        protected Boolean compute() {
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

            for (ConcurrentMap.Entry<String, Integer> c : result.getWordCounts().entrySet()) {
                counts.compute(c.getKey(), (k, v) -> (Objects.isNull(v)) ? c.getValue() : c.getValue() + v);
            }
            List<CrawlInternal> subtasks = new ArrayList<>();
            for (String link : result.getLinks()) {
                subtasks.add(new CrawlInternal(link, deadline, maxDepth - 1, counts, visitedUrls));
            }
            invokeAll(subtasks);
            return true;
        }
    }
}
