package learning.classifiers;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class NaiveBayes<V,L,F> implements Classifier<V,L> {
    // Each entry represents P(Feature | Label)
    // We want to know P(Label | Features). That means calculating P(Feature | Label) * P(Label) / P(Feature)
    // P(Feature) is invariant to the label and is generally ignored.
    // But we could do what we did with Markov chains, and calculate 1 / sum(P(Feature|Label)) for P(Label) / P(Feature)

    // Each entry represents P(Feature | Label)
    private LinkedHashMap<L,Histogram<F>> featuresByLabel = new LinkedHashMap<>();

    // Each entry represents P(Label)
    private Histogram<L> priors = new Histogram<>();

    // Given a value, this function returns a list of features and counts of those features.
    private Function<V,ArrayList<Duple<F,Integer>>> allFeaturesFrom;

    public NaiveBayes(Function<V,ArrayList<Duple<F,Integer>>> allFeaturesFrom) {
        this.allFeaturesFrom = allFeaturesFrom;
    }

    // In the training process, we accumulate data to calculate P(Feature), P(Label), and P(Feature | Label).
    // For each data item:
    // * Increment the prior count for the item's label.
    // * For each feature in the item (determined by calling allFeaturesFrom on the item's value)
    //   * Increment the feature count for the item's label by the number of appearances of the feature.
    @Override
    public void train(ArrayList<Duple<V, L>> data) {
        for(Duple<V,L> item: data){
            priors.bump(item.getSecond());
            for(Duple<F, Integer> feature: allFeaturesFrom.apply(item.getFirst())){
                if(!featuresByLabel.containsKey(item.getSecond())){
                    featuresByLabel.put(item.getSecond(), new Histogram<>());
                }
                featuresByLabel.get(item.getSecond()).bumpBy(feature.getFirst(), feature.getSecond());
            }
        }
    }

    // To classify:
    // * For each label:
    //   * Calculate the product of P(Label) * (P(Label | Feature) for all features)
    //     * In principle, we should divide by P(Feature). In practice, we don't, because it is the
    //       same value for all labels.
    // * Whichever label produces the highest product is the classification.
    @Override
    public L classify(V value) {
        double best_score = 0;
        L best_label = null;
        for(L label: featuresByLabel.keySet()){
            double p_label = (double) (priors.getCountFor(label) + 1) / (priors.getTotalCounts() + 1);
            for (Duple<F, Integer> feature: allFeaturesFrom.apply(value)){
                double p_label_feature = (double) (featuresByLabel.get(label).getCountFor(feature.getFirst()) + 1)/ (featuresByLabel.get(label).getTotalCounts() + 1);
                p_label *= p_label_feature;
            }
            if(p_label > best_score) {
                best_score = p_label;
                best_label = label;
            }
        }
        return best_label;
    }
}