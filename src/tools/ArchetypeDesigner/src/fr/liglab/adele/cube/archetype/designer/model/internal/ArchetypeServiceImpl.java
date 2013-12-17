package fr.liglab.adele.cube.archetype.designer.model.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeListener;
import fr.liglab.adele.cube.archetype.designer.model.ArchetypeServiceListener;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
import fr.liglab.adele.cube.archetype.designer.parser.ArchetypeParser;
import fr.liglab.adele.cube.archetype.designer.parser.ParseException;

public class ArchetypeServiceImpl implements IArchetypeService {

	private List<Archetype> archetypes = new ArrayList<Archetype>();
	
	private Archetype current = null;
	
	private List<ArchetypeServiceListener> listeners = new ArrayList<ArchetypeServiceListener>();
	
	
	@Override
	public Archetype newArchetype() {
		Archetype a = new Archetype();
		archetypes.add(a);
		return a;
	}

	@Override
	public Archetype loadArchetype(String filename) {
		if (filename != null) {
			Archetype a = null;
			try {
				a = ArchetypeParser.parse(new URL("file:" + filename));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (a != null) {
				a.setFilepath(filename);
			}
			return a;
		}
		return null;
	}
	
	@Override
	public Archetype getArchetype(String filepath) {
		for (Archetype a : this.archetypes) {
			if (a.getFilepath().equalsIgnoreCase(filepath)) {
				return a;
			}
		}
		return null;
	}

	
	public synchronized void addListener(ArchetypeServiceListener listener) {
		if (!this.listeners.contains(listener)) {			
			this.listeners.add(listener);
		}
	}
	
	public synchronized void removeListener(ArchetypeServiceListener listener) {
		this.listeners.remove(listener);
	}
	
	private synchronized void notifyListeners(int event, Object oldObj, Object newObj) {
		for (ArchetypeServiceListener l : this.listeners) {			
			l.notify(event, oldObj, newObj);
		}
	}
	
	
	@Override
	public boolean saveArchetype(Archetype arch) {		
		return false;
	}

	@Override
	public boolean closeArchetype(Archetype arch) {		
		return false;
	}

	@Override
	public List<Archetype> getArchetypes() {		
		return this.archetypes;
	}

	@Override
	public void setCurrentArchetype(Archetype a) {
		Archetype old = this.current;
		this.current = a;
		notifyListeners(ArchetypeServiceListener.CURRENT_ARCHETYPE_CHANGED, old, a);
	}
	
	public Archetype getCurrentArchetype() {
		return this.current;
	}
	
}
