package fr.liglab.adele.cube.archetype;

/**
 * User: debbabi
 * Date: 9/21/13
 * Time: 4:21 PM
 */
public enum ResolutionStrategy {

    Find(1), FindOrCreate(2), Create(3);

    private int value;

    private ResolutionStrategy(int value) {
        this.value = value;
    }

}
