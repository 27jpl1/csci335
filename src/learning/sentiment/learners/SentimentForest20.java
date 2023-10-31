package learning.sentiment.learners;

import learning.core.Histogram;
import learning.decisiontree.RandomForest;
import learning.sentiment.core.SentimentAnalyzer;

public class SentimentForest20 extends RandomForest<Histogram<String>, String, String, Integer> {
    public SentimentForest20() {
        super(20, SentimentAnalyzer::allFeatures, Histogram::getCountFor, i -> i + 1);
    }
}
