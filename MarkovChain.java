import java.util.*;

/**
 * A Markov Chain is a data structure that tracks the frequency with which one
 * value follows another value in sequence. This project uses a MarkovChain to
 * model tweets by gathering the frequency information from a Twitter feed. We
 * can use the MarkovChain to generate "plausible" tweets by conducting a random
 * walk through the chain according to the frequencies. Please see the homework
 * instructions for more information on Markov Chains.
 * <p>
 * TRAINING:
 * <p>
 * An example: Suppose we train the MarkovChain on these two Strings that
 * represent tweets: "a table" and "A banana? A banana!"
 * <p>
 * We first "clean up" the tweets and parse them into individual sentences to
 * use as training data. This process will remove punctuation and put all words
 * into lower case, yielding these three sentences (written using OCaml list
 * notation):
 * <p>
 * [ ["a"; "table"]; ["a"; "banana"]; ["a"; "banana"] ]
 * <p>
 * The MarkovChain that results from this training data maps each observed
 * string to a ProbabilityDistribution that is based on the recorded occurrences
 * of bigrams (adjacent words) in the data:
 * <p>
 * - "a" maps to "table":1, "banana":2
 * <p>
 * - "table" maps to null:1
 * <p>
 * - "banana" maps to null:2
 * <p>
 * "a" is followed by "table" one time and "banana" twice, "table" is the end of
 * a sentence once, and "banana" is the end of a sentence twice. NOTE: we use
 * null to mark the end of a sentence.
 * <p>
 * The MarkovChain also records a ProbabilityDistribution that contains the
 * frequencies with which words start any sentence. In this case, that
 * startWords data will just say that "a" started 3 sentences.
 * <p>
 * GENERATING A TWEET:
 * <p>
 * Once we have trained the Markov model, we can use it to generate a tweet.
 * Given a desired length of tweet (in characters), we repeatedly generate
 * sentences until the tweet is long enough.
 * <p>
 * To generate a sentence, we treat the MarkovChain as an iterator that
 * maintains state about the current word (i.e. the one that will be generated
 * by next()).
 * <p>
 * - the reset() method picks (at random) one of the startWords to be the
 * current word. We use reset() to start a new sentence.
 * <p>
 * - the next() method picks (at random) a successor of the current word
 * according to the current word's probability distribution. That successor will
 * be the new "current" word after the current one is returned by next().
 * <p>
 * In the example above, reset() sets the current word to "a" (the only choice
 * offered by startWord). Then: next(); // yields "a" (the start word) with
 * probability 3/3 next(); // yields "table" with probability 1/3 and "banana"
 * with probability "2/3" then the iterator is finished (the current word will
 * be null), since both "table" and "banana" appeared only at the end of
 * sentences.
 * <p>
 * The random choices are determined by a NumberGenerator.
 */
public class MarkovChain implements Iterator<String> {
    private NumberGenerator ng;
    final Map<String, ProbabilityDistribution<String>> chain;
    final ProbabilityDistribution<String> startWords;
    
    private String current = null;
    
    

    /**
     * No need to write any constructors. They are provided for you.
     */
    public MarkovChain() {
        this(new RandomNumberGenerator());
    }

    /**
     * No need to write any constructors. They are provided for you.
     *
     * @param ng - A (non-null) NumberGenerator used to walk through the MarkovChain
     */
    public MarkovChain(NumberGenerator ng) {
        if (ng == null) {
            throw new IllegalArgumentException("NumberGenerator input cannot be null");
        }
        this.chain = new TreeMap<String, ProbabilityDistribution<String>>();
        this.ng = ng;
        this.startWords = new ProbabilityDistribution<String>();
        reset();
    }

    /**
     * Adds a bigram to the Markov Chain dictionary. Note that the dictionary is a
     * field called chain of type {@code final Map<String, ProbabilityDistribution<String>> }.
     *
     * @param first  - The first word of the Bigram (should not be null)
     * @param second - The second word of the Bigram
     * @throws IllegalArgumentException if the first parameter is null.
     */
    void addBigram(String first, String second) {
        if (first == null) {
            throw new IllegalArgumentException();
        }
        
        if (chain.containsKey(first)) {
            ProbabilityDistribution<String> p = chain.get(first);
            p.record(second);
        } else {
            ProbabilityDistribution<String> n = new ProbabilityDistribution<String>();
            n.record(second);
            chain.put(first, n);
        }
        
    }

