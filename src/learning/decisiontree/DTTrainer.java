package learning.decisiontree;

import core.Duple;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DTTrainer<V,L, F, FV extends Comparable<FV>> {
	private ArrayList<Duple<V,L>> baseData;
	private boolean restrictFeatures;
	private Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures;
	private BiFunction<V,F,FV> getFeatureValue;
	private Function<FV,FV> successor;
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 boolean restrictFeatures, BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		baseData = data;
		this.restrictFeatures = restrictFeatures;
		this.allFeatures = allFeatures;
		this.getFeatureValue = getFeatureValue;
		this.successor = successor;
	}
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		this(data, allFeatures, false, getFeatureValue, successor);
	}


	public static <V,L, F, FV  extends Comparable<FV>> ArrayList<Duple<F,FV>>
	reducedFeatures(ArrayList<Duple<V,L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					int targetNumber) {
		ArrayList<Duple<F, FV>> features = allFeatures.apply(data);
		Collections.shuffle(features);
		while(features.size() > targetNumber){
			features.remove(0);
		}
		return features;
    }
	
	public DecisionTree<V,L,F,FV> train() {
		return train(baseData);
	}

	public static <V,L> int numLabels(ArrayList<Duple<V,L>> data) {
		return data.stream().map(Duple::getSecond).collect(Collectors.toUnmodifiableSet()).size();
	}
	
	private DecisionTree<V,L,F,FV> train(ArrayList<Duple<V,L>> data) {
		ArrayList<Duple<F, FV>> features;
		Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> best_split = null;
		F decision_feature = null;
		FV maxFeatureValue = null;
		double best_gain = - Double.MAX_VALUE;
		if (numLabels(data) == 1) {
			return new DTLeaf<>(data.get(0).getSecond());
		} else {
			if(!restrictFeatures){
				features = allFeatures.apply(data);
			}
			else{
				features = reducedFeatures(data, allFeatures, (int) Math.sqrt(data.size()));
			}
			for(Duple<F, FV> feature: features){
				Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> split = splitOn(data, feature.getFirst(), feature.getSecond(), getFeatureValue);
				double gain = gain(data, split.getFirst(), split.getSecond());
				if(gain > best_gain){
					best_split = split;
					decision_feature = feature.getFirst();
					maxFeatureValue = feature.getSecond();
					best_gain = gain;
				}
			}
			if(best_split.getFirst().size() == 0){
				L best_label = mostPopularLabelFrom(best_split.getSecond());
				return new DTLeaf<>(best_label);
			}
			if(best_split.getSecond().size() == 0){
				L best_label = mostPopularLabelFrom(best_split.getFirst());
				return new DTLeaf<>(best_label);
			}
			return new DTInterior<>(decision_feature, maxFeatureValue, train(best_split.getFirst()), train(best_split.getSecond()),getFeatureValue,successor);
		}		
	}

	public static <V,L> L mostPopularLabelFrom(ArrayList<Duple<V, L>> data) {
		Histogram<L> h = new Histogram<>();
		for (Duple<V,L> datum: data) {
			h.bump(datum.getSecond());
		}
		return h.getPluralityWinner();
	}
	public static <V,L> ArrayList<Duple<V,L>> resample(ArrayList<Duple<V,L>> data) {
		ArrayList<Duple<V,L>> new_data = new ArrayList<>();
		for(int i = 0; i < data.size(); i ++){
			int random = (int) (Math.random() * (data.size() - 1));
			new_data.add(data.get(random));
		}
		return new_data;
	}

	public static <V,L> double getGini(ArrayList<Duple<V,L>> data) {
		Histogram<L> hist = new Histogram<>();
		double gini = 0.0;
		for(Duple<V, L> item: data){
			hist.bump(item.getSecond());
		}
		for(L label: hist.stream().toList()){
			Double nume = (double) hist.getCountFor(label);
			Double denom = (double) hist.getTotalCounts();
			gini += (nume / denom) * (nume / denom);
		}
		return 1.0 - gini;
	}

	public static <V,L> double gain(ArrayList<Duple<V,L>> parent, ArrayList<Duple<V,L>> child1,
									ArrayList<Duple<V,L>> child2) {
		Double parent_gini = getGini(parent);
		Double child1_gini = getGini(child1);
		Double child2_gini = getGini(child2);
		return parent_gini - child1_gini - child2_gini;
	}

	public static <V,L, F, FV  extends Comparable<FV>> Duple<ArrayList<Duple<V,L>>,ArrayList<Duple<V,L>>> splitOn
			(ArrayList<Duple<V,L>> data, F feature, FV featureValue, BiFunction<V,F,FV> getFeatureValue) {
		ArrayList<Duple<V,L>> less = new ArrayList<>();
		ArrayList<Duple<V,L>> more = new ArrayList<>();
		for (Duple<V,L> item: data){
			if(getFeatureValue.apply(item.getFirst(),feature).compareTo(featureValue) > 0){
				more.add(item);
			}
			else{
				less.add(item);
			}
		}
		return new Duple<>(less, more);
	}
}
