/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.cli.interactive.completers;

import jline.console.completer.Completer;

import java.util.*;

import static jline.internal.Preconditions.checkNotNull;

/**
 * Copied from jline AggregateCompleter
 *
 * sorts aggregated completions
 *
 */
public class SortedAggregateCompleter
    implements Completer
{
    private final List<Completer> completers = new ArrayList<Completer>();

    public SortedAggregateCompleter() {
        // empty
    }

    /**
     * Construct an AggregateCompleter with the given collection of completers.
     * The completers will be used in the iteration order of the collection.
     *
     * @param completers the collection of completers
     */
    public SortedAggregateCompleter(final Collection<Completer> completers) {
        checkNotNull(completers);
        this.completers.addAll(completers);
    }

    /**
     * Construct an AggregateCompleter with the given completers.
     * The completers will be used in the order given.
     *
     * @param completers the completers
     */
    public SortedAggregateCompleter(final Completer... completers) {
        this(Arrays.asList(completers));
    }

    /**
     * Retrieve the collection of completers currently being aggregated.
     *
     * @return the aggregated completers
     */
    public Collection<Completer> getCompleters() {
        return completers;
    }

    /**
     * Perform a completion operation across all aggregated completers.
     *
     * @see Completer#complete(String, int, java.util.List)
     * @return the highest completion return value from all completers
     */
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        // buffer could be null
        checkNotNull(candidates);

        List<Completion> completions = new ArrayList<Completion>(completers.size());

        // Run each completer, saving its completion results
        int max = -1;
        for (Completer completer : completers) {
            Completion completion = new Completion(candidates);
            completion.complete(completer, buffer, cursor);

            // Compute the max cursor position
            max = Math.max(max, completion.cursor);

            completions.add(completion);
        }

        SortedSet<CharSequence> allCandidates = new TreeSet<>();

        // Append candidates from completions which have the same cursor position as max
        for (Completion completion : completions) {
            if (completion.cursor == max) {
                allCandidates.addAll(completion.candidates);
            }
        }

        candidates.addAll(allCandidates);

        return max;
    }

    /**
     * @return a string representing the aggregated completers
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "completers=" + completers +
            '}';
    }

    private class Completion
    {
        public final List<CharSequence> candidates;

        public int cursor;

        public Completion(final List<CharSequence> candidates) {
            checkNotNull(candidates);
            this.candidates = new LinkedList<CharSequence>(candidates);
        }

        public void complete(final Completer completer, final String buffer, final int cursor) {
            checkNotNull(completer);
            this.cursor = completer.complete(buffer, cursor, candidates);
        }
    }
}
