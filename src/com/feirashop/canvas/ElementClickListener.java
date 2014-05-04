package com.feirashop.canvas;

/**
 * Listener para o evento {@link #onElementCLick}.
 * @author William A. Paula
 *
 */
public interface ElementClickListener {

	/**
	 * Evento disparado ao clicar em um elemento exibido no canvas
	 * @param element - código do elemento clicado
	 */
	public void onElementCLick(Element element);
	
}
