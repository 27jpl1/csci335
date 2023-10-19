package learning.classifiers;

public class kNNClass<L> implements Comparable<kNNClass<L>>{
    public Double distance;
    public L label;

    public kNNClass(Double distance, L label){
        this.distance = distance;
        this.label = label;
    }
    @Override
    public int compareTo(kNNClass<L> o) {
        if(distance > o.distance){
            return 1;
        }
        if(o.distance > distance)
            return -1;
        else {
            return 0;
        }
    }
}
