package de.dfki.revvisgdx.buttons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.dfki.revvisgdx.Button;

public class FunctionButton extends Button {
	Method toExecute;
	public FunctionButton(String filename, Method m) {
		super(filename);
		toExecute = m;
	}
	
	@Override
	public void Clicked() {
		try {
			this.toExecute.invoke(null, null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
