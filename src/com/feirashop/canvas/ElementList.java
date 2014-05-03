package com.feirashop.canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ElementList {
	
	private List<Element> elementList;
	
	public ElementList(){
		elementList = new ArrayList<Element>();
	}

	public List<Element> getElementList() {
		return elementList;
	}

	public void setStandList(List<Element> standList) {
		this.elementList = standList;
	}

	public void add(int index, Element element) {
		elementList.add(index, element);
	}

	public boolean add(Element e) {
		return elementList.add(e);
	}

	public void clear() {
		elementList.clear();
	}

	public Element get(int index) {
		return elementList.get(index);
	}

	public Iterator<Element> iterator() {
		return elementList.iterator();
	}

	public Element remove(int index) {
		return elementList.remove(index);
	}

	public boolean remove(Object o) {
		return elementList.remove(o);
	}

	public int size() {
		return elementList.size();
	}

	public Object[] toArray() {
		return elementList.toArray();
	}
		
}
