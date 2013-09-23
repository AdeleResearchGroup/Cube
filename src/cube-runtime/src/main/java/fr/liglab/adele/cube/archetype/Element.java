package fr.liglab.adele.cube.archetype;

/**
 * User: debbabi
 * Date: 9/1/13
 * Time: 6:04 PM
 */
public abstract class Element {

    protected Archetype archetype;
    protected String id;
    private static int index = 0;

    public Element(Archetype archetype, String id) {
        this.archetype = archetype;
        if (id != null) this.id = id; else this.id = "__e"+index++;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public void setArchetype(Archetype archetype) {
        this.archetype = archetype;
    }
}
