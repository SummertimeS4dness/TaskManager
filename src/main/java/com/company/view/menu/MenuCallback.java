package com.company.view.menu;

import java.io.IOException;
import java.text.ParseException;
import java.util.EventListener;

/**
 * @brief A menu callback interface for invoking methods.
 * 
 * @author David MacDermot
 *
 * @date 02-07-2012
 * 
 * @bug
 */
public interface MenuCallback extends EventListener {
	public void Invoke() throws IOException, ParseException;
}
