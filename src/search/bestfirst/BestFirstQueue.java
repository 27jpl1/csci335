package search.bestfirst;

import search.SearchNode;
import search.SearchQueue;

import java.util.*;
import java.util.function.ToIntFunction;

public class BestFirstQueue<T> implements SearchQueue<T> {
    private Comparator<SearchNode<T>> comparator = new Comparator<>() {
        @Override
        public int compare(SearchNode<T> o1, SearchNode<T> o2) {
            int calc1 = o1.getDepth() + heuristic.applyAsInt(o1.getValue());
            int calc2 = o2.getDepth() + heuristic.applyAsInt(o2.getValue());
            if (calc1 < calc2) {
                return -1;
            }
            if (calc1 > calc2) {
                return 1;
            }
            return 0;
        }

    };
    private java.util.PriorityQueue<SearchNode<T>> heap = new PriorityQueue<>(comparator);
    private HashMap<T, Integer> visited = new HashMap<>();
    private ToIntFunction<T> heuristic;
    public BestFirstQueue(ToIntFunction<T> heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public void enqueue(SearchNode<T> node) {
        int calc = heuristic.applyAsInt(node.getValue()) + node.getDepth();
        if(!visited.containsKey(node.getValue()) || visited.get(node.getValue()) > calc){
            visited.put(node.getValue(), calc);
            heap.add(node);
        }
    }

    @Override
    public Optional<SearchNode<T>> dequeue() {
        if (heap.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(heap.remove());
        }
    }
}
