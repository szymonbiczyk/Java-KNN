import java.io.FileNotFoundException;

public class Iris {
    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String irisVariant;

    public Iris(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String irisVariant) throws FileNotFoundException {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.irisVariant = irisVariant;
    }

    public String getIrisVariant() {
        return irisVariant;
    }

    public double getSepalLength() {
        return sepalLength;
    }

    public double getSepalWidth() {
        return sepalWidth;
    }

    public double getPetalLength() {
        return petalLength;
    }

    public double getPetalWidth() {
        return petalWidth;
    }

    @Override
    public String toString(){
        return "Sepal length:" + sepalLength + "cm Sepal Width:" + sepalWidth +
                "cm Petal length:" + petalLength + "cm Petal Width:" + petalWidth + "cm  Iris Variant: " + irisVariant;
    }
}

