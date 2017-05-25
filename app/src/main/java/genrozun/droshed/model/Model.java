package genrozun.droshed.model;

/**
 * Created by axelheine on 08/05/2017.
 */

public class Model {

    private Model() {

    }

    public static Model create(String modelName) {
        return new Model();
    }

    public int getColumnNumber() {
        return 8;
    }

    public int getLineNumber() {
        return 15;
    }
}
