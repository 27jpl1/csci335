package learning.classifiers;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleBiFunction;

// KnnTest.test() should pass once this is finished.
public class Knn<V, L> implements Classifier<V, L> {
    private ArrayList<Duple<V, L>> data = new ArrayList<>();
    private ToDoubleBiFunction<V, V> distance;
    private int k;

    public Knn(int k, ToDoubleBiFunction<V, V> distance) {
        this.k = k;
        this.distance = distance;
    }

    @Override
    public L classify(V value) {
        PriorityQueue<kNNClass<L>> queue = new PriorityQueue<>();
        for (Duple<V, L> data_piece: data) {
            queue.add(new kNNClass<>(distance.applyAsDouble(data_piece.getFirst(), value), data_piece.getSecond()));
        }
        Histogram<L> hist = new Histogram<>();
        for(int i = 0; i < k; i++){
            hist.bump(queue.remove().label);
        }
        return hist.getPluralityWinner();
    }

    @Override
    public void train(ArrayList<Duple<V, L>> training) {
        for(Duple<V,L> item: training){
            data.add(item);
        }
    }
}
