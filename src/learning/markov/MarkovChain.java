package learning.markov;

import learning.core.Histogram;

import java.util.*;

public class MarkovChain<L,S> {
    private LinkedHashMap<L, HashMap<Optional<S>, Histogram<S>>> label2symbol2symbol = new LinkedHashMap<>();

    public Set<L> allLabels() {return label2symbol2symbol.keySet();}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (L language: label2symbol2symbol.keySet()) {
            sb.append(language);
            sb.append('\n');
            for (Map.Entry<Optional<S>, Histogram<S>> entry: label2symbol2symbol.get(language).entrySet()) {
                sb.append("    ");
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue().toString());
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // Increase the count for the transition from prev to next.
    // Should pass SimpleMarkovTest.testCreateChains().
    public void count(Optional<S> prev, L label, S next) {
        if (label2symbol2symbol.containsKey(label)){
            if(label2symbol2symbol.get(label).containsKey(prev)){
                label2symbol2symbol.get(label).get(prev).bump(next);
            }
            else{
                label2symbol2symbol.get(label).put(prev, new Histogram<>());
                label2symbol2symbol.get(label).get(prev).bump(next);
            }
        }
        else{
            label2symbol2symbol.put(label, new HashMap<>());
            label2symbol2symbol.get(label).put(prev, new Histogram<>());
            label2symbol2symbol.get(label).get(prev).bump(next);
        }
    }

    // Returns P(sequence | label)
    // Should pass SimpleMarkovTest.testSourceProbabilities() and MajorMarkovTest.phraseTest()
    //
    // HINT: Be sure to add 1 to both the numerator and denominator when finding the probability of a
    // transition. This helps avoid sending the probability to zero.
    public double probability(ArrayList<S> sequence, L label) {
        double probabilities = 1;
        HashMap<Optional<S>, Histogram<S>> languageTable = label2symbol2symbol.get(label);
        Optional<S> previousSymbol = Optional.empty();
        for(int num = 0; num < sequence.size(); num++){
            if(languageTable.containsKey(previousSymbol)) {
                double PrevTotalCounts = languageTable.get(previousSymbol).getTotalCounts();
                double CurrCount = languageTable.get(previousSymbol).getCountFor(sequence.get(num));
                probabilities *= (CurrCount + 1) / (PrevTotalCounts + 1);
            }
            previousSymbol = Optional.of(sequence.get(num));
        }
        return probabilities;
    }

    // Return a map from each label to P(label | sequence).
    // Should pass MajorMarkovTest.testSentenceDistributions()
    public LinkedHashMap<L,Double> labelDistribution(ArrayList<S> sequence) { //this works just wrong numbers
        LinkedHashMap<L, Double> map = new LinkedHashMap<>();
        Set<L> labels = allLabels();
        L[] labelsArray = (L[]) allLabels().toArray();
        double[] probabilities = new double[labelsArray.length];
        double totalProbability = 0;
        for(int num = 0; num < labels.size(); num++){
            probabilities[num] = probability(sequence, labelsArray[num]);
        }
        for(int num = 0; num < labels.size(); num++){
            totalProbability += probabilities[num];
        }
        for(int num = 0; num < labels.size(); num++){
            map.put(labelsArray[num], (probabilities[num]/totalProbability));
        }
        System.out.println(Arrays.toString(probabilities));
        System.out.println(totalProbability);
        return map;
    }

    // Calls labelDistribution(). Returns the label with highest probability.
    // Should pass MajorMarkovTest.bestChainTest()
    public L bestMatchingChain(ArrayList<S> sequence) {
        LinkedHashMap<L, Double> map;
        map = labelDistribution(sequence);
        Set<L> labels = allLabels();
        L[] labelsArray = (L[]) allLabels().toArray();
        L bestLanguage = null;
        double bestProbability = 0;
        for(int num = 0; num < labels.size(); num++){
            L language = labelsArray[num];
            double probability = map.get(language);
            if(probability > bestProbability){
                bestProbability = probability;
                bestLanguage = language;
            }
        }
        return bestLanguage;
    }
}