    /**
     * Adds a sentence's training data to the MarkovChain frequency information.
     * <p>
     * This method is meant to be called multiple times. Each call to this method
     * should provide this method with an Iterator that represents one sentence. If
     * we were to train a Markov Chain with four unique sentences, we would convert
     * each sentence into an iterator and call train() four times, once on each of
     * the iterators.
     * <p>
     * Look at the homework instructions for more details on bigrams. 
     * You should use addBigram() in this method.
     * <p>
     * Once you reach the last word of a sentence, add a bigram of that word and
     * null. This will teach the Markov Chain that that word can be used to end a
     * sentence.
     * <p>
     * Do nothing if the sentence is empty.
     *
     * @param sentence - an iterator representing one sentence of training data
     * @throws IllegalArgumentException if the sentence Iterator is null
     */
    public void train(Iterator<String> sentence) {
        String curr = null;
        if (sentence == null) {
            throw new IllegalArgumentException();
        }
        
        if (sentence.hasNext()) {
            curr = sentence.next();
            startWords.record(curr);
            
            while (sentence.hasNext()) {
                String s = sentence.next();
                addBigram(curr, s);
                curr = s;
            }
            addBigram(curr, null);
        }
    }

    /**
     * Returns the ProbabilityDistribution for a given token. Returns null if none
     * exists.
     *
     * @param token - the token for which the ProbabilityDistribution is sought
     * @return a ProbabilityDistribution or null
     */
    ProbabilityDistribution<String> get(String token) {
        return chain.get(token);
    }

    /**
     * Given a starting String, sets up the Iterator functionality such that: (1)
     * the Markov Chain will begin a walk at start. (2) the first call to next()
     * made after calling reset(start) will return start.
     * <p>
     * If start is null, then hasNext() should return false. start need not actually
     * be a part of the chain (but it should have no successor).
     *
     * @param start - the element that will be the first word in a walk on the
     *              Markov Chain.
     */
    public void reset(String start) {
        if (start != null) {
            current = start;
        }
    }

    /**
     * DO NOT EDIT THIS METHOD. WE COMPLETED IT FOR YOU.
     * <p>
     * Sets up the Iterator functionality with a random start word such that the
     * MarkovChain will now move along a walk beginning with that start word.
     * <p>
     * The first call to next() after calling reset() will return the random start
     * word selected by this call to reset().
     */
    public void reset() {
        if (startWords.getTotal() == 0) {
            reset(null);
        } else {
            reset(startWords.pick(ng));
        }
    }

    /**
     * This method should check if there is another word to retrieve from the Markov
     * Chain based on the current word of our walk.
     * <p>
     * Your solution should be very short.
     *
     * @return true if next() will return a String and false otherwise
     */
    @Override
    public boolean hasNext() {
        if (current == null) {
            return false;
        }
        return true;
    }

    /**
     * Returns: either
     * 1. the most recent word passed to reset() if reset was previously called
     * 2. a successor picked from the probability distribution associated with the most recently
     * returned from next()
     *
     * @return the next word in the MarkovChain (chosen at random via the number
     * generator if it is a successor)
     * @throws NoSuchElementException if there are no more words on the walk through
     *                                the chain.
     */
    @Override
    public String next() {
        String r = null;
        if (hasNext()) {
            try {
                r = current;
                if (chain.get(current) != null) {
                    current = chain.get(current).pick(ng);
                }
            } catch (NoSuchElementException e) {
                
            }
        }
        return r;
    }

    /**
     * Modifies all ProbabilityDistributions to output words in the order specified
     *
     * @param words - an ordered list of words that the distributions should generate
     * @throws IllegalArgumentException if there are less than 1 words fed in
     */
    public void fixDistribution(List<String> words) {
        fixDistribution(words, false);
    }

    /**
     * Modifies all ProbabilityDistributions to output words in the order specified
     *
     * @param words       - an ordered list of words that the distributions should generate
     * @param recordStart - whether or not to pick the first word from `startWords`
     * @throws IllegalArgumentException if there are less than 1 words fed in
     */
    public void fixDistribution(List<String> words, boolean recordStart) {
        if (words.size() < 1) {
            throw new IllegalArgumentException("must have words in order to fix distribution");
        }

        String curWord = words.remove(0);
        if (startWords.count(curWord) < 1) {
            throw new IllegalArgumentException("first word " + curWord + " not present in " +
                    "startWords");
        }

        List<Integer> probabilityNumbers = new LinkedList<>();
        if (recordStart) {
            probabilityNumbers.add(startWords.index(curWord));
        }

        while (words.size() > 0) {
            ProbabilityDistribution<String> curDistribution;
            // if we were just at null, reset. otherwise, continue on the chain
            if (curWord == null) {
                curDistribution = startWords;
            } else {
                curDistribution = chain.get(curWord);
            }

            String nextWord = words.remove(0);
            if (curDistribution.count(nextWord) < 1) {
                throw new IllegalArgumentException("word " + nextWord + " not found as a child of"
                        + " word " + curWord);
            }
            probabilityNumbers.add(curDistribution.index(nextWord));
            curWord = nextWord;
        }

        ng = new ListNumberGenerator(probabilityNumbers);
    }

    /**
     * Use this method to print out markov chains with words and probability distributions.
     */
    @Override
    public String toString() {
        String res = "";
        for (Map.Entry<String, ProbabilityDistribution<String>> c : chain.entrySet()) {
            res += (c.getKey() + ": " + c.getValue().toString() + "\n");
        }
        return res;
    }
}