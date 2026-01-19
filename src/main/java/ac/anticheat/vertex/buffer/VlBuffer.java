package ac.anticheat.vertex.buffer;

public class VlBuffer {
    private double vl;

    public void fail(double amount) {
        vl += amount;
    }

    public void decay(double decay) {
        vl = Math.max(0.0, vl - decay);
    }

    public double getVl() {
        return vl;
    }

    public void setVl(double vl) {
        this.vl = Math.max(0.0, vl);
    }
}
